package com.afrimax.paysimati.ui.chatMerchant.data.di

import android.content.Context
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.common.data.utils.TokenProvider
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.ApiService
import com.afrimax.paysimati.ui.chatMerchant.domain.repo.PaymentApiRepository
import com.afrimax.paysimati.ui.chatMerchant.domain.repo.PaymentApiRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


    @Module
    @InstallIn(ViewModelComponent::class)
    class PaymentApiRepositoryModule {

        @Provides
        @ViewModelScoped
        fun providePaymentApiRepository(
            tokenProvider: TokenProvider
        ): PaymentApiRepository {
            return PaymentApiRepositoryImpl(
                tokenProvider = tokenProvider
            )
        }
    }

//    fun providePaymentApiRepository(
//        context: Context, paymentApiService: ApiService, tokenProvider: TokenProvider
//    ): PaymentApiRepository = PaymentApiRepositoryImpl(
//        context = context, paymentApiService = paymentApiService, tokenProvider = tokenProvider
//    )
//}