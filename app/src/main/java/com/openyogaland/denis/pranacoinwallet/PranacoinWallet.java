package com.openyogaland.denis.pranacoinwallet;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Singleton pattern is used
 * TODO make safe for threads
 */
public final class PranacoinWallet extends Application
{
  // fields
  private static PranacoinWallet instance = null;
  
  public PranacoinWallet()
  {
    if(instance == null)
    {
      instance = this;
    }
  }
  
  public static PranacoinWallet getInstance()
  {
    return PranacoinWallet.instance;
  }
  
  @NonNull
  public static Context applicationContext()
  {
    return getInstance().getApplicationContext();
  }
}