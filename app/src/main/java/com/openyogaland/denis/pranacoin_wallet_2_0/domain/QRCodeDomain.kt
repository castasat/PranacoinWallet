package com.openyogaland.denis.pranacoin_wallet_2_0.domain

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat.QR_CODE
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.openyogaland.denis.pranacoin_wallet_2_0.R

class
QRCodeDomain
{
  companion object
  {
    @JvmStatic val QR_CODE_DIMENSION = 500
  
    // encode string information to obtain QR-code
    @JvmStatic
    @Throws(WriterException::class)
    fun textToImageEncode(context: Context,
                          address : String,
                          isPrivate : Boolean) : Bitmap?
    {
      val applicationContext = context.applicationContext
  
      // define colors
      val blackColor = ContextCompat.getColor(applicationContext, R.color.QRCodeBlackColor)
      val whiteColor = ContextCompat.getColor(applicationContext, R.color.QRCodeWhiteColor)
      val redColor = ContextCompat.getColor(applicationContext, R.color.QRCodeRedColor)
      
      val bitMatrix : BitMatrix
      val qrCodeColor = if(isPrivate) redColor else blackColor
      
      try
      {
        bitMatrix = MultiFormatWriter()
        .encode(address,
                QR_CODE,
                QR_CODE_DIMENSION,
                QR_CODE_DIMENSION,
                null)
      }
      catch(e : IllegalArgumentException)
      {
        e.printStackTrace()
        return null
      }
    
      val bitMatrixWidth = bitMatrix.width
      val bitMatrixHeight = bitMatrix.height
      val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
      for(y in 0 until bitMatrixHeight)
      {
        val offset = y * bitMatrixWidth
        for(x in 0 until bitMatrixWidth)
        {
          pixels[offset + x] = if(bitMatrix.get(x, y)) qrCodeColor else whiteColor
        }
      }
      val bitmap =
        Bitmap
        .createBitmap(bitMatrixWidth,
                      bitMatrixHeight,
                      Bitmap.Config.ARGB_4444)
      
      bitmap
      .setPixels(pixels, 0,
                 QR_CODE_DIMENSION,
                 0, 0,
                 bitMatrixWidth,
                 bitMatrixHeight)
      
      return bitmap
    }
  }
}