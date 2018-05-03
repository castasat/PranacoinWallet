package com.openyogaland.denis.pranacoinwallet;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.firebase.iid.FirebaseInstanceId;

// singleton class to get application context
// + not in a static manner to prevent memory leaks
// + has lazy initialization
// + safe for threads
class PranacoinWallet
{
  // fields
  private static volatile PranacoinWallet instance;
  
  private PranacoinWallet(@NonNull Context context)
  {
    Context applicationContext = context.getApplicationContext();
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
  String getIdOfUser()
  {
    return FirebaseInstanceId.getInstance().getId();
  }
}