package com.openyogaland.denis.pranacoinwallet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class HomeFragment extends Fragment implements OnPublicAddressObtainedListener,
                                                      OnBalanceObtainedListener,
                                                      OnPrivateAddressObtainedListener 
{
  // constants
  private final static String PUBLIC_ADDRESS    = "public address";
  private final static String BALANCE           = "balance";
  private final static String PRIVATE_ADDRESS   = "private address";
  private final static int    QR_CODE_DIMENSION = 500;
  
  private String                       idOfUser;
  private SharedPreferences            sharedPreferences;
  private Editor                       editor;
  private String                       publicAddress  = "";
  private String                       balance        = "";
  private TextView                     publicAddressTextView;
  private TextView                     balanceAmountTextView;
  private ProgressBar                  balanceProgressBar;
  private ProgressBar                  addressProgressBar;
  private int                          blackColor;
  private int                          whiteColor;
  private ImageView                    addressQRCodeImageView;
  private ProgressBar                  imagePublicProgressBar;
  
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState)
  {
    GetBalanceFromNetTask getBalanceFromNetTask;
    
    View view = inflater.inflate(R.layout.fragment_home, container, false);
  
    Context context = getContext();
    if (context != null)
    {
      idOfUser = PranacoinWallet.getInstance(context).getIdOfUser();
    }
    
    // find views by ids
    publicAddressTextView = view.findViewById(R.id.publicAddressTextView);
    balanceAmountTextView = view.findViewById(R.id.balanceAmountTextView);
    balanceProgressBar = view.findViewById(R.id.balanceProgressBar);
    addressProgressBar = view.findViewById(R.id.addressProgressBar);
    addressQRCodeImageView = view.findViewById(R.id.addressQRCodeImageView);
    imagePublicProgressBar = view.findViewById(R.id.imagePublicProgressBar);
  
    // setting progress bars visible and imageView not-visible
    addressProgressBar.setVisibility(View.VISIBLE);
    balanceProgressBar.setVisibility(View.VISIBLE);
    addressQRCodeImageView.setVisibility(View.GONE);
    imagePublicProgressBar.setVisibility(View.VISIBLE);
  
    // define colors
    blackColor = getResources().getColor(R.color.QRCodeBlackColor);
    whiteColor = getResources().getColor(R.color.QRCodeWhiteColor);
    
    if ((context != null) && hasConnection(context))
    {
      GetPublicAddressFromNetTask getPublicAddressFromNetTask =
          new GetPublicAddressFromNetTask(context, idOfUser);
      getPublicAddressFromNetTask.setOnPublicAddressObtainedListener(this);
  
      getBalanceFromNetTask = new GetBalanceFromNetTask(context, idOfUser);
      getBalanceFromNetTask.setOnBalanceObtainedListener(this);
  
      GetPrivateAddressFromNetTask getPrivateAddressFromNetTask =
          new GetPrivateAddressFromNetTask(context, idOfUser);
      getPrivateAddressFromNetTask.setOnPrivateAddressObtainedListener(this);
    }
    else if((context != null) && (!hasConnection(context)))
    {
      balance = loadBalance();
      showBalance(balance);
      publicAddress  = loadPublicAddress();
      showPublicAddress(publicAddress);
      showQRCode(publicAddress);
    }
    
    return view;
  }
  
  private String loadPublicAddress()
  {
    Activity activity = getActivity();
    if(activity != null)
    {
      sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
      return sharedPreferences.getString(PUBLIC_ADDRESS, "");
    }
    return "";
  }
  
  private String loadBalance()
  {
    Activity activity = getActivity();
    if(activity != null)
    {
      sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
      return sharedPreferences.getString(BALANCE, "");
    }
    return "";
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
  
  private void savePublicAddress(String publicAddress)
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
  
  private void saveBalance(String balance)
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
  
  private void savePrivateAddress(String privateAddress)
  {
    Activity activity = getActivity();
    if(activity != null)
    {
      sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
      editor = sharedPreferences.edit();
      editor.putString(PRIVATE_ADDRESS, privateAddress);
      editor.apply();
    }
  }
  
  private boolean stringNotEmpty(String string)
  {
    return ( (string != null) && (!"".equals(string)) );
  }
  
  private void showPublicAddress(String publicAddress)
  {
    if (stringNotEmpty(publicAddress))
    {
      publicAddressTextView.setText(publicAddress);
      addressProgressBar.setVisibility(View.GONE);
    }
  }
  
  private void showBalance(String balance)
  {
    if(stringNotEmpty(balance))
    {
      balanceAmountTextView.setText(balance);
      balanceProgressBar.setVisibility(View.GONE);
    }
  }
  
  private void showQRCode(String publicAddress)
  {
    if(stringNotEmpty(publicAddress))
    {
      try
      {
        addressQRCodeImageView.setImageBitmap(textToImageEncode(publicAddress));
        addressQRCodeImageView.setVisibility(View.VISIBLE);
        imagePublicProgressBar.setVisibility(View.GONE);
      }
      catch(WriterException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  Bitmap textToImageEncode(String address) throws WriterException
  {
    BitMatrix bitMatrix;
    try
    {
      bitMatrix = new MultiFormatWriter().encode(address, BarcodeFormat.QR_CODE,
          QR_CODE_DIMENSION, QR_CODE_DIMENSION, null);
    }
    catch(IllegalArgumentException e)
    {
      e.printStackTrace();
      return null;
    }
    int bitMatrixWidth = bitMatrix.getWidth();
    int bitMatrixHeight = bitMatrix.getHeight();
    int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
    for(int y = 0; y < bitMatrixHeight; y++)
    {
      int offset = y * bitMatrixWidth;
      for(int x = 0; x < bitMatrixWidth; x++)
      {
        pixels[offset + x] = bitMatrix.get(x, y) ? blackColor : whiteColor;
      }
    }
    Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
    bitmap.setPixels(pixels, 0, QR_CODE_DIMENSION, 0, 0, bitMatrixWidth, bitMatrixHeight);
    return bitmap;
  }
  
  @Override
  public void onBalanceObtained(@NonNull String balance)
  {
    this.balance = balance;
    showBalance(balance);
    // save to SharedPreferences
    saveBalance(balance);
  }
  
  @Override
  public void onPrivateAddressObtained(@NonNull String privateAddress)
  {
    // save to SharedPreferences
    savePrivateAddress(privateAddress);
  }
  
  @Override
  public void onPublicAddressObtained(@NonNull String publicAddress)
  {
    this.publicAddress = publicAddress;
    showPublicAddress(publicAddress);
    showQRCode(publicAddress);
    // save to SharedPreferences
    savePublicAddress(publicAddress);
  }
  
  private static boolean hasConnection(final Context context)
  {
    // local variables
    ConnectivityManager connectivityManager;
    NetworkInfo         networkInfo;
    
    connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if(connectivityManager != null)
    {
      networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      if(networkInfo != null && networkInfo.isConnected())
      {
        return true;
      }
      networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
      if(networkInfo != null && networkInfo.isConnected())
      {
        return true;
      }
      networkInfo = connectivityManager.getActiveNetworkInfo();
      return networkInfo != null && networkInfo.isConnected();
    }
    return false;
  }
}