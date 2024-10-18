package com.afrimax.paysimati.main.data.di.repository

import android.content.Context
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.common.data.utils.ApiClient
import com.afrimax.paysimati.common.data.utils.TokenProvider
import com.afrimax.paysimati.main.data.network.MainApiService
import com.afrimax.paysimati.main.data.repository.MainApiRepositoryImpl
import com.afrimax.paysimati.main.domain.repository.MainApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

class MainApiRepositoryModule {

    @Provides
    @Singleton
    fun provideMainApiService(apiClient: ApiClient): MainApiService =
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(apiClient.instance)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(MainApiService::class.java)

    @Provides
    @Singleton
    fun provideMainApiRepository(
        context: Context, mainApiService: MainApiService, tokenGenerator: TokenProvider
    ): MainApiRepository = MainApiRepositoryImpl(context, mainApiService, tokenGenerator)

}