package com.openyogaland.denis.pranacoin_wallet_2_0.view.fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.zxing.WriterException
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.log
import com.openyogaland.denis.pranacoin_wallet_2_0.domain.QRCodeDomain.Companion.textToImageEncode
import com.openyogaland.denis.pranacoin_wallet_2_0.viewmodel.MainViewModel

class BackupFragment : Fragment() {
    private var privateAddressTextView: TextView? = null
    private var privateAddressQRCodeImageView: ImageView? = null
    private var privateAddressGroup: Group? = null
    private var privateAddressQRCodeProgressBar: ProgressBar? = null
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_backup, container, false)
        val getPrivateAddressButton = view.findViewById<Button>(R.id.getPrivateAddressButton)
        privateAddressTextView = view.findViewById(R.id.privateAddressTextView)
        privateAddressQRCodeImageView = view.findViewById(R.id.privateAddressQRCodeImageView)
        privateAddressGroup = view.findViewById(R.id.privateAddress)
        privateAddressQRCodeProgressBar = view.findViewById(R.id.privateAddressQRCodeProgressBar)

        getPrivateAddressButton.setOnClickListener { getPrivateAddress() }

        // setting progress bars visible and imageView not-visible
        privateAddressGroup?.visibility = Group.GONE
        privateAddressQRCodeProgressBar?.visibility = View.GONE

        mainViewModel.privateAddressLiveData.observe(
            viewLifecycleOwner,
            { privateAddress ->
                savePrivateAddress(privateAddress)
                showPrivateAddressGroup(privateAddress)
                log("BackupFragment.onCreateView(): privateAddress = $privateAddress")
            }
        )

        return view
    }

    private fun savePrivateAddress(privateAddress: String) {
        activity
            ?.getPreferences(MODE_PRIVATE)
            ?.edit()
            ?.let { editor: Editor ->
                editor.putString(PRIVATE_ADDRESS, privateAddress)
                editor.apply()
            }
    }

    private fun loadPrivateAddress(): String {
        var result = ""
        activity
            ?.getPreferences(MODE_PRIVATE)
            ?.getString(PRIVATE_ADDRESS, "")
            ?.let { privateAddress: String ->
                result = privateAddress
            }
        return result
    }

    private fun getPrivateAddress() {
        privateAddressQRCodeProgressBar?.visibility = View.VISIBLE

        // TODO 0009 check internet connectivity and load values from local database
        //  if not connected
        /*val privateAddress = loadPrivateAddress()
        showPrivateAddressGroup(privateAddress)*/

        mainViewModel.getPrivateAddress()
    }

    // TODO 0008 make QR-codes selectable
    private fun showPrivateAddressGroup(privateAddress: String) {
        if (privateAddress.isNotEmpty()) {
            privateAddressTextView?.text = privateAddress
            try {
                context?.let { context: Context ->
                    val bitmap = textToImageEncode(context, privateAddress, true)
                    privateAddressQRCodeImageView?.setImageBitmap(bitmap)
                }
            } catch (e: WriterException) {
                e.printStackTrace()
            } finally {
                privateAddressQRCodeProgressBar?.visibility = View.GONE
                privateAddressTextView?.visibility = View.VISIBLE
                privateAddressQRCodeImageView?.visibility = View.VISIBLE
                privateAddressGroup?.visibility = Group.VISIBLE
            }
        }
    }

    companion object {
        const val PRIVATE_ADDRESS = "private address"
    }
}