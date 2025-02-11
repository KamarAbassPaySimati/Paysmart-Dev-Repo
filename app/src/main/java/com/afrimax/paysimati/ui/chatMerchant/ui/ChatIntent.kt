package com.afrimax.paysimati.ui.chatMerchant.ui

sealed class ChatIntent {
    data object EstablishConnection : ChatIntent()
   data object SendMessage : ChatIntent()
  data object ShutDownSocket : ChatIntent()
    data class SetMessageText(val text: String) : ChatIntent()
}