package com.afrimax.paysimati

import android.app.Application
import android.content.Context
import com.afrimax.paysimati.util.RecaptchaManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideContext(app: Application): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideRecaptchaClient(app: Application): RecaptchaManager = RecaptchaManager(app)
}