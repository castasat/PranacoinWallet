package com.openyogaland.denis.pranacoin_wallet_2_0.application

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.openyogaland.denis.pranacoin_wallet_2_0.BuildConfig
import org.jetbrains.annotations.NonNls

class
PranacoinWallet2 : MultiDexApplication()
{
  override fun
  onCreate()
  {
    super.onCreate()
    // for use of vector drawables on pre-lollipop
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
  }
  
  companion object
  PranacoinWallet2
  {
    @JvmStatic
    @NonNls
    val APP_ID = "PranacoinWallet2.0"
    
    @JvmStatic
    fun
    log(@NonNls text : String?)
    {
      if(BuildConfig.DEBUG)
      {
        when
        {
          text == null   ->
          {
            @NonNls val nullPointer = "NULL POINTER: text in log"
            
            Log.d(APP_ID, nullPointer)
          }
          text.isEmpty() ->
          {
            @NonNls val isEmpty = "IS EMPTY: text in log"
            
            Log.d(APP_ID, isEmpty)
          }
          else           -> Log.d(APP_ID, text)
        }
      }
    }
    
    // TODO String getIdOfUser()
    
    @JvmStatic
    fun stringNotEmpty(string : String?) : Boolean
    {
      return string != null && "" != string
    }
    
    @JvmStatic
    fun hasConnection(context : Context) : Boolean
    {
      val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      
      val networkInfo = connectivityManager.activeNetworkInfo
      
      return networkInfo != null && networkInfo.isConnected
    }
  }
}