package com.afrimax.paysimati.ui.chatMerchant.data.di


import com.afrimax.paysimati.common.data.utils.TokenProvider
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.ApiService
import com.afrimax.paysimati.ui.chatMerchant.domain.repo.PaymentApiRepository
import com.afrimax.paysimati.ui.chatMerchant.repository.PaymentApiRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PaymentApiRepositoryModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiClient.apiService
    }

    @Provides
    @Singleton
    fun providePaymentApiRepository(
        apiService: ApiService,
        tokenProvider: TokenProvider
    ): PaymentApiRepository= PaymentApiRepositoryImpl(
        paymentApiService = apiService,
        tokenProvider = tokenProvider
        )
    }

