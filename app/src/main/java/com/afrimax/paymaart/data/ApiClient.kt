package com.afrimax.paymaart.data

import retrofit2.Retrofit

object RetrofitClient{
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("")
            .build()
    }
}
object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}