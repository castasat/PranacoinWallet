package com.openyogaland.denis.pranacoinwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SendFragment extends Fragment implements OnClickListener,
                                                      OnSendResponseObtainedListener
{
  // fields
  private Context  context;
  private String   idOfUser;
  private EditText recipientAddressEditText;
  private String   recipientAddress;
  private String   amount;
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_send, container, false);
    
    // find views by ids
    recipientAddressEditText = view.findViewById(R.id.recipientAddressEditText);
    EditText sumEditText = view.findViewById(R.id.sumEditText);
    Button   scanButton  = view.findViewById(R.id.scanButton);
    Button   sendButton  = view.findViewById(R.id.sendButton);
    
    // TODO commission
    
    context = getContext();
    if(context != null)
    {
      idOfUser = PranacoinWallet.getInstance(context).getIdOfUser();
    }
  
    recipientAddress = recipientAddressEditText.getText().toString();
    amount           = sumEditText.getText().toString();
  
    sendButton.setOnClickListener(this);
    scanButton.setOnClickListener(this);
    return view;
  }
  
  @Override
  public void onClick(View view)
  {
    switch (view.getId())
    {
      default:
      case R.id.sendButton:
        if (PranacoinWallet.hasConnection(context))
        {
          SendSumTask sendSumTask = new SendSumTask(context, idOfUser, recipientAddress, amount);
          sendSumTask.setOnSendResponseObtainedListener(this);
        }
        else
        {
          Toast.makeText(context, "Check internet connection", Toast.LENGTH_SHORT).show();
        }
        break;
      case R.id.scanButton:
        // TODO check
        IntentIntegrator.forSupportFragment(this).initiateScan();
        break;
    }
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if(result != null)
    {
      if(result.getContents() == null)
      {
        Toast.makeText(getContext(), "Cancelled by user", Toast.LENGTH_SHORT).show();
      }
      else
      {
        View view = getView();
        if(view != null)
        {
          recipientAddressEditText = view.findViewById(R.id.recipientAddressEditText);
          recipientAddressEditText.setText(result.getContents());
        }
        Toast.makeText(context, result.getContents(), Toast.LENGTH_SHORT).show();
      }
    }
  }
  
  @Override
  public void onSendResponseObtained(@NonNull String response)
  {
    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
  }
}