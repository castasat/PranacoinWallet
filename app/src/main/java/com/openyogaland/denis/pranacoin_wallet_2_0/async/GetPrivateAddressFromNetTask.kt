package com.openyogaland.denis.pranacoin_wallet_2_0.async

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
import com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES
import com.android.volley.DefaultRetryPolicy.DEFAULT_TIMEOUT_MS
import com.android.volley.Request
import com.android.volley.Request.Method
import com.android.volley.Request.Method.GET
import com.android.volley.RequestQueue
import com.android.volley.RequestQueue.RequestFinishedListener
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnPrivateAddressObtainedListener

class
GetPrivateAddressFromNetTask(context : Context,
                             idOfUser : String)
  : Listener<String>,
    ErrorListener,
    RequestFinishedListener<StringRequest>
{
  // fields
  private var requestQueue : RequestQueue? = null
  private var onPrivateAddressObtainedListener : OnPrivateAddressObtainedListener? = null
  
  init
  {
    if(idOfUser.isNotEmpty())
    {
      val privateAddressUrl = GET_PRIVADDR_API + idOfUser
      
      requestQueue = Volley.newRequestQueue(context)
      
      // try to get publicAddress from net
      val privateAddressRequest = StringRequest(GET, privateAddressUrl, this, this)
      
      if(requestQueue != null)
      {
        privateAddressRequest.retryPolicy = DefaultRetryPolicy(DEFAULT_TIMEOUT_MS,
                                                               DEFAULT_MAX_RETRIES,
                                                               DEFAULT_BACKOFF_MULT)
        requestQueue?.add(privateAddressRequest)
        requestQueue?.addRequestFinishedListener(this)
      }
    }
  }
  
  override fun onResponse(response : String)
  {
    if(response.isNotEmpty() && onPrivateAddressObtainedListener != null)
    {
      onPrivateAddressObtainedListener?.onPrivateAddressObtained(response)
    }
  }
  
  override fun onErrorResponse(error : VolleyError)
  {
    error.printStackTrace()
  }
  
  override fun onRequestFinished(request : Request<StringRequest>)
  {
    requestQueue?.stop()
  }
  
  fun setOnPrivateAddressObtainedListener(onPrivateAddressObtainedListener : OnPrivateAddressObtainedListener)
  {
    this.onPrivateAddressObtainedListener = onPrivateAddressObtainedListener
  }
  
  companion object
  {
    const val GET_PRIVADDR_API = "http://95.213.191.196/api.php?action=getprivaddr&walletid="
  }
}

