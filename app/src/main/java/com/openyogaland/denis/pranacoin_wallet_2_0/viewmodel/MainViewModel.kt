package com.openyogaland.denis.pranacoin_wallet_2_0.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.log
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.pranacoinServerApi
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers.io

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val pranacoinWallet2: PranacoinWallet2 = application as PranacoinWallet2
    var googleAccountId: String? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun getBalance() {
        googleAccountId?.let { walletId ->
            compositeDisposable.add(
                pranacoinServerApi
                    .getBalance(walletId = walletId)
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .doOnSuccess { balance ->
                        log("MainViewModel.getBalance(): balance = $balance")
                    }
                    .subscribe(
                        // TODO
                    )
            )
        }
    }

    fun getPublicAddress() {
        googleAccountId?.let { walletId ->
            compositeDisposable.add(
                pranacoinServerApi
                    .getPublicAddress(walletId = walletId)
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .doOnSuccess { publicAddress ->
                        log("MainViewModel.getBPublicAddress(): publicAddress = $publicAddress")
                    }
                    .subscribe(
                        // TODO
                    )
            )
        }
    }
}
