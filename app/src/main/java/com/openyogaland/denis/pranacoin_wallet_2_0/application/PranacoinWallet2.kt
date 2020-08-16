package com.openyogaland.denis.pranacoin_wallet_2_0.application

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate.setCompatVectorFromResourcesEnabled
import androidx.multidex.MultiDexApplication
import com.openyogaland.denis.pranacoin_wallet_2_0.BuildConfig.DEBUG

class
PranacoinWallet2 : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        // for use of vector drawables on pre-lollipop
        setCompatVectorFromResourcesEnabled(true)
    }

    companion object
    PranacoinWallet2 {
        @JvmStatic
        val APP_ID = "PranacoinWallet2.0"

        @JvmStatic
        fun log(text: String) {
            if (DEBUG) {
                when {
                    text.isEmpty() -> {
                        val isEmpty = "IS EMPTY: text in log"
                        Log.d(APP_ID, isEmpty)
                    }
                    else -> Log.d(APP_ID, text)
                }
            }
        }

        // TODO String getIdOfUser()

        // TODO remove deprecated connectivity API

        @JvmStatic
        fun hasConnection(context: Context): Boolean {
            var result = false
            (context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.let { networkInfo: NetworkInfo ->
                    result = networkInfo.isConnected
                }
            return result
        }
    }
}