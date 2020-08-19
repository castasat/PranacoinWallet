package com.openyogaland.denis.pranacoin_wallet_2_0.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PranacoinServerApi {
    @GET("api.php?action=getbalance")
    fun getBalance(@Query(WALLET_ID) walletId: String): Single<String>

    @GET("api.php?action=getpubaddr")
    fun getPublicAddress(@Query(WALLET_ID) walletId: String): Single<String>

    @GET("api.php?action=getprivaddr")
    fun getPrivateAddress(@Query(WALLET_ID) walletId: String): Single<String>

    @GET("api.php?action=sendprana")
    fun sendSum(
        @Query(WALLET_ID) walletId: String,
        @Query(RECIPIENT_ADDRESS) recipientAddress: String,
        @Query(AMOUNT) amount: String
    ): Single<String>

    companion object {
        private const val WALLET_ID = "walletid"
        private const val RECIPIENT_ADDRESS = "recipientaddr"
        private const val AMOUNT = "sum"
    }
}