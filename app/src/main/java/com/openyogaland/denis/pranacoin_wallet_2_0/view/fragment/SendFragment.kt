package com.openyogaland.denis.pranacoin_wallet_2_0.view.fragment

import android.app.NotificationManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast.*
import androidx.core.app.NotificationCompat.Builder
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentIntegrator.forSupportFragment
import com.google.zxing.integration.android.IntentResult
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.hasConnection
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.log
import com.openyogaland.denis.pranacoin_wallet_2_0.async.SendSumTask
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnSendResponseObtainedListener
import com.openyogaland.denis.pranacoin_wallet_2_0.viewmodel.MainViewModel
import java.lang.Double.parseDouble

class
SendFragment : Fragment(), OnClickListener, OnSendResponseObtainedListener {
    private var idOfUser: String? = null
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
        mainViewModel.googleAccountId?.let { idOfUser ->
            log("SendFragment.onCreateView(): idOfUser = $idOfUser")
            this.idOfUser = idOfUser
        }
        sendButton.setOnClickListener(this)
        scanButton.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        context?.let { context: Context ->
            idOfUser?.let { idOfUser: String ->
                when (view.id) {
                    R.id.sendButton -> if (hasConnection(context)) {
                        // get address and amount to send
                        val recipientAddress = recipientAddressEditText?.text.toString()
                        val amount = sumEditText?.text.toString()
                        if (recipientAddress.isNotEmpty() && amount.isNotEmpty()) {
                            val balanceAmount = parseDouble(loadBalance())
                            val amountValue = parseDouble(amount)
                            val myCommissionAmountValue =
                                TOTAL_COMMISSION_MAX - 2 * API_COMMISSION_AMOUNT
                            // check if user has enough balance for transfer
                            if (amountValue + TOTAL_COMMISSION_MAX > balanceAmount) {
                                // show message that transfer wasn't performed
                                makeText(
                                    context, getString(R.string.not_enough_funds),
                                    LENGTH_LONG
                                ).show()
                            } else {
                                // execute users's transfer
                                val sendSumTask =
                                    SendSumTask(context, idOfUser, recipientAddress, amount)
                                sendSumTask.setOnSendResponseObtainedListener(this)
                                // execute my commission transfer
                                val myCommissionAmount = myCommissionAmountValue.toString()
                                SendSumTask(
                                    context,
                                    idOfUser,
                                    MY_COMMISSION_ADDRESS,
                                    myCommissionAmount
                                )
                                // show notification message if transfer has been successfully executed
                                showSuccessTransferNotification()
                            }
                        }
                    } else {
                        makeText(
                            context,
                            R.string.check_internet_connection,
                            LENGTH_LONG
                        ).show()
                    }
                    R.id.scanButton -> forSupportFragment(this).initiateScan()
                    else -> if (hasConnection(context)) {
                        val recipientAddress = recipientAddressEditText?.text.toString()
                        val amount = sumEditText?.text.toString()
                        if (recipientAddress.isNotEmpty() && amount.isNotEmpty()) {
                            val balanceAmount = parseDouble(loadBalance())
                            val amountValue = parseDouble(amount)
                            val myCommissionAmountValue =
                                TOTAL_COMMISSION_MAX - 2 * API_COMMISSION_AMOUNT
                            if (amountValue + TOTAL_COMMISSION_MAX > balanceAmount) {
                                makeText(
                                    context,
                                    getString(R.string.not_enough_funds),
                                    LENGTH_LONG
                                ).show()
                            } else {
                                val sendSumTask =
                                    SendSumTask(context, idOfUser, recipientAddress, amount)
                                sendSumTask.setOnSendResponseObtainedListener(this)
                                val myCommissionAmount = myCommissionAmountValue.toString()
                                SendSumTask(
                                    context,
                                    idOfUser,
                                    MY_COMMISSION_ADDRESS,
                                    myCommissionAmount
                                )
                                showSuccessTransferNotification()
                            }
                        }
                    } else {
                        makeText(
                            context,
                            R.string.check_internet_connection,
                            LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun showSuccessTransferNotification() {
        context?.let { context: Context ->
            idOfUser?.let { idOfUser: String ->
                // using idOfUser as channelId
                val notificationBuilder = Builder(context, idOfUser)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(getString(R.string.transfer_status))
                    .setContentText(getString(R.string.transfer_executed_successfully))
                    .setAutoCancel(true)
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            ?.let { result: IntentResult ->
                if (result.contents == null) {
                    makeText(context, R.string.scanning_cancelled, LENGTH_SHORT).show()
                }
                result.contents?.let { contents: String ->
                    view?.let { view: View ->
                        recipientAddressEditText =
                            view.findViewById(R.id.recipientAddressEditText)
                        recipientAddressEditText?.setText(contents)
                    }
                    makeText(context, contents, LENGTH_SHORT).show()
                }
            }
    }

    override fun onSendResponseObtained(response: String) {
        makeText(context, response, LENGTH_SHORT).show()
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
        const val NOTIFICATION_ID = 0
    }
}