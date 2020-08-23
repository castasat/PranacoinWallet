package com.openyogaland.denis.pranacoin_wallet_2_0.view.fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.zxing.WriterException
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.crashlytics
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.log
import com.openyogaland.denis.pranacoin_wallet_2_0.domain.QRCodeUtil.textToImageEncode
import com.openyogaland.denis.pranacoin_wallet_2_0.view.dialog.AlertDialogUtil.showAlertDialog
import com.openyogaland.denis.pranacoin_wallet_2_0.viewmodel.MainViewModel

// TODO 0017 share publicAddress
// TODO 0018 create commercial
// TODO 0019 share commercial
// TODO 0020 rate application screen
// TODO 0021 statistics
// TODO 0022 english language
// TODO 0023 memory leaks
// TODO 0024 crashlytics
// TODO 0025 in-app pranacoin purchases
// TODO 0026 backend
// TODO 0027 other in-app purchases
// TODO 0028 user profile
// TODO 0029 user ads
// TODO 0030 viewing other user's profiles and ads
// TODO 0031 users geography
// TODO 0032 buying pranacoins from users
// TODO 0033 ESCROW service
class HomeFragment : Fragment() {
    private lateinit var publicAddressTextView: TextView
    private lateinit var balanceAmountTextView: TextView
    private lateinit var balanceProgressBar: ProgressBar
    private lateinit var publicAddressProgressBar: ProgressBar
    private lateinit var publicAddressQRCodeImageView: ImageView
    private lateinit var publicAddressQRCodeProgressBar: ProgressBar
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        publicAddressTextView = view.findViewById(R.id.publicAddressTextView)
        balanceAmountTextView = view.findViewById(R.id.balanceAmountTextView)
        balanceProgressBar = view.findViewById(R.id.balanceProgressBar)
        publicAddressProgressBar = view.findViewById(R.id.publicAddressProgressBar)
        publicAddressQRCodeImageView = view.findViewById(R.id.publicAddressQRCodeImageView)
        publicAddressQRCodeProgressBar = view.findViewById(R.id.publicAddressQRCodeProgressBar)

        // setting progress bars visible and imageView not-visible
        publicAddressProgressBar.visibility = VISIBLE
        balanceProgressBar.visibility = VISIBLE
        publicAddressQRCodeImageView.visibility = GONE
        publicAddressQRCodeProgressBar.visibility = VISIBLE

        publicAddressQRCodeImageView.setOnLongClickListener { _ ->
            // TODO 0008-2 create context menu for QR-code
            log("BackupFragment.onCreateView(): Public address QR-code long-clicked")
            true
        }

        mainViewModel.balanceLiveData.observe(
            viewLifecycleOwner,
            { balance ->
                saveBalance(balance)
                showBalance(balance)
                log("HomeFragment.onCreateView(): balance = $balance")
                FirebaseCrashlytics.getInstance().setCustomKey(BALANCE, balance)
            }
        )

        mainViewModel.publicAddressLiveData.observe(
            viewLifecycleOwner,
            { publicAddress ->
                savePublicAddress(publicAddress)
                showPublicAddress(publicAddress)
                showQRCode(publicAddress)
                log("HomeFragment.onCreateView(): publicAddress = $publicAddress")
                FirebaseCrashlytics.getInstance().setCustomKey(PUBLIC_ADDRESS, publicAddress)
            }
        )

        // TODO 0009 check internet connectivity and load values from local database
        //  if not connected
        mainViewModel.getBalance()
        mainViewModel.getPublicAddress()
        /*balance = loadBalance()
        showBalance(balance)
        publicAddress = loadPublicAddress()
        showPublicAddress(publicAddress)
        showQRCode(publicAddress)*/
        return view
    }

    private fun loadPublicAddress(): String {
        var result = ""
        activity
            ?.getPreferences(MODE_PRIVATE)
            ?.getString(PUBLIC_ADDRESS, "")
            ?.let { publicAddress: String ->
                result = publicAddress
                FirebaseCrashlytics.getInstance().setCustomKey(PUBLIC_ADDRESS, publicAddress)
            }
        return result
    }

    private fun loadBalance(): String {
        var result = ""
        activity
            ?.getPreferences(MODE_PRIVATE)
            ?.getString(BALANCE, "")
            ?.let { balance: String ->
                result = balance
                FirebaseCrashlytics.getInstance().setCustomKey(BALANCE, balance)
            }
        return result
    }

    private fun savePublicAddress(publicAddress: String) {
        activity
            ?.getPreferences(MODE_PRIVATE)
            ?.edit()
            ?.let { editor: Editor ->
                editor.putString(PUBLIC_ADDRESS, publicAddress)
                editor.apply()
            }
    }

    private fun saveBalance(balance: String) {
        activity
            ?.getPreferences(MODE_PRIVATE)
            ?.edit()
            ?.let { editor: Editor ->
                editor.putString(BALANCE, balance)
                editor.apply()
            }
    }

    private fun showPublicAddress(publicAddress: String) {
        if (publicAddress.isNotEmpty()) {
            publicAddressTextView.text = publicAddress
            publicAddressProgressBar.visibility = GONE
        }
    }

    private fun showBalance(balance: String) {
        if (balance.isNotEmpty()) {
            balanceAmountTextView.text = balance
            balanceProgressBar.visibility = GONE
        }
    }

    // TODO 0008 make QR-codes selectable
    private fun showQRCode(publicAddress: String) {
        if (publicAddress.isNotEmpty()) {
            try {
                context?.let { context: Context ->
                    val bitmap = textToImageEncode(context, publicAddress, false)
                    publicAddressQRCodeImageView.setImageBitmap(bitmap)
                    publicAddressQRCodeImageView.visibility = VISIBLE
                    publicAddressQRCodeProgressBar.visibility = GONE
                }
            } catch (exception: WriterException) {
                log("HomeFragment.showQRCode(): Could not create QR code for public address")
                exception.printStackTrace()
                crashlytics(exception)
                showAlertDialog(
                    requireContext(),
                    ERROR,
                    getString(R.string.public_address_qr_generation_failed)
                )
            }
        }
    }

    companion object {
        const val PUBLIC_ADDRESS = "public address"
        const val BALANCE = "balance"
        private const val ERROR = "error"
    }
}