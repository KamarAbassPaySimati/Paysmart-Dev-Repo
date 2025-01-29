package com.afrimax.paysimati.ui.paymerchant

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paysimati.R
import com.afrimax.paysimati.ui.BaseActivity

class ChatMerchantActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }
        setContent {
            ChatMerchantScreen()
        }
    }
}


@Composable
fun ChatMerchantScreen(modifier: Modifier = Modifier) {

    Scaffold(modifier = modifier, topBar = {
        val context = androidx.compose.ui.platform.LocalContext.current
        TopBar(modifier = Modifier.fillMaxWidth(),context = context)
    },
        bottomBar = {
            BottomBar()
        }) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            // Content goes here
        }
    }
}

@Composable
fun BottomBar() {
    // Implement BottomBar content
}


@Composable
fun TopBar(modifier: Modifier = Modifier, context: android.content.Context) {
    val primaryColor = Color(ContextCompat.getColor(context, R.color.primaryColor))

    Column(modifier = modifier.background(primaryColor)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material.IconButton(onClick = {}) {
                Image(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    // Implement Avatar or Icon
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Shoaib",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Shoaib kota",
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewChatMerchantScreen() {
    ChatMerchantScreen()
}