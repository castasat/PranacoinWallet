package com.openyogaland.denis.pranacoin_wallet_2_0.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.crashlytics
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.log
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.pranacoinServerApi
import com.openyogaland.denis.pranacoin_wallet_2_0.architecture.EventWrapper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers.io

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var googleAccountId: String? = null
    var googleEmail: String? = null
    private val pranacoinWallet2: PranacoinWallet2 = application as PranacoinWallet2

    val balanceLiveData = MutableLiveData<String>()
    val publicAddressLiveData = MutableLiveData<String>()
    val privateKeyLiveData = MutableLiveData<String>()
    val sendPranacoinsTransactionLiveData = MutableLiveData<EventWrapper<String>>()
    val errorLiveData = MutableLiveData<EventWrapper<String>>()

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
                        { balance -> balanceLiveData.postValue(balance) },
                        { throwable ->
                            log("MainViewModel.getBalance(): throwable = $throwable")
                            throwable.printStackTrace()
                            crashlytics(throwable)
                            errorLiveData.postValue(
                                EventWrapper(
                                    "MainViewModel.getBalance(): throwable = $throwable"
                                )
                            )
                        }
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
                        log("MainViewModel.getPublicAddress(): publicAddress = $publicAddress")
                    }
                    .subscribe(
                        { publicAddress -> publicAddressLiveData.postValue(publicAddress) },
                        { throwable ->
                            log("MainViewModel.getPublicAddress(): throwable = $throwable")
                            throwable.printStackTrace()
                            crashlytics(throwable)
                            errorLiveData.postValue(
                                EventWrapper(
                                    "MainViewModel.getPublicAddress(): throwable = $throwable"
                                )
                            )
                        }
                    )
            )
        }
    }

    fun getPrivateKey() {
        googleAccountId?.let { walletId ->
            compositeDisposable.add(
                pranacoinServerApi
                    .getPrivateKey(walletId = walletId)
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .doOnSuccess { privateKey ->
                        log("MainViewModel.getPrivateKey(): privateKey = $privateKey")
                    }
                    .subscribe(
                        { privateKey -> privateKeyLiveData.postValue(privateKey) },
                        { throwable ->
                            log("MainViewModel.getPrivateKey(): throwable = $throwable")
                            throwable.printStackTrace()
                            crashlytics(throwable)
                            errorLiveData.postValue(
                                EventWrapper(
                                    "MainViewModel.getPrivateKey(): throwable = $throwable"
                                )
                            )
                        }
                    )
            )
        }
    }

    fun sendPranacoins(recipientAddress: String, amount: String) {
        googleAccountId?.let { walletId ->
            compositeDisposable.add(
                pranacoinServerApi
                    .sendPranacoins(
                        walletId,
                        recipientAddress,
                        amount
                    )
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .doOnSuccess { transaction ->
                        log("MainViewModel.sendPranacoins(): transaction = $transaction")
                    }
                    .subscribe(
                        { transaction ->
                            sendPranacoinsTransactionLiveData.postValue(EventWrapper(transaction))
                            // TODO 0011 write transaction to local database
                        },
                        { throwable ->
                            log("MainViewModel.sendPranacoins(): throwable = $throwable")
                            throwable.printStackTrace()
                            crashlytics(throwable)
                            errorLiveData.postValue(
                                EventWrapper(
                                    "MainViewModel.sendPranacoins(): throwable = $throwable"
                                )
                            )
                        }
                    )
            )
        }
    }
}
