package com.openyogaland.denis.pranacoin_wallet_2_0.async;

import android.content.Context;
import androidx.annotation.NonNull;
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
import com.openyogaland.denis.pranacoin_wallet_2_0.application.Pranacoin_Wallet_2_0;
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnBalanceObtainedListener;

public class GetBalanceFromNetTask implements Listener<String>, ErrorListener,
                                       RequestFinishedListener<StringRequest>
{
  // constants
  private final static String GET_BALANCE_API = "http://95.213.191.196/api.php?action=getbalance&walletid=";
  // fields
  private RequestQueue              requestQueue;
  private OnBalanceObtainedListener onBalanceObtainedListener;
  
  /**
   * constructor
   * @param idOfUser - id of user
   */
  GetBalanceFromNetTask(@NonNull Context context, @NonNull String idOfUser)
  {
    if(Pranacoin_Wallet_2_0.stringNotEmpty(idOfUser))
    {
      String balanceUrl = GET_BALANCE_API + idOfUser;
      DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
          DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
      requestQueue = Volley.newRequestQueue(context);
      // try to get publicAddress from net
      StringRequest balanceRequest = new StringRequest(Method.GET, balanceUrl, this, this);
  
      if(requestQueue != null)
      {
        balanceRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(balanceRequest);
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
    if(Pranacoin_Wallet_2_0.stringNotEmpty(response) && (onBalanceObtainedListener != null))
    {
      onBalanceObtainedListener.onBalanceObtained(response);
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
  
  /**
   * setter
   * @param onBalanceObtainedListener listener
   */
  public void setOnBalanceObtainedListener(OnBalanceObtainedListener onBalanceObtainedListener)
  {
    this.onBalanceObtainedListener = onBalanceObtainedListener;
  }
}

