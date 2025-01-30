package com.afrimax.paysimati.ui.chatMerchant.ui

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.afrimax.paysimati.R
import androidx.compose.runtime.State
import androidx.compose.ui.viewinterop.AndroidView
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.common.core.InterFontFamily
import com.afrimax.paysimati.common.core.PaySimatiTypography
import com.afrimax.paysimati.common.core.highlightedLight
import com.afrimax.paysimati.common.core.neutralGrey
import com.afrimax.paysimati.common.presentation.utils.PaymaartIdFormatter
import com.afrimax.paysimati.data.model.chat.ChatMessage
import com.afrimax.paysimati.data.model.chat.ChatState
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.util.getInitials
import com.bumptech.glide.Glide

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
            ChatMerchantScreen(state = state)
        }
    }


    @Composable
    fun ChatMerchantScreen(state: State<ChatState>, modifier: Modifier = Modifier) {

        val context = LocalContext.current
        Scaffold(modifier = modifier, topBar = {

            TopBar(
                receiverName = state.value.receiverName,
                receiverId = state.value.receiverId,
                receiverProfilePicture = state.value.receiverProfilePicture,
                modifier = Modifier.fillMaxWidth(), context = context
            )
        },
            bottomBar = {
                BottomBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    context = context,
                    messageText = state.value.messageText,
                )
            }) { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                // Content goes here
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //  val previousChats = vm.pagedData.collectAsLazyPagingItems()
                    //   previousChats.apply {
                    when {
//                        loadState.refresh is LoadState.Loading -> {
//                            PrimaryLoader(
//                                modifier = Modifier
//                                    .size(100.dp)
//                                    .align(Alignment.Center),
//                                context = context
//                            )
//                        }
//
//                        else -> {
//                            ChatsLazyList(
//                                modifier = Modifier.matchParentSize(),
//                                previousChats = previousChats,
//                                realTimeMessages = state.value.realTimeMessages
//                            )
//                        }
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
        modifier: Modifier = Modifier, context: Context
    ) {
        val primaryColor = Color(ContextCompat.getColor(context, R.color.primaryColor))
        Column(modifier = modifier.background(primaryColor)) {
            //Topbar
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
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.White
                        )


                        Spacer(modifier = Modifier.height(4.dp))
                        //paymart id
                        PaymaartIdFormatter.formatId(receiverId!!)?.let {
                            Text(
                                text = it,
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
     //   onMessageType: (text: String) -> Unit,
      //  onClickSend: () -> Unit,
        modifier: Modifier = Modifier,
        context: Context
    ) {
        val primaryColor = Color(ContextCompat.getColor(context, R.color.primaryColor))
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
                    onValueChange = { text -> }, // onMessageType(text) },
                    maxLines = 6,
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f)
                        .padding(vertical = 4.dp)
                        .align(Alignment.CenterVertically),
                decorationBox = { innerTextField ->
                    Box {
                        if (messageText.isEmpty()) {
                            androidx.compose.material.Text(
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
                        .padding(4.dp), onClick = {}  //onClickSend
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_send),
                        contentDescription = null
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
                    //logic part
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
        // Implement BottomBar content
    }



    @Composable
    fun ChatsLazyList(
        modifier: Modifier = Modifier,
        previousChats: LazyPagingItems<ChatMessage>,
        realTimeMessages: ArrayList<ChatMessage>
    ) {

        LazyColumn(
            modifier = modifier,
            reverseLayout = true,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            //Realtime messages
            //      realtimeChats(realTimeMessages = realTimeMessages, previousChats = previousChats)

            //Previous messages
            //    previousChats(previousChats = previousChats)

            if (previousChats.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.CircularProgressIndicator(
                            ///  color = primaryColor,
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

}


//@Preview(showBackground = true)
//@Composable
//fun PreviewChatMerchantScreen() {
//    ChatMerchantScreen()
//}