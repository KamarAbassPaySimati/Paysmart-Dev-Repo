package com.afrimax.paysimati.ui.chatMerchant.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.common.presentation.utils.VIEW_MODEL_STATE
import com.afrimax.paysimati.common.presentation.utils.asUiText
import com.afrimax.paysimati.data.model.chat.ChatMessage
import com.afrimax.paysimati.data.model.chat.ChatState
import com.afrimax.paysimati.data.model.chat.PaymentStatusType
import com.afrimax.paysimati.ui.chatMerchant.domain.usecase.EstablishSocketConnectionUseCase
import com.afrimax.paysimati.ui.chatMerchant.domain.usecase.GetPreviousChatsUseCase
import com.afrimax.paysimati.ui.chatMerchant.domain.usecase.ShutDownChatSocketUseCase
import com.afrimax.paysimati.ui.chatMerchant.domain.usecase.sendChatMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor  (
    private val savedStateHandle: SavedStateHandle,
    private val sendChatMessageUseCase: sendChatMessageUseCase,
    private val establishSocketConnectionUseCase: EstablishSocketConnectionUseCase,
    private val shutDownSocketUseCase: ShutDownChatSocketUseCase,
    private val getPreviousChatsUseCase: GetPreviousChatsUseCase
) :ViewModel() {



    private val _sideEffect = MutableSharedFlow<ChatSideEffect>() // Use SharedFlow for side effects
    val sideEffect: SharedFlow<ChatSideEffect> = _sideEffect

    private val initialstate = savedStateHandle[VIEW_MODEL_STATE] ?: ChatState(
        receiverName = "", receiverId = "", receiverProfilePicture = "", receiverAddress = "")
    //override val container = container<ChatState, ChatSideEffect>(initialState, savedStateHandle)

    val state = savedStateHandle.getStateFlow("state", initialstate)

    operator fun invoke(action: ChatIntent): Job? {
        return when (action) {
            is ChatIntent.EstablishConnection -> establishConnection()
            is ChatIntent.SendMessage -> sendMessage()
            is ChatIntent.ShutDownSocket -> shutDownSocket()
            is ChatIntent.SetMessageText -> setMessage(action.text)

        }
    }


    private fun establishConnection(): Job {
        return viewModelScope.launch {
            establishSocketConnectionUseCase(
                receiverId =state.value.receiverId
            ) { newMessage ->
                val currentMessages = ArrayList(state.value.realTimeMessages) //ArrayList(currentState.realTimeMessages)
                //Add message
                currentMessages.add(0, newMessage)

                viewModelScope.launch {
                    savedStateHandle["state"]= state.value.copy(realTimeMessages = currentMessages) }
            }
        }
    }



    private fun sendMessage(): Job? {
        return viewModelScope.launch {
            val currentMessage = state.value.messageText

            if (currentMessage.isNotBlank()) {
                val sendMessageCall = sendChatMessageUseCase(
                    message = currentMessage, // use currentMessage directly
                    receiverId = state.value.receiverId
                )

                when (sendMessageCall) {
                    is GenericResult.Success -> {

                        val currentMessages = ArrayList(state.value.realTimeMessages)
                        val newChatMessage = ChatMessage.TextMessage(
                            chatId = UUID.randomUUID().toString(),
                            receiverId = state.value.receiverId,
                            message = currentMessage,
                            chatCreatedTime = Date(),
                            isAuthor = true
                        )
                        currentMessages.add(0, newChatMessage)
                        savedStateHandle["state"] = state.value.copy(realTimeMessages = currentMessages)
                        // Now clear the message text after sending successfully
                        savedStateHandle["state"] = state.value.copy(messageText = "")
                    }

                    is GenericResult.Error -> {
                        viewModelScope.launch {
                            _sideEffect.emit(ChatSideEffect.ShowToast(sendMessageCall.error.asUiText()))
                        }
                    }
                }
            }
        }
    }

    private fun shutDownSocket(): Job? {
        shutDownSocketUseCase()
        return null
    }


    private fun setMessage(text: String): Job? {
        return viewModelScope.launch {
            savedStateHandle["state"] = state.value.copy(messageText = text)
        }
    }
    //// ====================================================================
    //                        HELPER CLASSES
    // ====================================================================

    class PreviousChatsSource(
        private val receiverId: String,
        private val getPreviousChatsUseCase: GetPreviousChatsUseCase,
        private val onError: (error: Errors) -> Unit,
    ) : PagingSource<Int, ChatMessage>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatMessage> {
            // The current page to load; default to 1 if not specified
            val currentPage = params.key ?: 1
            val previousChatsCall = getPreviousChatsUseCase(
                receiverId = receiverId, page = currentPage
            )

            return when (previousChatsCall) {
                is GenericResult.Success -> {
                    val nextPage = if (currentPage * 20 < previousChatsCall.data.totalRecords) {
                        currentPage + 1
                    } else {
                        null
                    }

                    LoadResult.Page(
                        data = previousChatsCall.data.previousChats,
                        prevKey = if (currentPage == 1) null else currentPage - 1,
                        nextKey = nextPage
                    )
                }

                is GenericResult.Error -> {
                    onError(previousChatsCall.error)
                    LoadResult.Error(Exception(previousChatsCall.error.toString()))
                }
            }

        }

        override fun getRefreshKey(state: PagingState<Int, ChatMessage>): Int? {
            return state.anchorPosition?.let { anchor ->
                state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                    ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
            }
        }
    }

    private val pageSource = PreviousChatsSource(
        // receiverId = currentState.receiverId,
        receiverId = state.value.receiverId,
        getPreviousChatsUseCase = getPreviousChatsUseCase,
        onError = { error ->
            // Directly show the toast without using side effect
            ChatSideEffect.ShowToast(error.asUiText())
        }
    )


    val pagedData = Pager(config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { pageSource }).flow.cachedIn(
        viewModelScope
    )
}