package com.openyogaland.denis.pranacoin_wallet_2_0.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.crashlytics.android.Crashlytics;
import com.google.zxing.WriterException;
import com.openyogaland.denis.pranacoin_wallet_2_0.async.GetBalanceFromNetTask;
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnBalanceObtainedListener;
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnPublicAddressObtainedListener;
import com.openyogaland.denis.pranacoin_wallet_2_0.R;
import static com.openyogaland.denis.pranacoin_wallet_2_0.domain.QRCodeDomain.textToImageEncode;

public
class HomeFragment
extends Fragment
implements OnPublicAddressObtainedListener,
           OnBalanceObtainedListener
{
  // constants
  private final static String            PUBLIC_ADDRESS = "public address";
  private final static String            BALANCE        = "balance";
  // fields
  private              Context           context;
  private              SharedPreferences sharedPreferences;
  private              Editor            editor;
  private              String            publicAddress  = "";
  private              String            balance        = "";
  private              TextView          publicAddressTextView;
  private              TextView          balanceAmountTextView;
  private              ProgressBar       privateAddressQRCodeProgressBar;
  private              ProgressBar       publicAddressProgressBar;
  private              ImageView         publicAddressQRCodeImageView;
  private              ProgressBar       publicAddressQRCodeProgressBar;
  
  @Override
  @Nullable
  public
  View onCreateView(@NonNull LayoutInflater inflater,
                    @Nullable ViewGroup container,
                    @Nullable Bundle savedInstanceState)
  {
    // local variables
    GetBalanceFromNetTask getBalanceFromNetTask;
    
    // inflate fragment layout
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    
    // find views by ids
    publicAddressTextView = view.findViewById(R.id.publicAddressTextView);
    balanceAmountTextView = view.findViewById(R.id.balanceAmountTextView);
    privateAddressQRCodeProgressBar =
    view.findViewById(R.id.balanceProgressBar);
    publicAddressProgressBar = view.findViewById(R.id.publicAddressProgressBar);
    publicAddressQRCodeImageView =
    view.findViewById(R.id.publicAddressQRCodeImageView);
    publicAddressQRCodeProgressBar =
    view.findViewById(R.id.publicAddressQRCodeProgressBar);
    
    // setting progress bars visible and imageView not-visible
    publicAddressProgressBar.setVisibility(View.VISIBLE);
    privateAddressQRCodeProgressBar.setVisibility(View.VISIBLE);
    publicAddressQRCodeImageView.setVisibility(View.GONE);
    publicAddressQRCodeProgressBar.setVisibility(View.VISIBLE);
    
    // show public address and balance
    context = getContext();
    if(context != null)
    {
      /*TODO String idOfUser = PranacoinWallet2.getInstance(context).getIdOfUser();
      
      if(PranacoinWallet2.hasConnection(context))
      {
        GetPublicAddressFromNetTask getPublicAddressFromNetTask =
            new GetPublicAddressFromNetTask(context, idOfUser);
        getPublicAddressFromNetTask.setOnPublicAddressObtainedListener(this);
    
        getBalanceFromNetTask = new GetBalanceFromNetTask(context, idOfUser);
        getBalanceFromNetTask.setOnBalanceObtainedListener(this);
      }
      else if(!PranacoinWallet2.hasConnection(context))
      {
        balance = loadBalance();
        showBalance(balance);
        publicAddress = loadPublicAddress();
        showPublicAddress(publicAddress);
        showQRCode(publicAddress);
      }*/
    }
    return view;
  }
  
  private
  String loadPublicAddress()
  {
    Activity activity = getActivity();
    if(activity != null)
    {
      sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
      return sharedPreferences.getString(PUBLIC_ADDRESS, "");
    }
    return "";
  }
  
  private
  String loadBalance()
  {
    Activity activity = getActivity();
    if(activity != null)
    {
      sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
      return sharedPreferences.getString(BALANCE, "");
    }
    return "";
  }
  
  private
  void savePublicAddress(String publicAddress)
  {
    Activity activity = getActivity();
    if(activity != null)
    {
      sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
      editor = sharedPreferences.edit();
      editor.putString(PUBLIC_ADDRESS, publicAddress);
      editor.apply();
    }
  }
  
  private
  void saveBalance(String balance)
  {
    Activity activity = getActivity();
    if(activity != null)
    {
      sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
      editor = sharedPreferences.edit();
      editor.putString(BALANCE, balance);
      editor.apply();
    }
  }
  
  private
  void showPublicAddress(String publicAddress)
  {
    if(!publicAddress.isEmpty())
    {
      publicAddressTextView.setText(publicAddress);
      publicAddressProgressBar.setVisibility(View.GONE);
    }
  }
  
  private
  void showBalance(String balance)
  {
    if(!balance.isEmpty())
    {
      balanceAmountTextView.setText(balance);
      privateAddressQRCodeProgressBar.setVisibility(View.GONE);
    }
  }
  
  private
  void showQRCode(String publicAddress)
  {
    if(!publicAddress.isEmpty())
    {
      try
      {
        Bitmap bitmap = textToImageEncode(context, publicAddress, false);
        publicAddressQRCodeImageView.setImageBitmap(bitmap);
        publicAddressQRCodeImageView.setVisibility(View.VISIBLE);
        publicAddressQRCodeProgressBar.setVisibility(View.GONE);
      }
      catch(WriterException e)
      {
        //Crashlytics.logException(e);
        e.printStackTrace();
      }
    }
  }
  
  @Override
  public
  void onBalanceObtained(@NonNull String balance)
  {
    this.balance = balance;
    showBalance(balance);
    // save to SharedPreferences
    saveBalance(balance);
  }
  
  @Override
  public
  void onPublicAddressObtained(@NonNull String publicAddress)
  {
    this.publicAddress = publicAddress;
    showPublicAddress(publicAddress);
    showQRCode(publicAddress);
    // save to SharedPreferences
    savePublicAddress(publicAddress);
  }
}