package com.afrimax.paysimati.common.data.di.repository

import android.content.Context
import com.afrimax.paysimati.common.data.repository.AmplifyRepositoryImpl
import com.afrimax.paysimati.common.domain.repository.AmplifyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AmplifyRepositoryModule {

    @Provides
    @Singleton
    fun provideAmplifyRepository(
        context: Context
    ): AmplifyRepository = AmplifyRepositoryImpl(context)
}