package com.afrimax.paysimati.common.data.di.repository

import android.content.Context
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.common.data.network.DownloadService
import com.afrimax.paysimati.common.data.repository.DownloadRepositoryImpl
import com.afrimax.paysimati.common.domain.repository.DownloadRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DownloadRepositoryModule {

    @Provides
    @Singleton
    fun provideDownloadService(): DownloadService = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create()).build()
        .create(DownloadService::class.java)

    @Provides
    @Singleton
    fun provideDownloadRepository(
        context: Context, downloadService: DownloadService
    ): DownloadRepository = DownloadRepositoryImpl(context, downloadService)

}