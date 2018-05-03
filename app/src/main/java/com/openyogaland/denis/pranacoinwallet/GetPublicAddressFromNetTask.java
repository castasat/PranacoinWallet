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

class GetPublicAddressFromNetTask implements Listener<String>, ErrorListener,
                                             RequestFinishedListener<StringRequest>
{
  // constants
  private final static String LOG_TAG         = "PranaWallet";
  private final static String GET_ADDRESS_API = "http://95.213.191.196/api.php?action=getpubaddr&walletid=";
  
  
  private RequestQueue                    requestQueue;
  private OnPublicAddressObtainedListener onPublicAddressObtainedListener;
  
  /**
   * constructor
   * @param idOfUser - id of user
   */
  GetPublicAddressFromNetTask(@NonNull Context context, @Nullable String idOfUser)
  {
    log("GetPublicAddressFromNetTask constructor idOfUser = " + idOfUser);
    if (stringNotEmpty(idOfUser))
    {
      String publicAddressUrl = GET_ADDRESS_API + idOfUser;
      DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
          DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
      requestQueue = Volley.newRequestQueue(context);
      // try to get publicAddress from net
      StringRequest publicAddressRequest =
          new StringRequest(Method.GET, publicAddressUrl, this, this);
      
      if(requestQueue != null)
      {
        publicAddressRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(publicAddressRequest);
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
    log("GetPublicAddressFromNetTask onResponse() response = " + response);
    if(stringNotEmpty(response) && (onPublicAddressObtainedListener != null))
    {
      onPublicAddressObtainedListener.onPublicAddressObtained(response);
    }
  }
  
  /**
   * In case of error response
   * @param error the cause of error response
   */
  @Override
  public void onErrorResponse(VolleyError error)
  {
    log("GetPublicAddressFromNetTask onErrorResponse() error = " + error.toString());
    error.printStackTrace();
    Log.e(LOG_TAG, error.toString(), error);
  }
  
  private boolean stringNotEmpty(String string)
  {
    log("GetPublicAddressFromNetTask stringNotEmpty() string = " + string);
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
   * @param onPublicAddressObtainedListener listener
   */
  public void setOnPublicAddressObtainedListener(OnPublicAddressObtainedListener onPublicAddressObtainedListener)
  {
    this.onPublicAddressObtainedListener = onPublicAddressObtainedListener;
  }
  
  private void log(String message)
  {
    Log.d(LOG_TAG, message);
  }
}
