package com.openyogaland.denis.pranacoin_wallet_2_0.view.fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.zxing.integration.android.IntentIntegrator.forSupportFragment
import com.google.zxing.integration.android.IntentIntegrator.parseActivityResult
import com.google.zxing.integration.android.IntentResult
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.hasConnection
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.log
import com.openyogaland.denis.pranacoin_wallet_2_0.view.dialog.AlertDialogUtil.showAlertDialog
import com.openyogaland.denis.pranacoin_wallet_2_0.viewmodel.MainViewModel
import java.lang.Double.parseDouble

class SendFragment : Fragment() {
    private var recipientAddressEditText: EditText? = null
    private var sumEditText: EditText? = null
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_send, container, false)
        recipientAddressEditText = view.findViewById(R.id.recipientAddressEditText)
        sumEditText = view.findViewById(R.id.sumEditText)
        val scanButton = view.findViewById<Button>(R.id.scanButton)
        val sendButton = view.findViewById<Button>(R.id.sendButton)
        scanButton.setOnClickListener { scanRecipientQRCode() }
        sendButton.setOnClickListener { sendPranacoins() }

        mainViewModel.sendPranacoinsTransactionLiveData.observe(
            viewLifecycleOwner,
            { transactionEventWrapper ->
                transactionEventWrapper
                    .getEventIfNotHandled()
                    ?.let { transaction ->
                        log("SendFragment.onCreateView(): transaction = $transaction")
                        FirebaseCrashlytics.getInstance().setCustomKey(TRANSACTION, transaction)

                        // TODO 0008-5 check ProgressDialog
                        /*activity?.supportFragmentManager?.let { fragmentManager ->
                            hideProgressDialog(fragmentManager)
                        }*/

                        context?.let { context: Context ->
                            showAlertDialog(
                                context,
                                getString(R.string.transfer_status),
                                getString(R.string.transfer_executed_successfully) +
                                        " transaction = $transaction"
                            )
                        }

                        // TODO 0012 show transaction history in HistoryFragment
                    }
            }
        )
        return view
    }

    private fun sendPranacoins() {
        val myCommissionAmountValue = TOTAL_COMMISSION_MAX - 2 * API_COMMISSION_AMOUNT
        val recipientAddress = recipientAddressEditText?.text.toString()
        val amount = sumEditText?.text.toString()
        FirebaseCrashlytics.getInstance().apply {
            setCustomKey(RECIPIENT_ADDRESS, recipientAddress)
            setCustomKey(AMOUNT, amount)
        }
        if (userHasEnoughBalanceForTransfer(recipientAddress, amount)) {
            context?.let { context: Context ->
                // TODO 0009 check internet connectivity
                if (hasConnection(context)) {

                    // TODO 0008-5 check ProgressDialog
                    /*activity?.supportFragmentManager?.let { fragmentManager ->
                        newProgressDialogInstance()
                            .setMessage("Дождитесь подтверждения сервера об отправке пранакоинов")
                            .show(fragmentManager)
                    }*/

                    mainViewModel.sendPranacoins(recipientAddress, amount)
                    mainViewModel.sendPranacoins(
                        MY_COMMISSION_ADDRESS, myCommissionAmountValue.toString()
                    )
                } else { // no internet connection
                    log(
                        "SendFragment.sendPranacoins(): " +
                                getString(R.string.check_internet_connection)
                    )
                    showAlertDialog(
                        requireContext(),
                        getString(R.string.error),
                        getString(R.string.check_internet_connection)
                    )
                }
            }
        } else { // not enough funds or empty field
            log(
                "SendFragment.sendPranacoins(): " +
                        getString(R.string.not_enough_funds_or_empty)
            )
            showAlertDialog(
                requireContext(),
                getString(R.string.error),
                getString(R.string.not_enough_funds_or_empty)
            )
        }
    }

    private fun userHasEnoughBalanceForTransfer(recipientAddress: String, amount: String): Boolean {
        var result = false
        if (recipientAddress.isNotEmpty() && amount.isNotEmpty()) {
            val balance = parseDouble(loadBalance())
            log("SendFragment.onCreateView(): balance = $balance")
            val amountValue = parseDouble(amount)
            if (amountValue + TOTAL_COMMISSION_MAX <= balance) {
                result = true
            }
        }
        return result
    }

    private fun scanRecipientQRCode() = forSupportFragment(this).initiateScan()

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        parseActivityResult(requestCode, resultCode, data)
            ?.let { result: IntentResult ->
                if (result.contents == null) {
                    log(
                        "SendFragment.onActivityResult(): " +
                                getString(R.string.scanning_cancelled)
                    )
                    showAlertDialog(
                        requireContext(),
                        getString(R.string.error),
                        getString(R.string.scanning_cancelled)
                    )
                }
                result.contents?.let { contents: String ->
                    view?.let { view: View ->
                        recipientAddressEditText = view.findViewById(R.id.recipientAddressEditText)
                        recipientAddressEditText?.setText(contents)
                    }
                    log(
                        "SendFragment.onActivityResult(): " +
                                getString(R.string.scanResult) +
                                contents
                    )
                    showAlertDialog(
                        requireContext(),
                        getString(R.string.success),
                        getString(R.string.scanResult) + contents
                    )
                }
            }
    }

    private fun loadBalance(): String {
        var result = ""
        activity
            ?.getPreferences(MODE_PRIVATE)
            ?.getString(BALANCE, "")
            ?.let { balance: String ->
                result = balance
            }
        return result
    }

    companion object {
        const val BALANCE = "balance"
        const val MY_COMMISSION_ADDRESS = "PBA8J5vGK4G8brcRehTiyxrw9JAHodCj65"
        const val TOTAL_COMMISSION_MAX = 0.1
        const val API_COMMISSION_AMOUNT = 0.001
        private const val RECIPIENT_ADDRESS = "recipient address"
        private const val AMOUNT = "amount"
        private const val TRANSACTION = "transaction"
    }
}