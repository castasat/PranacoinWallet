package com.openyogaland.denis.pranacoinwallet;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// singleton class to get application context
// + not in a static manner to prevent memory leaks
// + has lazy initialization
// + safe for threads
class PranacoinWallet
{
  // constants
  private final static int QR_CODE_DIMENSION = 500;
  
  // fields
  private static volatile PranacoinWallet instance;
  private                 int             blackColor;
  private                 int             whiteColor;
  
  private PranacoinWallet(@NonNull @NotNull Context context)
  {
    Context applicationContext = context.getApplicationContext();
    
    // define colors
    blackColor = applicationContext.getResources().getColor(R.color.QRCodeBlackColor);
    whiteColor = applicationContext.getResources().getColor(R.color.QRCodeWhiteColor);
  }
  
  // obtain instance of singleton, double-check locking safe for threads
  static PranacoinWallet getInstance(@NonNull Context context)
  {
    PranacoinWallet localInstance = instance;
    if (localInstance == null)
    {
      synchronized(PranacoinWallet.class)
      {
        localInstance = instance;
        if(localInstance == null)
        {
          instance = new PranacoinWallet(context);
        }
      }
    }
    return instance;
  }
  
  // get idOfUser using application context
  String getIdOfUser()
  {
    return FirebaseInstanceId.getInstance().getId();
  }
  
  // check if string is not empty and not equal to null
  @Contract("null -> false")
  static boolean stringNotEmpty(String string)
  {
    return ((string != null) && (!"".equals(string)));
  }
  
  // check if there is internet connection
  static boolean hasConnection(@NonNull @NotNull final Context context)
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
  
  // encode string information to obtain QR-code
  Bitmap textToImageEncode(String address) throws WriterException
  {
    BitMatrix bitMatrix;
    try
    {
      bitMatrix = new MultiFormatWriter()
          .encode(address, BarcodeFormat.QR_CODE, QR_CODE_DIMENSION, QR_CODE_DIMENSION, null);
    }
    catch(IllegalArgumentException e)
    {
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
        pixels[offset + x] = bitMatrix.get(x, y) ? blackColor : whiteColor;
      }
    }
    Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
    bitmap.setPixels(pixels, 0, QR_CODE_DIMENSION, 0, 0, bitMatrixWidth, bitMatrixHeight);
    return bitmap;
  }
}