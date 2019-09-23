package com.openyogaland.denis.pranacoin_wallet_2_0.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

//import com.crashlytics.android.Crashlytics;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.openyogaland.denis.pranacoin_wallet_2_0.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// singleton class to get application context
// + not in a static manner to prevent memory leaks
// + has lazy initialization
// + safe for threads
public class Pranacoin_Wallet_2_0
{
  // constants
  private final static int QR_CODE_DIMENSION = 500;
  
  // fields
  private static volatile Pranacoin_Wallet_2_0 instance;
  private                 int             blackColor;
  private                 int             whiteColor;
  private                 int             redColor;
  
  private Pranacoin_Wallet_2_0(@NonNull @NotNull Context context)
  {
    Context applicationContext = context.getApplicationContext();
    
    // define colors
    blackColor = ContextCompat.getColor(applicationContext, R.color.QRCodeBlackColor);
    whiteColor = ContextCompat.getColor(applicationContext, R.color.QRCodeWhiteColor);
    redColor = ContextCompat.getColor(applicationContext, R.color.QRCodeRedColor);
  }
  
  // obtain instance of singleton, double-check locking safe for threads
  public static Pranacoin_Wallet_2_0 getInstance(@NonNull Context context)
  {
    Pranacoin_Wallet_2_0 localInstance = instance;
    if (localInstance == null)
    {
      synchronized(Pranacoin_Wallet_2_0.class)
      {
        localInstance = instance;
        if(localInstance == null)
        {
          instance = new Pranacoin_Wallet_2_0(context);
        }
      }
    }
    return instance;
  }
  
  // get idOfUser using application context
  /* TODO String getIdOfUser()
  {
    return FirebaseInstanceId.getInstance().getId();
  }*/
  
  // check if string is not empty and not equal to null
  @Contract("null -> false")
  public static boolean stringNotEmpty(String string)
  {
    return ((string != null) && (!"".equals(string)));
  }
  
  // check if there is internet connection
  public static boolean hasConnection(@NonNull @NotNull final Context context)
  {
    // local variables
    ConnectivityManager connectivityManager;
    NetworkInfo         networkInfo;
    
    connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if(connectivityManager != null)
    {
      networkInfo = connectivityManager.getActiveNetworkInfo();
      return networkInfo != null && networkInfo.isConnected();
    }
    return false;
  }
  
  // encode string information to obtain QR-code
  public Bitmap textToImageEncode(String address, boolean isPrivate) throws WriterException
  {
    BitMatrix bitMatrix;
    int qrCodeColor = (isPrivate) ? redColor : blackColor;
    try
    {
      bitMatrix = new MultiFormatWriter()
          .encode(address, BarcodeFormat.QR_CODE, QR_CODE_DIMENSION, QR_CODE_DIMENSION, null);
    }
    catch(IllegalArgumentException e)
    {
      //Crashlytics.logException(e);
      e.printStackTrace();
      return null;
    }
    int   bitMatrixWidth  = bitMatrix.getWidth();
    int   bitMatrixHeight = bitMatrix.getHeight();
    int[] pixels          = new int[bitMatrixWidth * bitMatrixHeight];
    for(int y = 0; y < bitMatrixHeight; y++)
    {
      int offset = y * bitMatrixWidth;
      for(int x = 0; x < bitMatrixWidth; x++)
      {
        pixels[offset + x] = bitMatrix.get(x, y) ? qrCodeColor : whiteColor;
      }
    }
    Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
    bitmap.setPixels(pixels, 0, QR_CODE_DIMENSION, 0, 0, bitMatrixWidth, bitMatrixHeight);
    return bitmap;
  }
}