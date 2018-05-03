package com.openyogaland.denis.pranacoinwallet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFinishedListener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

class GetPrivateAddressFromNetTask implements Listener<String>, ErrorListener,
                                              RequestFinishedListener<StringRequest>
{
  // constants
  private final static String LOG_TAG          = "PranaWallet";
  private final static String GET_PRIVADDR_API = "http://95.213.191.196/api.php?action=getprivaddr&walletid=";
  
  
  private RequestQueue                     requestQueue;
  private OnPrivateAddressObtainedListener onPrivateAddressObtainedListener;
  
  /**
   * constructor
   * @param idOfUser - id of user
   */
  GetPrivateAddressFromNetTask(@NonNull Context context, @Nullable String idOfUser)
  {
    if(stringNotEmpty(idOfUser))
    {
      String privateAddressUrl = GET_PRIVADDR_API + idOfUser;
  
      DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
          DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
      
      requestQueue = Volley.newRequestQueue(context);
  
      // try to get publicAddress from net
      StringRequest privateAddressRequest =
          new StringRequest(Method.GET, privateAddressUrl, this, this);
  
      if(requestQueue != null)
      {
        privateAddressRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(privateAddressRequest);
        requestQueue.addRequestFinishedListener(this);
      }
    }
  }
  
  /**
   * In case of response from executing RequestQueue
   * @param response the balance in a wallet
   */
  @Override
  public void onResponse(String response)
  {
    if(stringNotEmpty(response) && (onPrivateAddressObtainedListener != null))
    {
      Log.d(LOG_TAG, "onResponse privateAddress = " + response);
      onPrivateAddressObtainedListener.onPrivateAddressObtained(response);
    }
  }
  
  /**
   * In case of error response
   * @param error the cause of error response
   */
  @Override
  public void onErrorResponse(VolleyError error)
  {
    error.printStackTrace();
    Log.e(LOG_TAG, error.toString(), error);
  }
  
  private boolean stringNotEmpty(String string)
  {
    return ((string != null) && (!"".equals(string)));
  }
  
  /**
   * Called when a request has finished processing.
   */
  @Override
  public void onRequestFinished(Request<StringRequest> request)
  {
    requestQueue.stop();
  }
  
  /**
   * setter
   * @param onPrivateAddressObtainedListener listener
   */
  public void setOnPrivateAddressObtainedListener(OnPrivateAddressObtainedListener onPrivateAddressObtainedListener)
  {
    this.onPrivateAddressObtainedListener = onPrivateAddressObtainedListener;
  }
}
