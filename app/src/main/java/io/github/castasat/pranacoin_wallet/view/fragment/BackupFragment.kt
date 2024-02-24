package io.github.castasat.pranacoin_wallet.view.fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.zxing.WriterException
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import io.github.castasat.pranacoin_wallet.application.PranacoinWallet2.Companion.crashlytics
import io.github.castasat.pranacoin_wallet.application.PranacoinWallet2.Companion.log
import io.github.castasat.pranacoin_wallet.domain.QRCodeUtil.textToImageEncode
import io.github.castasat.pranacoin_wallet.view.dialog.AlertDialogUtil.showAlertDialog
import io.github.castasat.pranacoin_wallet.viewmodel.MainViewModel

class BackupFragment : Fragment() {
    private lateinit var privateKeyTextView: TextView
    private lateinit var privateKeyQRCodeImageView: ImageView
    private lateinit var privateKeyQRCodeProgressBar: ProgressBar
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_backup, container, false)
        val getPrivateKeyButton = view.findViewById<Button>(R.id.getPrivateKeyButton)
        privateKeyTextView = view.findViewById(R.id.privateKeyTextView)

        privateKeyQRCodeImageView = view.findViewById(R.id.privateKeyQRCodeImageView)
        privateKeyQRCodeProgressBar = view.findViewById(R.id.privateKeyQRCodeProgressBar)
        getPrivateKeyButton.setOnClickListener { getPrivateKey() }

        privateKeyQRCodeImageView.setOnLongClickListener { _ ->
            // TODO 0008-4 create context menu for QR-code
            log("BackupFragment.onCreateView(): Private key QR-code long-clicked")
            true
        }

        // setting progress bars visible and imageView not-visible
        privateKeyTextView.visibility = GONE
        privateKeyQRCodeImageView.visibility = GONE
        privateKeyQRCodeProgressBar.visibility = GONE

        mainViewModel.privateKeyLiveData.observe(
            viewLifecycleOwner,
            { privateKey ->
                savePrivateKey(privateKey)
                showPrivateKeyGroup(privateKey)
                log("BackupFragment.onCreateView(): privateKey = $privateKey")
                FirebaseCrashlytics.getInstance().setCustomKey(PRIVATE_KEY, privateKey)
            }
        )

        return view
    }

    private fun savePrivateKey(privateKey: String) {
        activity
            ?.getPreferences(MODE_PRIVATE)
            ?.edit()
            ?.let { editor: Editor ->
                editor.putString(PRIVATE_KEY, privateKey)
                editor.apply()
            }
    }

    private fun loadPrivateKey(): String {
        var result = ""
        activity
            ?.getPreferences(MODE_PRIVATE)
            ?.getString(PRIVATE_KEY, "")
            ?.let { privateKey: String ->
                result = privateKey
                FirebaseCrashlytics.getInstance().setCustomKey(PRIVATE_KEY, privateKey)
            }
        return result
    }

    private fun getPrivateKey() {
        privateKeyQRCodeProgressBar.visibility = VISIBLE

        // TODO 0009 check internet connectivity and load values from local database
        //  if not connected
        /*val privateKey = loadPrivateKey()
        showPrivateKeyGroup(privateKey)*/
        mainViewModel.getPrivateKey()
    }

    // TODO 0008 make QR-codes selectable
    private fun showPrivateKeyGroup(privateKey: String) {
        if (privateKey.isNotEmpty()) {
            privateKeyTextView.text = privateKey
            try {
                context?.let { context: Context ->
                    val bitmap = textToImageEncode(context, privateKey, true)
                    privateKeyQRCodeImageView.setImageBitmap(bitmap)
                }
            } catch (exception: WriterException) {
                log("BackupFragment.showPrivateKeyGroup(): could not create QR code")
                exception.printStackTrace()
                crashlytics(exception)
                showAlertDialog(
                    requireContext(),
                    ERROR,
                    getString(R.string.private_key_qr_generation_failed)
                )
            } finally {
                privateKeyQRCodeProgressBar.visibility = GONE
                privateKeyTextView.visibility = VISIBLE
                privateKeyQRCodeImageView.visibility = VISIBLE
            }
        }
    }

    companion object {
        const val PRIVATE_KEY = "private key"
        private const val ERROR = "error"
    }
}