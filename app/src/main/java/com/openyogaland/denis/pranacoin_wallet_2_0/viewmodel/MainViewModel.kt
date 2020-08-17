package com.openyogaland.denis.pranacoin_wallet_2_0.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.ref.WeakReference

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var contextWeakReference: WeakReference<Context>
    private val applicationContext: Context?
        get() = contextWeakReference.get()
    private val compositeDisposable = CompositeDisposable()
    var googleAccountId: String? = null

    init {
        initializeApplicationContext(application)
    }

    private fun initializeApplicationContext(application: Application): Context {
        contextWeakReference = WeakReference(application.applicationContext)
        return application.applicationContext
    }

    // TODO call from Activity.onTerminate()
    fun utilizeDisposable(disposableToUtilize: Disposable) {
        compositeDisposable.add(disposableToUtilize)
    }
}
