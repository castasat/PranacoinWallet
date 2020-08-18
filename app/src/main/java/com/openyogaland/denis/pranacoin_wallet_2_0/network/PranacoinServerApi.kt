package com.openyogaland.denis.pranacoin_wallet_2_0.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PranacoinServerApi {
    @GET(value = "api.php?action=getbalance&walletid={walletId}")
    fun getBalance(@Path(value = "walletId") walletId: String): Single<String>

    @GET(value = "api.php?action=getpubaddr&walletid={walletId}")
    fun getPublicAddress(@Path(value = "walletId") walletId: String): Single<String>

    @GET(value = "api.php?action=getprivaddr&walletid={walletId}")
    fun getPrivateAddress(@Path(value = "walletId") walletId: String): Single<String>

    @GET(value = "api.php?action=sendprana&walletid={walletId}")
    fun sendSum(
        @Path(value = "walletId") walletId: String,
        @Query(value = "recipientaddr") recipientAddress: String,
        @Query(value = "sum") amount: String
    ): Single<String>
}