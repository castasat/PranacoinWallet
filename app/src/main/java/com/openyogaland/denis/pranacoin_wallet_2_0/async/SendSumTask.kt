package com.openyogaland.denis.pranacoin_wallet_2_0.async

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.DefaultRetryPolicy.*
import com.android.volley.Request
import com.android.volley.Request.Method.GET
import com.android.volley.RequestQueue
import com.android.volley.RequestQueue.RequestFinishedListener
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnSendResponseObtainedListener

class
SendSumTask(
    context: Context, idOfUser: String, recipientAddress: String, amount: String
) : Listener<String>, ErrorListener, RequestFinishedListener<StringRequest> {

    private val requestQueue: RequestQueue?
    private var onSendResponseObtainedListener: OnSendResponseObtainedListener? = null

    init {
        val sendURL = SEND_API + idOfUser + RECIPIENT_API + recipientAddress + SUM_API + amount
        val retryPolicy = DefaultRetryPolicy(
            DEFAULT_TIMEOUT_MS,
            DEFAULT_MAX_RETRIES,
            DEFAULT_BACKOFF_MULT
        )
        requestQueue = Volley.newRequestQueue(context)
        // try to send pranacoins to recipient
        val sendRequest = StringRequest(GET, sendURL, this, this)

        if (requestQueue != null) {
            sendRequest.retryPolicy = retryPolicy
            requestQueue.add(sendRequest)
            requestQueue.addRequestFinishedListener(this)
        }
    }

    override fun onResponse(response: String) {
        if (response.isNotEmpty() && onSendResponseObtainedListener != null) {
            onSendResponseObtainedListener?.onSendResponseObtained(response)
        }
    }

    override fun onErrorResponse(error: VolleyError) {
        error.printStackTrace()
    }

    override fun onRequestFinished(request: Request<StringRequest>) {
        requestQueue?.stop()
    }

    fun setOnSendResponseObtainedListener(
        onSendResponseObtainedListener: OnSendResponseObtainedListener
    ) {
        this.onSendResponseObtainedListener = onSendResponseObtainedListener
    }

    companion object {
        const val SEND_API = "http://95.213.191.196/api.php?action=sendprana&walletid="
        const val RECIPIENT_API = "&recipientaddr="
        const val SUM_API = "&sum="
    }
}