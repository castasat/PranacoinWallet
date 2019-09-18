package com.openyogaland.denis.pranacoinwallet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.zxing.WriterException;

public class BackupFragment extends Fragment implements OnPrivateAddressObtainedListener,
                                                        OnClickListener
{
  // constants
  private final static String PRIVATE_ADDRESS = "private address";
  // fields
  private Context           context;
  private SharedPreferences sharedPreferences;
  private TextView          privateAddressTextView;
  private ImageView         privateAddressQRCodeImageView;
  private Group             privateAddressGroup;
  private ProgressBar       privateAddressQRCodeProgressBar;
  
  @Override @Nullable
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState)
  {
    // local variables
    Button getPrivateAddressButton;
    
    // inflate fragment layout
    View view = inflater.inflate(R.layout.fragment_backup, container, false);
    
    // find views by ids
    getPrivateAddressButton         = view.findViewById(R.id.getPrivateAddressButton);
    privateAddressTextView          = view.findViewById(R.id.privateAddressTextView);
    privateAddressQRCodeImageView   = view.findViewById(R.id.privateAddressQRCodeImageView);
    privateAddressGroup             = view.findViewById(R.id.privateAddress);
    privateAddressQRCodeProgressBar = view.findViewById(R.id.privateAddressQRCodeProgressBar);
    
    // setting visibility
    privateAddressGroup.setVisibility(Group.GONE);
    privateAddressQRCodeProgressBar.setVisibility(View.GONE);
    
    // set button OnClickListener
    getPrivateAddressButton.setOnClickListener(this);
    
    return view;
  }
  
  private void savePrivateAddress(String privateAddress)
  {
    Activity activity = getActivity();
    if(activity != null)
    {
      sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
      Editor editor = sharedPreferences.edit();
      editor.putString(PRIVATE_ADDRESS, privateAddress);
      editor.apply();
    }
  }
  
  private String loadPrivateAddress()
  {
    Activity activity = getActivity();
    if(activity != null)
    {
      sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
      return sharedPreferences.getString(PRIVATE_ADDRESS, "");
    }
    return "";
  }
  
  @Override
  public void onPrivateAddressObtained(@NonNull String privateAddress)
  {
    // save to SharedPreferences
    savePrivateAddress(privateAddress);
    showPrivateAddressGroup(privateAddress);
  }
  
  /**
   * Called when a view has been clicked.
   * @param view The view that was clicked.
   */
  @Override
  public void onClick(View view)
  {
    // show public address and balance
    context = getContext();
    if(context != null)
    {
      privateAddressQRCodeProgressBar.setVisibility(View.VISIBLE);
      String idOfUser = PranacoinWallet.getInstance(context).getIdOfUser();
    
      if(PranacoinWallet.hasConnection(context))
      {
        GetPrivateAddressFromNetTask getPrivateAddressFromNetTask =
            new GetPrivateAddressFromNetTask(context, idOfUser);
        getPrivateAddressFromNetTask.setOnPrivateAddressObtainedListener(this);
      }
      else if(!PranacoinWallet.hasConnection(context))
      {
        String privateAddress = loadPrivateAddress();
        showPrivateAddressGroup(privateAddress);
      }
    }
  }
  
  private void showPrivateAddressGroup(String privateAddress)
  {
    if(PranacoinWallet.stringNotEmpty(privateAddress))
    {
      privateAddressTextView.setText(privateAddress);
      try
      {
        Bitmap bitmap = PranacoinWallet.getInstance(context).textToImageEncode(privateAddress,true);
        privateAddressQRCodeImageView.setImageBitmap(bitmap);
      }
      catch(WriterException e)
      {
        Crashlytics.logException(e);
        e.printStackTrace();
      }
      finally
      {
        privateAddressQRCodeProgressBar.setVisibility(View.GONE);
        privateAddressTextView.setVisibility(View.VISIBLE);
        privateAddressQRCodeImageView.setVisibility(View.VISIBLE);
        privateAddressGroup.setVisibility(Group.VISIBLE);
      }
    }
  }
}