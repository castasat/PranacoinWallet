package com.openyogaland.denis.pranacoin_wallet_2_0.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.ref.WeakReference

class
MainViewModel(application: Application) : AndroidViewModel(application) {
    // context fields
    private lateinit var contextWeakReference: WeakReference<Context>
    private val applicationContext: Context?
        get() = contextWeakReference.get()

    // fields
    lateinit var googleAccountId: String

    // reactive fields
    private val compositeDisposable = CompositeDisposable()

    init {
        initializeApplicationContext(application)
    }

    private fun initializeApplicationContext(application: Application): Context {
        contextWeakReference = WeakReference(application.applicationContext)
        return application.applicationContext
    }

    // TODO call from Application.onTerminate()
    fun utilizeDisposable(disposableToUtilize: Disposable) {
        compositeDisposable.add(disposableToUtilize)
    }
}
