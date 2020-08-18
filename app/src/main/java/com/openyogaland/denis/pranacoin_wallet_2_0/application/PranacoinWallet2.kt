package com.openyogaland.denis.pranacoin_wallet_2_0.application

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate.setCompatVectorFromResourcesEnabled
import com.google.gson.GsonBuilder
import com.openyogaland.denis.pranacoin_wallet_2_0.BuildConfig.DEBUG
import com.openyogaland.denis.pranacoin_wallet_2_0.network.PranacoinServerApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class
PranacoinWallet2 : Application() {
    override fun onCreate() {
        super.onCreate()
        // for use of vector drawables on pre-lollipop
        setCompatVectorFromResourcesEnabled(true)
    }

    companion object
    PranacoinWallet2 {
        private const val APP_ID = "PranacoinWallet2.0"
        private const val PRANACOIN_SERVER_URL = "http://95.213.191.196/"

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

        @Deprecated("remove deprecated connectivity API")
        fun hasConnection(context: Context): Boolean {
            var result = false
            (context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.let { networkInfo: NetworkInfo ->
                    result = networkInfo.isConnected
                }
            return result
        }

        fun getPranacoinServerApi() =
            Retrofit
                .Builder()
                .baseUrl(PRANACOIN_SERVER_URL)
                .client(
                    OkHttpClient()
                        .newBuilder()
                        .addInterceptor(
                            HttpLoggingInterceptor().setLevel(BODY)
                        )
                        .build()
                )
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory
                        .create(
                            GsonBuilder()
                                .setLenient()
                                .create()
                        )
                )
                .build()
                .create(PranacoinServerApi::class.java)
    }
}