@file:Suppress("unused", "RedundantOverride", "UnusedImport")

package com.openyogaland.denis.pranacoin_wallet_2_0.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity.BOTTOM
import android.view.Gravity.CENTER
import android.view.View.GONE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.crashlytics
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.log

class ProgressDialog private constructor() : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setStyle(STYLE_NORMAL, R.style.BottomSheetDialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState)
            .apply {
                /*View.inflate(context, R.layout.progress_dialog, null)
                    .apply {
                        val messageTextView = findViewById<TextView>(R.id.messageTextView)
                        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                        setupProgressBar(messageTextView, progressBar)
                        setupMessage(messageTextView)
                        setContentView(this)
                    }*/
                window?.apply {
                    setLayout(MATCH_PARENT, MATCH_PARENT)
                    setGravity(BOTTOM)
                }
            }

    private fun setupProgressBar(textViewToHide: TextView?, progressBar: ProgressBar?) {
        textViewToHide?.visibility = GONE
        progressBar?.layoutParams =
            LayoutParams(MATCH_PARENT, MATCH_PARENT)
                .also { layoutParams ->
                    layoutParams.gravity = CENTER
                }
    }

    private fun setupMessage(messageTextView: TextView?) {
        messageTextView?.apply {
            arguments?.apply {
                text = when {
                    containsKey(MESSAGE) ->
                        getCharSequence(MESSAGE)
                    containsKey(MESSAGE_RESOURCE) ->
                        resources.getText(getInt(MESSAGE_RESOURCE))
                    else ->
                        resources.getString(R.string.defaultProgressBarText)
                }
            }
        }
    }

    fun setMessage(message: CharSequence): ProgressDialog {
        arguments?.putCharSequence(MESSAGE, message)
        return this
    }

    fun setMessage(messageResource: Int): ProgressDialog {
        arguments?.putInt(MESSAGE_RESOURCE, messageResource)
        return this
    }

    fun show(supportFragmentManager: FragmentManager): ProgressDialog {
        with(supportFragmentManager) {
            try {
                beginTransaction()
                    .add(this@ProgressDialog, TAG)
                    .commitNowAllowingStateLoss()
            } catch (error: IllegalStateException) {
                log("ProgressDialog.show(): error = $error")
                error.printStackTrace()
                crashlytics(error)
                beginTransaction()
                    .add(this@ProgressDialog, TAG)
                    .commitAllowingStateLoss()
            }
        }
        return this
    }

    fun isShowing() = dialog?.isShowing

    companion object {
        private const val TAG = "ProgressDialog"
        private const val MESSAGE = "message"
        private const val MESSAGE_RESOURCE = "message_resource"

        fun newProgressDialogInstance() =
            ProgressDialog()
                .apply {
                    retainInstance = true
                    isCancelable = false
                    arguments = Bundle()
                }

        fun hideProgressDialog(supportFragmentManager: FragmentManager) {
            with(supportFragmentManager) {
                findFragmentByTag(TAG)
                    ?.let { fragment ->
                        try {
                            beginTransaction()
                                .remove(fragment)
                                .commitNowAllowingStateLoss()
                        } catch (error: IllegalStateException) {
                            log("ProgressDialog.hide(): error = $error")
                            error.printStackTrace()
                            crashlytics(error)
                            beginTransaction()
                                .remove(fragment)
                                .commitAllowingStateLoss()
                        }
                    }
            }
        }
    }
}