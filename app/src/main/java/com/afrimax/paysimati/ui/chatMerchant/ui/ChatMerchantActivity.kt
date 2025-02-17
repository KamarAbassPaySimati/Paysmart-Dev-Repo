package com.afrimax.paysimati.ui.chatMerchant.ui

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.core.InterFontFamily
import com.afrimax.paysimati.common.core.PaySimatiTypography
import com.afrimax.paysimati.common.core.clearTimeToMalawiTimeZone
import com.afrimax.paysimati.common.core.highlightedLight
import com.afrimax.paysimati.common.core.neutralGrey
import com.afrimax.paysimati.common.core.neutralGreyDisabled
import com.afrimax.paysimati.common.core.neutralGreyPrimaryText
import com.afrimax.paysimati.common.core.neutralGreyTextFieldBackground
import com.afrimax.paysimati.common.core.parseCurrency
import com.afrimax.paysimati.common.core.parseMalawianDate
import com.afrimax.paysimati.common.core.primaryColor
import com.afrimax.paysimati.common.presentation.utils.PaymaartIdFormatter
import com.afrimax.paysimati.common.presentation.utils.parseTillNumber
import com.afrimax.paysimati.common.presentation.utils.showToast
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.DeclineMerchantRequest
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.chatMerchant.data.chat.ChatMessage
import com.afrimax.paysimati.ui.chatMerchant.data.chat.ChatState
import com.afrimax.paysimati.ui.chatMerchant.data.chat.PaymentStatusType
import com.afrimax.paysimati.ui.paymerchant.MerchantProfile
import com.afrimax.paysimati.ui.paymerchant.PayMerchantActivity
import com.afrimax.paysimati.util.Constants.MERCHANT_NAME
import com.afrimax.paysimati.util.Constants.PAYMAART_ID
import com.afrimax.paysimati.util.Constants.PAYMENT_AMOUNT
import com.afrimax.paysimati.util.Constants.PROFILE_PICTURE
import com.afrimax.paysimati.util.Constants.STATUS_CODE
import com.afrimax.paysimati.util.Constants.STREET_NAME
import com.afrimax.paysimati.util.Constants.TILL_NUMBER
import com.afrimax.paysimati.util.Constants.TRANSACTION_ID
import com.afrimax.paysimati.util.getInitials
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatMerchantActivity : BaseActivity() {
    private val vm: ChatViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }
        setContent {
            val state = vm.state.collectAsState()
            LaunchedEffect(Unit) {
                vm.sideEffect.collect {
                    when (it) {
                        is ChatSideEffect.ShowToast -> {
                            showToast(it.message)
                        }

                        is ChatSideEffect.ShowSnack -> {

                        }
                    }
                }
            }
            ChatMerchantScreen(state = state, modifier = Modifier.fillMaxSize())
        }
    }

    @Composable
    fun ChatMerchantScreen(state: State<ChatState>, modifier: Modifier = Modifier) {

        // val context = LocalContext.current
        Scaffold(modifier = modifier, topBar = {
            vm(ChatIntent.EstablishConnection)
            TopBar(
                receiverName = state.value.receiverName,
                receiverId = state.value.receiverId,
                receiverProfilePicture = state.value.receiverProfilePicture,
                modifier = Modifier.fillMaxWidth().clickable {
                    val intent = Intent(this@ChatMerchantActivity,MerchantProfile::class.java)
                    intent.putExtra(PAYMAART_ID,state.value.receiverId)
                    intent.putExtra(MERCHANT_NAME,state.value.receiverName)
                    startActivity(intent)
                }
            )
        },
            bottomBar = {
                BottomBar(
                    modifier = Modifier.background(Color.White)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    messageText = state.value.messageText,
                    onMessageType = { text ->
                        vm(ChatIntent.SetMessageText(text))
                    },
                    onClickSend = {
                        vm(ChatIntent.SendMessage)
                    },
                    reciverId = state.value.receiverId,
                    reciverName = state.value.receiverName,
                    reciverLoc = state.value.receiverAddress,
                    receiverProfilePicture = state.value.receiverProfilePicture,
                    tillnumber = state.value.tillnumber,
                    statusCode =0)
            }) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .background(Color.White)
            ) {
                // Content goes here
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val previousChats = vm.pagedData.collectAsLazyPagingItems()

                    previousChats.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                PrimaryLoader(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .align(Alignment.Center),
                                    context = this@ChatMerchantActivity
                                )
                            }

                            else -> {
                                ChatsLazyList(
                                    modifier = Modifier.matchParentSize(),
                                    previousChats = previousChats,
                                    realTimeMessages = state.value.realTimeMessages,
                                    reciverLoc = state.value.receiverAddress,
                                    receiverId= state.value.receiverId,
                                    reciverName = state.value.receiverName,
                                    receiverProfilePicture = state.value.receiverProfilePicture,
                                    statusCode = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TopBar(
        receiverName: String,
        receiverId: String,
        receiverProfilePicture: String? = null,
        modifier: Modifier = Modifier
    ) {
        val primaryColor = Color(ContextCompat.getColor(this@ChatMerchantActivity, R.color.primaryColor))
        Column(modifier = modifier.background(primaryColor)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onBackPressedDispatcher.onBackPressed()
                }) {
                    Image(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    //profile pic & shortname
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        //shortname
                        // receiverName.parseShortName()
                        Text(
                            text = getInitials(receiverName),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = primaryColor
                        )
                        //get profile pic
                        receiverProfilePicture?.let {
                            AndroidView(modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(8.dp)), factory = { context ->

                                ImageView(context).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    scaleType = ImageView.ScaleType.CENTER_CROP
                                }

                            }, update = { imageView ->
                                Glide.with(imageView.context).load(BuildConfig.CDN_BASE_URL + it)
                                    .override(150).into(imageView)
                            })
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            //reciver name
                            text = receiverName,
                            fontFamily = InterFontFamily(),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.White
                        )


                        Spacer(modifier = Modifier.height(4.dp))
                        //paymart id
                        PaymaartIdFormatter.formatId(receiverId!!)?.let {
                            Text(
                                text = it,
                                fontFamily = InterFontFamily(),
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun BottomBar(
        messageText: String,
        onMessageType: (text: String) -> Unit,
        onClickSend: () -> Unit,
        modifier: Modifier = Modifier,
        reciverId:String,
        reciverName:String,
        reciverLoc:String?=null,
        receiverProfilePicture: String? = null,
        tillnumber:String?=null,
        statusCode:Int

    ) {

        Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp, color = highlightedLight, shape = RoundedCornerShape(8.dp)
                    )
                    .padding(start = 12.dp)
                    .weight(1f)
                    .heightIn(min = 56.dp)
            ) {
                BasicTextField(
                    value = messageText,
                    onValueChange = { text -> onMessageType(text) },
                    maxLines = 6,
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f)
                        .padding(vertical = 4.dp)
                        .align(Alignment.CenterVertically),
                    decorationBox = { innerTextField ->
                        Box {
                            if (messageText.isEmpty()) {
                                Text(
                                    text = getString(R.string.enter_message),
                                    style = PaySimatiTypography().subtitle2,
                                    color = neutralGrey
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                IconButton(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(4.dp), onClick = onClickSend
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_send),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(neutralGreyPrimaryText)
                    )
                }

            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(colors = ButtonDefaults.buttonColors(primaryColor),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .wrapContentWidth()
                    .height(56.dp),
                onClick = {
                    val i =Intent(this@ChatMerchantActivity,PayMerchantActivity::class.java)
                    i.putExtra(PAYMAART_ID,reciverId)
                    i.putExtra(MERCHANT_NAME,reciverName)
                    i.putExtra(STREET_NAME,reciverLoc)
                    i.putExtra(PROFILE_PICTURE,receiverProfilePicture)
                    i.putExtra(TILL_NUMBER,tillnumber)
                    i.putExtra(STATUS_CODE,statusCode)
                    startActivity(i)
                }
            ) {
                Text(
                    text = stringResource(R.string.pay),
                    fontFamily = InterFontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

        }

    }


    @Composable
    fun ChatsLazyList(
        modifier: Modifier = Modifier,
        previousChats: LazyPagingItems<ChatMessage>,
        realTimeMessages: ArrayList<ChatMessage>,
        reciverLoc: String?=null,
        receiverProfilePicture: String?,
        receiverId: String,
        reciverName: String,
        statusCode:Int
    ) {

        LazyColumn(
            modifier = modifier.background(Color.White),
            reverseLayout = true,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            //Realtime messages
            realtimeChats(realTimeMessages = realTimeMessages,
                previousChats = previousChats,
                reciverLoc = reciverLoc,
                reciverName=reciverName,
                receiverProfilePicture=receiverProfilePicture,
                receiverId=receiverId,
                statusCode=statusCode)

            //Previous messages
            previousChats(previousChats = previousChats,
                reciverLoc=reciverLoc,
                reciverName=reciverName,
                receiverProfilePicture=receiverProfilePicture,
                receiverId=receiverId,
                statusCode=statusCode)

            if (previousChats.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = primaryColor,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

        }
    }

    /**
     * Displays real-time chat messages in a LazyList.
     *
     * This function iterates through a list of real-time chat messages ([realTimeMessages]) and displays them
     * using appropriate composable functions based on the message type (text or payment).
     * It also handles displaying date chips to separate messages by day.
     *
     * @param realTimeMessages A list of real-time chat messages to display.
     * @param previousChats A LazyPagingItems object containing previous chat messages. This is used to
     * determine if a date chip should be displayed before the first real-time message.
     */
    private fun LazyListScope.realtimeChats(
        realTimeMessages: ArrayList<ChatMessage>, previousChats: LazyPagingItems<ChatMessage>
        ,reciverLoc: String?=null,
        receiverId: String,
        reciverName: String,
        receiverProfilePicture: String?,
        statusCode: Int
    ) {
        items(count = realTimeMessages.size) { index ->

            val chat = realTimeMessages.getOrNull(index)
            val previousChat = if (index == realTimeMessages.size - 1) {
                if (previousChats.itemCount > 0) previousChats[0] else null
            } else {
                realTimeMessages.getOrNull(index + 1)
            }

            chat?.let {
                when (chat) {
                    is ChatMessage.TextMessage -> {
                        TextMessage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            message = chat.message,
                            isAuthor = chat.isAuthor
                        )
                    }

                    is ChatMessage.PaymentMessage -> {
                        PaymentMessage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            amount = chat.amount,
                            txnId = chat.transactionId,
                            paymentStatus = chat.paymentStatusType,
                            date = chat.chatCreatedTime.parseMalawianDate("dd MMM yyyy, HH:mm"),
                            note = chat.note,
                            isSender = chat.isAuthor,
                            tillnumber = chat.tillnumber.parseTillNumber(),
                            reciverLoc = reciverLoc,
                            receiverId = receiverId,
                            reciverName = reciverName,
                            receiverProfilePicture = receiverProfilePicture,
                            statusCode = statusCode
                        )
                    }
                }
            }

            val isFirstChatInTheDay =
                chat != null && previousChat != null && chat.createdTime.clearTimeToMalawiTimeZone() != previousChat.createdTime.clearTimeToMalawiTimeZone()

            if (isFirstChatInTheDay || previousChat == null) {
                DateChip(
                    date = chat!!.createdTime.parseMalawianDate(DATE_CHIP_FORMAT),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

    }
    private fun LazyListScope.previousChats(previousChats: LazyPagingItems<ChatMessage>,
                                            reciverLoc: String?=null,
                                            receiverId: String,
                                            reciverName: String,
                                            receiverProfilePicture: String?,statusCode: Int) {
        items(
            count = previousChats.itemCount
        ) { index ->
            val chat = previousChats[index]
            val previousChat = if (index > 0) previousChats[index - 1] else null

            val isLastChatInTheDay =
                chat != null && previousChat != null && chat.createdTime.clearTimeToMalawiTimeZone() != previousChat.createdTime.clearTimeToMalawiTimeZone()

            //Load after chat message
            if (isLastChatInTheDay) {
                DateChip(
                    date = previousChat!!.createdTime.parseMalawianDate(DATE_CHIP_FORMAT),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            chat?.let {
                when (chat) {
                    is ChatMessage.TextMessage -> {
                        TextMessage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            message = chat.message,
                            isAuthor = chat.isAuthor
                        )
                    }

                    is ChatMessage.PaymentMessage -> {
                        PaymentMessage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            amount = chat.amount,
                            txnId = chat.transactionId,
                            paymentStatus = chat.paymentStatusType,
                            date = chat.chatCreatedTime.parseMalawianDate("dd MMM yyyy, HH:mm"),
                            note = chat.note,
                            isSender = chat.isAuthor,
                            tillnumber = chat.tillnumber.parseTillNumber(),
                            reciverLoc = reciverLoc,
                            receiverId = receiverId,
                            reciverName = reciverName,
                            receiverProfilePicture = receiverProfilePicture,
                            statusCode = statusCode,


                            )
                    }
                }
            }

            //All the messages are loaded if following is true
            val isFullyLoaded =
                chat != null && previousChats.loadState.append == LoadState.NotLoading(
                    endOfPaginationReached = true
                ) && index == previousChats.itemCount - 1

            //Load before chat message
            if (isFullyLoaded) {
                DateChip(
                    date = chat!!.createdTime.parseMalawianDate(DATE_CHIP_FORMAT),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }

    @Composable
    fun TextMessage(modifier: Modifier = Modifier, message: String, isAuthor: Boolean) {

        BoxWithConstraints(
            modifier = modifier,
            contentAlignment = if (isAuthor) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            val maxWidth = maxWidth * 0.75f // 70% of the screen width
            Box(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .background(
                        if (isAuthor) neutralGreyDisabled else neutralGreyDisabled,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = message,
                    fontFamily = InterFontFamily(),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }

    }



    @Composable
    fun PaymentMessage(
        modifier: Modifier = Modifier,
        amount: Double,
        txnId: String,
        paymentStatus: PaymentStatusType,
        date: String,
        note: String? = null,
        isSender: Boolean,
        tillnumber:String,
        reciverLoc: String?=null,
        reciverName:String,
        receiverProfilePicture:String?,
        receiverId:String,
        statusCode: Int

    ) {
        var isLoading by remember { mutableStateOf(false) }
        var declineButtonText by remember { mutableStateOf("") }
        declineButtonText = stringResource(R.string.decline)
        var isRowVisible by remember { mutableStateOf(true) }
        var isdeclined by remember {mutableStateOf(false)}



        BoxWithConstraints(
            modifier = modifier,
            contentAlignment = if (isSender) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            val maxWidth = maxWidth * 0.7f // 70% of the screen width
            Column(
                modifier = Modifier
                    .width(maxWidth)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp, color = highlightedLight, shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {

                //Amount
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = amount.parseCurrency(),
                        fontFamily = InterFontFamily(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = primaryColor
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = stringResource(R.string.mwk),
                        fontFamily = InterFontFamily(),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = primaryColor,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                //Txn Id
                Text(
                    text = stringResource(R.string.txn_id_colon, txnId),
                    fontFamily = InterFontFamily(),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = neutralGreyPrimaryText
                )

                Spacer(modifier = Modifier.height(10.dp))




                Text(
                    text = stringResource(R.string.tillnumber, tillnumber),
                    fontFamily = InterFontFamily(),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = neutralGreyPrimaryText
                )

                Spacer(modifier = Modifier.height(10.dp))



                Row(modifier = Modifier.fillMaxWidth()) {

                    if(isdeclined){
                        PaymentDeclinedChip(modifier = Modifier.weight(1f))
                    }

                    //Payment status
                    when (paymentStatus) {

                        PaymentStatusType.RECEIVED -> {
                            PaymentReceivedChip(modifier = Modifier.weight(1f))
                        }

                        PaymentStatusType.DECLINED -> {
                            PaymentDeclinedChip(modifier = Modifier.weight(1f))
                        }

                        PaymentStatusType.PENDING -> {

                        }
                    }


                    //Date
                    androidx.compose.material.Text(
                        text = date,
                        fontFamily = InterFontFamily(),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = neutralGreyPrimaryText
                    )

                }
                Spacer(modifier = Modifier.height(10.dp))



                //Note
                if(!note.isNullOrBlank()) {
                    Text(
                        text = stringResource(R.string.note_colon, note),
                        fontFamily = InterFontFamily(),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = neutralGreyPrimaryText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    //Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(highlightedLight)
                    )

                }

                if(isRowVisible && paymentStatus!=PaymentStatusType.RECEIVED && paymentStatus!=PaymentStatusType.DECLINED) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {

                                if(!isLoading){
                                    isLoading=true
                                    lifecycleScope.launch {
                                        val response = ApiClient.apiService.declineMerchantRequest(fetchIdToken(),
                                            DeclineMerchantRequest(
                                                requestId = txnId,
                                                recieverId =receiverId
                                            )
                                        )
                                        isLoading = false
                                        if(response.isSuccessful) {
                                            isRowVisible = false
                                            isdeclined = true


                                        }else{
                                            PaymentStatusType.PENDING
                                        }
                                    }

                                }
                            },//onDeclineClick,
                            border = BorderStroke(
                                1.dp, primaryColor
                            ), // Use your highlightedLight color
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if(isLoading){
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(0.dp),
                                    color = primaryColor,
                                    strokeWidth = 2.dp
                                )
                            }
                            else{
                                Text(
                                    text = declineButtonText,
                                    color = primaryColor, // Or a suitable color
                                    fontFamily = InterFontFamily(),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }

                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                val i = Intent(
                                    this@ChatMerchantActivity,
                                    PayMerchantActivity::class.java
                                )
                                i.putExtra(PAYMAART_ID, receiverId)
                                i.putExtra(MERCHANT_NAME, reciverName)
                                i.putExtra(STREET_NAME, reciverLoc)
                                i.putExtra(PROFILE_PICTURE, receiverProfilePicture)
                                i.putExtra(TILL_NUMBER, tillnumber)
                                i.putExtra(PAYMENT_AMOUNT, amount)
                                i.putExtra(STATUS_CODE, statusCode)
                                i.putExtra(TRANSACTION_ID, txnId)
                                startActivity(i)
                            },//onPayClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor // Use your primary color
                            ),
                            shape = RoundedCornerShape(8.dp)

                        ) {
                            Text(
                                text = stringResource(R.string.pay),
                                color = Color.White,
                                fontFamily = InterFontFamily(),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun PaymentReceivedChip(modifier: Modifier = Modifier) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_payment_success),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(6.dp))

            androidx.compose.material.Text(
                text = stringResource(R.string.paid),
                fontFamily = InterFontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = neutralGreyPrimaryText
            )

        }
    }


    @Composable
    fun PaymentDeclinedChip(modifier: Modifier = Modifier) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_payment_failure),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = stringResource(R.string.declined),
                fontFamily = InterFontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = neutralGreyPrimaryText
            )

        }
    }
    @Composable
    fun DateChip(modifier: Modifier = Modifier, date: String) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(24.dp))
                .background(neutralGreyTextFieldBackground)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            androidx.compose.material.Text(
                text = date,
                fontFamily = InterFontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = neutralGreyPrimaryText
            )
        }
    }


    override fun onResume() {
        vm(ChatIntent.EstablishConnection)
        super.onResume()
    }

    override fun onPause() {
        vm(ChatIntent.ShutDownSocket)
        super.onPause()
    }


    companion object {
        const val DATE_CHIP_FORMAT = "dd MMM, yyyy"
    }


}



