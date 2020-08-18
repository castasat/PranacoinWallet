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
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnBalanceObtainedListener

class GetBalanceFromNetTask(context: Context, idOfUser: String) : Listener<String>, ErrorListener,
    RequestFinishedListener<StringRequest> {

    private var requestQueue: RequestQueue? = null
    private var onBalanceObtainedListener: OnBalanceObtainedListener? = null

    init {
        if (idOfUser.isNotEmpty()) {
            val balanceUrl = GET_BALANCE_API + idOfUser
            requestQueue = Volley.newRequestQueue(context)

            // try to get publicAddress from net
            val balanceRequest = StringRequest(GET, balanceUrl, this, this)

            if (requestQueue != null) {
                balanceRequest.retryPolicy = DefaultRetryPolicy(
                    DEFAULT_TIMEOUT_MS,
                    DEFAULT_MAX_RETRIES,
                    DEFAULT_BACKOFF_MULT
                )
                requestQueue?.add(balanceRequest)
                requestQueue?.addRequestFinishedListener(this)
            }
        }
    }

    override fun onResponse(response: String) {
        if (response.isNotEmpty() && onBalanceObtainedListener != null) {
            onBalanceObtainedListener?.onBalanceObtained(response)
        }
    }

    override fun onErrorResponse(error: VolleyError) {
        error.printStackTrace()
    }

    override fun onRequestFinished(request: Request<StringRequest>) {
        requestQueue?.stop()
    }

    fun setOnBalanceObtainedListener(onBalanceObtainedListener: OnBalanceObtainedListener) {
        this.onBalanceObtainedListener = onBalanceObtainedListener
    }

    companion object {
        const val GET_BALANCE_API = "http://95.213.191.196/api.php?action=getbalance&walletid="
    }
}

