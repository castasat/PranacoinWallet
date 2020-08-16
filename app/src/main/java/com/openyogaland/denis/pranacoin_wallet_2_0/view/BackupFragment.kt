package com.openyogaland.denis.pranacoin_wallet_2_0.view

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import com.google.zxing.WriterException
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.domain.QRCodeDomain.Companion.textToImageEncode
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnPrivateAddressObtainedListener

class
BackupFragment
    : Fragment(),
    OnPrivateAddressObtainedListener,
    OnClickListener {
    // fields
    private var privateAddressTextView: TextView? = null
    private var privateAddressQRCodeImageView: ImageView? = null
    private var privateAddressGroup: Group? = null
    private var privateAddressQRCodeProgressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View {
        // local variables
        val getPrivateAddressButton: Button

        // inflate fragment layout
        val view = inflater.inflate(R.layout.fragment_backup, container, false)

        // find views by ids
        getPrivateAddressButton = view.findViewById(R.id.getPrivateAddressButton)
        privateAddressTextView = view.findViewById(R.id.privateAddressTextView)
        privateAddressQRCodeImageView = view.findViewById(R.id.privateAddressQRCodeImageView)
        privateAddressGroup = view.findViewById(R.id.privateAddress)
        privateAddressQRCodeProgressBar = view.findViewById(R.id.privateAddressQRCodeProgressBar)

        // setting visibility
        privateAddressGroup?.visibility = Group.GONE
        privateAddressQRCodeProgressBar?.visibility = View.GONE

        // set button OnClickListener
        getPrivateAddressButton.setOnClickListener(this)

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

    override fun onPrivateAddressObtained(privateAddress: String) {
        savePrivateAddress(privateAddress)
        showPrivateAddressGroup(privateAddress)
    }

    override fun onClick(view: View) {
        // show public address and balance
        context?.let { context: Context ->

            privateAddressQRCodeProgressBar?.visibility = View.VISIBLE

            /*TODO String idOfUser = PranacoinWallet2.getInstance(context).getIdOfUser();

            if(PranacoinWallet2.hasConnection(context))
            {
              GetPrivateAddressFromNetTask getPrivateAddressFromNetTask =
                  new GetPrivateAddressFromNetTask(context, idOfUser);
              getPrivateAddressFromNetTask.setOnPrivateAddressObtainedListener(this);
            }
            else if(!PranacoinWallet2.hasConnection(context))
            {
              String privateAddress = loadPrivateAddress();
              showPrivateAddressGroup(privateAddress);
            }*/
        }
    }

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