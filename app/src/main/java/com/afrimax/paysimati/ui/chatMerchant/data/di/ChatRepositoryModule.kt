package com.afrimax.paysimati.ui.chatMerchant.data.di

import com.afrimax.paysimati.common.core.log
import com.afrimax.paysimati.common.core.parseDate
import com.afrimax.paysimati.common.data.utils.TokenProvider
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.common.presentation.utils.ACTION_SEND_MESSAGE
import com.afrimax.paysimati.common.presentation.utils.*
import com.afrimax.paysimati.data.model.chat.ChatMessage

import com.afrimax.paysimati.data.model.chat.ChatMessageRequest
import com.afrimax.paysimati.data.model.chat.ChatMessageResponse
import com.afrimax.paysimati.ui.chatMerchant.domain.repo.ChatSocketRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import java.util.UUID

@Module
@InstallIn(ViewModelComponent::class)
class ChatRepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideChatSocketRepository(tokenProvider: TokenProvider): ChatSocketRepository {
        return ChatSocketRepositoryImpl(tokenProvider)
    }
}


class ChatSocketRepositoryImpl(
    private val tokenProvider: TokenProvider
) : ChatSocketRepository {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    override fun sendTextMessage(
        message: String, senderId: String, receiverId: String
    ): GenericResult<Unit, Errors.Network> {
        val chatMessageRequest = ChatMessageRequest(
            action = ACTION_SEND_MESSAGE,
            senderId = senderId,
            receiverId = receiverId,
            message = message
        )

        chatMessageRequest.log()

        return if (webSocket != null) {
            webSocket?.send(Gson().toJson(chatMessageRequest))
            GenericResult.Success(Unit)
        } else {
            GenericResult.Error(Errors.Network.UNABLE_TO_CONNECT)
        }
    }

    override fun disconnect() {
        // Clean up the OkHttp client to prevent leaks
        webSocket?.close(1000, null)
        webSocket = null
        "Initiate shut down".log()
    }

    // ====================================================================
    //                        HELPER FUNCTIONS
    // ====================================================================

    override suspend fun establishConnection(
        senderId: String, receiverId: String, onMessageReceived: (message: ChatMessage) -> Unit
    ): GenericResult<Unit, Errors.Network> {
//        if (webSocket == null) {
//            val httpUrlBuilder =
//                //need to chnage for customer
//                HttpUrl.Builder().scheme(PROTOCOL_HTTPS).host(BuildConfig.MERCHANT_CHAT_WEBSOCKET)
//                    .addPathSegment("")
//
//            httpUrlBuilder.addQueryParameter(SENDER_ID, senderId)
//            httpUrlBuilder.addQueryParameter(RECEIVER_ID, receiverId)
//
//            val httpUrl = httpUrlBuilder.build()
//            // Replace "https" with "wss" manually for WebSocket compatibility
//            val webSocketUrl = httpUrl.toString().replaceFirst(PROTOCOL_HTTPS, PROTOCOL_WSS)
//            webSocketUrl.log()
//            val request = Request.Builder().url(webSocketUrl).build()
//
//            val listener = object : WebSocketListener() {
//                override fun onOpen(webSocket: WebSocket, response: Response) {
//                    "Connection established".log()
//                }
//
//                override fun onMessage(webSocket: WebSocket, text: String) {
//                    try {
//                        val response = Gson().fromJson(text, ChatMessageResponse::class.java)
//                        val chatMessage = mapToChatMessage(response)
//                        chatMessage?.let { onMessageReceived(it) }
//                    } catch (e: JsonSyntaxException) {
//                        e.printStackTrace()
//                    }
//                }
//
//                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//                    //
//                }
//
//                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//                    webSocket.close(1000, null)
//                }
//
//                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//                    "Web socket closed".log()
//                }
//
//                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//                    //
//                }
//            }
//
//            webSocket = client.newWebSocket(request, listener)
//        }

        return GenericResult.Success(Unit)
    }

    fun mapToChatMessage(response: ChatMessageResponse): ChatMessage? {

        val chatCreatedTime = response.createdAt.parseDate()
        return when (response.type) {
            CHAT_TYPE_TEXT_MESSAGE -> {
                if (response.receiverId != null && chatCreatedTime != null) {
                    ChatMessage.TextMessage(
                        chatId = UUID.randomUUID()
                            .toString(), //Ideally this should come from the backend
                        receiverId = response.receiverId,
                        message = response.message,
                        chatCreatedTime = chatCreatedTime,
                        isAuthor = false
                    )
                } else {
                    null
                }
            }

            else -> null
        }
    }
}