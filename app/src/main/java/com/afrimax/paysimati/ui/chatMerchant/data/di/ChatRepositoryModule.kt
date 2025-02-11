package com.afrimax.paysimati.ui.chatMerchant.data.di

import com.afrimax.paysimati.ui.chatMerchant.domain.repo.ChatSocketRepository
import com.afrimax.paysimati.ui.chatMerchant.repository.ChatSocketRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class ChatRepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideChatSocketRepository(): ChatSocketRepository {
        return ChatSocketRepositoryImpl()
    }
}

