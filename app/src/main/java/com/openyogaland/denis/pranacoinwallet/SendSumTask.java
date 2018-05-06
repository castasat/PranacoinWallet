package com.openyogaland.denis.pranacoinwallet;

import android.content.Context;
import android.support.annotation.NonNull;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue.RequestFinishedListener;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

class SendSumTask implements Listener<String>, ErrorListener, RequestFinishedListener<StringRequest>
{
  // constants
  private final static          String SEND_API      = "http://95.213.191.196/api.php?action=sendprana&walletid=";
  private final static          String RECIPIENT_API = "&recipientaddr=";
  private final static          String SUM_API       = "&sum=";
  // fields
  private RequestQueue                   requestQueue;
  private OnSendResponseObtainedListener onSendResponseObtainedListener;
  
  // constructor
  SendSumTask(@NonNull Context context, @NonNull String idOfUser, @NonNull
      String recipientAddress, @NonNull String amount)
  {
    String sendURL = SEND_API + idOfUser + RECIPIENT_API + recipientAddress + SUM_API + amount;
    DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    requestQueue = Volley.newRequestQueue(context);
    // try to send pranacoins to recipient
    StringRequest sendRequest = new StringRequest(Method.GET, sendURL, this, this);
  
    if (requestQueue != null)
    {
      sendRequest.setRetryPolicy(retryPolicy);
      requestQueue.add(sendRequest);
      requestQueue.addRequestFinishedListener(this);
    }
  }
  
  /**
   * In case of response from executing RequestQueue
   * @param response the balance in a wallet
   */
  @Override
  public void onResponse(String response)
  {
    if(PranacoinWallet.stringNotEmpty(response) && (onSendResponseObtainedListener != null))
    {
      onSendResponseObtainedListener.onSendResponseObtained(response);
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
  }
  
  /**
   * Called when a request has finished processing.
   */
  @Override
  public void onRequestFinished(Request<StringRequest> request)
  {
    requestQueue.stop();
  }
  
  public void setOnSendResponseObtainedListener(
      OnSendResponseObtainedListener onSendResponseObtainedListener)
  {
    this.onSendResponseObtainedListener = onSendResponseObtainedListener;
  }
}
