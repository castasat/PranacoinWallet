package com.openyogaland.denis.pranacoin_wallet_2_0.async

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.DefaultRetryPolicy.*
import com.android.volley.Request
import com.android.volley.Request.Method
import com.android.volley.RequestQueue
import com.android.volley.RequestQueue.RequestFinishedListener
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnPublicAddressObtainedListener

class
GetPublicAddressFromNetTask(
    context: Context,
    idOfUser: String
) : Listener<String>,
    ErrorListener,
    RequestFinishedListener<StringRequest> {
    // fields
    private var requestQueue: RequestQueue? = null
    private var onPublicAddressObtainedListener: OnPublicAddressObtainedListener? = null

    init {
        if (idOfUser.isNotEmpty()) {
            requestQueue = Volley.newRequestQueue(context)
            // try to get publicAddress from net
            val publicAddressRequest = StringRequest(
                Method.GET,
                GET_ADDRESS_API + idOfUser,
                this, this
            )

            if (requestQueue != null) {
                publicAddressRequest.retryPolicy = DefaultRetryPolicy(
                    DEFAULT_TIMEOUT_MS,
                    DEFAULT_MAX_RETRIES,
                    DEFAULT_BACKOFF_MULT
                )
                requestQueue?.add(publicAddressRequest)
                requestQueue?.addRequestFinishedListener(this)
            }
        }
    }

    override fun onResponse(response: String) {
        if (response.isNotEmpty() && onPublicAddressObtainedListener != null) {
            onPublicAddressObtainedListener?.onPublicAddressObtained(response)
        }
    }

    override fun onErrorResponse(error: VolleyError) {
        error.printStackTrace()
    }

    override fun onRequestFinished(request: Request<StringRequest>) {
        requestQueue?.stop()
    }

    fun setOnPublicAddressObtainedListener(onPublicAddressObtainedListener: OnPublicAddressObtainedListener) {
        this.onPublicAddressObtainedListener = onPublicAddressObtainedListener
    }

    companion object {
        const val GET_ADDRESS_API = "http://95.213.191.196/api.php?action=getpubaddr&walletid="
    }
}

