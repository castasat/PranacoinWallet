package com.openyogaland.denis.pranacoinwallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.iid.InstanceID;

// singleton class to get application context
// + not in a static manner to prevent memory leaks
// + has lazy initialization
// + safe for threads
class PranacoinWallet
{
  // fields
  @SuppressLint("StaticFieldLeak")
  private static volatile PranacoinWallet instance;
  private final           Context         context;
  
  // constructor
  private PranacoinWallet(@NonNull Context context)
  {
    this.context = context.getApplicationContext();
  }
  
  // double-check locking safe for threads
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
  public String getIdOfUser()
  {
    return InstanceID.getInstance(this.context).getId();
  }
}