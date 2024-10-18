package com.afrimax.paysimati.main.data.di.config

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
class WalletStatementConfig {

    @Provides
    @Named("walletStatementPdfNamePrefix")
    fun provideWalletStatementPdfName(): String = "wallet_statement"
}