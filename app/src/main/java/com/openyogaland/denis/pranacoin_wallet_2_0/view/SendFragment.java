package com.openyogaland.denis.pranacoin_wallet_2_0.view;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.app.NotificationCompat.Builder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.openyogaland.denis.pranacoin_wallet_2_0.async.SendSumTask;
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnSendResponseObtainedListener;
import com.openyogaland.denis.pranacoin_wallet_2_0.R;

import static com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.hasConnection;

public
class SendFragment
extends Fragment
implements OnClickListener,
           OnSendResponseObtainedListener
{
  // constants
  private final static String BALANCE               = "balance";
  private static final String MY_COMMISSION_ADDRESS =
  "PBA8J5vGK4G8brcRehTiyxrw9JAHodCj65";
  private static final double TOTAL_COMMISSION_MAX  = 0.1d;
  private static final double API_COMMISSION_AMOUNT = 0.001d;
  private static final int    NOTIFICATION_ID       = 0;
  
  // fields
  private Context  context;
  private String   idOfUser;
  private EditText recipientAddressEditText;
  private EditText sumEditText;
  
  @Override
  public
  View onCreateView(@NonNull LayoutInflater inflater,
                    ViewGroup container,
                    Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_send, container, false);
    
    // find views by ids
    recipientAddressEditText = view.findViewById(R.id.recipientAddressEditText);
    sumEditText = view.findViewById(R.id.sumEditText);
    Button scanButton = view.findViewById(R.id.scanButton);
    Button sendButton = view.findViewById(R.id.sendButton);
    
    // set idOfUser
    context = getContext();
    if(context != null)
    {
      // TODO idOfUser = PranacoinWallet2.getInstance(context).getIdOfUser();
    }
    
    // set listeners
    sendButton.setOnClickListener(this);
    scanButton.setOnClickListener(this);
    
    return view;
  }
  
  @Override
  public
  void onClick(View view)
  {
    switch(view.getId())
    {
      default:
      case R.id.sendButton:
        if(hasConnection(context))
        {
          // get address and amount to send
          String recipientAddress = recipientAddressEditText
          .getText()
          .toString();
          String amount = sumEditText
          .getText()
          .toString();
          
          if(!recipientAddress.isEmpty() && (!amount.isEmpty()))
          {
            double balanceAmount = Double.parseDouble(loadBalance());
            double amountValue = Double.parseDouble(amount);
            double myCommissionAmountValue =
            TOTAL_COMMISSION_MAX - (2 * API_COMMISSION_AMOUNT);
            
            // check if user has enough balance for transfer
            if((amountValue + TOTAL_COMMISSION_MAX) > balanceAmount)
            {
              // show message that transfer wasn't performed
              Toast
              .makeText(context,
                        getString(R.string.not_enough_funds),
                        Toast.LENGTH_LONG)
              .show();
            }
            else
            {
              // execute users's transfer
              SendSumTask sendSumTask =
              new SendSumTask(context, idOfUser, recipientAddress, amount);
              sendSumTask.setOnSendResponseObtainedListener(this);
              
              // execute my commission transfer
              String myCommissionAmount =
              String.valueOf(myCommissionAmountValue);
              new SendSumTask(context,
                              idOfUser,
                              MY_COMMISSION_ADDRESS,
                              myCommissionAmount);
              
              // show notification message if transfer has been successfully executed
              showSuccessTransferNotification();
            }
          }
        }
        else
        {
          Toast
          .makeText(context,
                    R.string.check_internet_connection,
                    Toast.LENGTH_SHORT)
          .show();
        }
        break;
      case R.id.scanButton:
        IntentIntegrator
        .forSupportFragment(this)
        .initiateScan();
        break;
    }
  }
  
  private
  void showSuccessTransferNotification()
  {
    // using idOfUser as channelId
    Builder notificationBuilder = new Builder(context, idOfUser)
    .setSmallIcon(R.mipmap.ic_launcher_round)
    .setContentTitle(getString(R.string.transfer_status))
    .setContentText(getString(R.string.transfer_executed_successfully))
    .setAutoCancel(true);
    NotificationManager notificationManager =
    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    if(notificationManager != null)
    {
      notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
  }
  
  @Override
  public
  void onActivityResult(int requestCode,
                        int resultCode,
                        Intent data)
  {
    IntentResult result =
    IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if(result != null)
    {
      if(result.getContents() == null)
      {
        Toast
        .makeText(getContext(), R.string.scanning_cancelled, Toast.LENGTH_SHORT)
        .show();
      }
      else
      {
        View view = getView();
        if(view != null)
        {
          recipientAddressEditText =
          view.findViewById(R.id.recipientAddressEditText);
          recipientAddressEditText.setText(result.getContents());
        }
        Toast
        .makeText(context, result.getContents(), Toast.LENGTH_SHORT)
        .show();
      }
    }
  }
  
  @Override
  public
  void onSendResponseObtained(@NonNull String response)
  {
    Toast
    .makeText(context, response, Toast.LENGTH_SHORT)
    .show();
  }
  
  private
  String loadBalance()
  {
    Activity activity = getActivity();
    if(activity != null)
    {
      SharedPreferences sharedPreferences =
      activity.getPreferences(Context.MODE_PRIVATE);
      return sharedPreferences.getString(BALANCE, "");
    }
    return "";
  }
}