package com.openyogaland.denis.pranacoin_wallet_2_0.view.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.Window
import android.view.Window.FEATURE_NO_TITLE
import android.webkit.WebView
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.appcompat.app.AppCompatDialogFragment
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnPrivacyPolicyAcceptedListener

// TODO 0006 update policy
class PolicyFragment : AppCompatDialogFragment(), OnCheckedChangeListener, OnClickListener {
    // fields
    private var nextButton: Button? = null
    private var onPrivacyPolicyAcceptedListener: OnPrivacyPolicyAcceptedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View {
        val view = inflater.inflate(R.layout.dialog_fragment_privacy_policy, container, false)
        // find views by ids
        val webView = view.findViewById<WebView>(R.id.webView)
        val acceptPrivacyPolicyCheckBox =
            view.findViewById<CheckBox>(R.id.acceptPrivacyPolicyCheckBox)
        nextButton = view.findViewById(R.id.nextButton)
        // set listeners
        acceptPrivacyPolicyCheckBox.setOnCheckedChangeListener(this)
        nextButton!!.setOnClickListener(this)
        // load localized privacy policy
        webView.loadUrl(PRIVACY_POLICY_URL + getString(R.string.locale_asset_path) + PRIVACY_POLICY_NAME)
        return view
    }

    override fun onResume() {
        super.onResume()
        // expand the dialog width and height to the size of the window
        dialog
            ?.window
            ?.let { window: Window ->
                val params = window.attributes
                params.width = MATCH_PARENT
                params.height = MATCH_PARENT
                window.attributes = params
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // request a window without the title
        dialog.window?.let { window: Window ->
            window.requestFeature(FEATURE_NO_TITLE)
        }
        return dialog
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (buttonView.id == R.id.acceptPrivacyPolicyCheckBox) {
            nextButton?.isClickable = isChecked
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        activity?.finish()
    }

    override fun onClick(view: View) {
        if (view.id == R.id.nextButton) {
            onPrivacyPolicyAcceptedListener
                ?.onPrivacyPolicyAccepted(true)
            this.dismiss()
        }
    }

    // setter
    fun setOnPrivacyPolicyAcceptedListener(
        onPrivacyPolicyAcceptedListener
        : OnPrivacyPolicyAcceptedListener
    ) {
        this.onPrivacyPolicyAcceptedListener = onPrivacyPolicyAcceptedListener
    }

    companion object {
        const val PRIVACY_POLICY_URL = "file:///android_asset"
        const val PRIVACY_POLICY_NAME = "privacy_policy.html"
    }
}