package com.afrimax.paysimati.ui.chatMerchant.data.di

import android.content.Context
import com.afrimax.paysimati.common.data.repository.SharedPrefsRepository
import com.afrimax.paysimati.common.data.repository.SharedPrefsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SharedPrefsRepositoryModule {

    @Provides
    fun provideSharedPrefsRepository(context: Context): SharedPrefsRepository =
        SharedPrefsRepositoryImpl(context)
}