package com.openyogaland.denis.pranacoinwallet;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Singleton pattern safe for threads
 */
public final class PranacoinWallet extends Application
{
  // fields
  // never cached in thread memory
  private volatile static PranacoinWallet instance = null;
  
  // empty constructor (called from getInstance())
  public PranacoinWallet()
  {
  }
  
  // called from getPranacoinWalletContext()
  public static PranacoinWallet getInstance()
  {
    if(instance == null)
    {
      synchronized(PranacoinWallet.class)
      {
        if(instance == null)
        {
          instance = new PranacoinWallet();
        }
      }
    }
    return instance;
  }
  
  @NonNull
  static Context getPranacoinWalletContext()
  {
    return getInstance().getApplicationContext();
  }
}