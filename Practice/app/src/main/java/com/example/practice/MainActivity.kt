package com.example.practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
NeoButtonExample()
        }
    }
}


@Composable
fun NeoButtonExample() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2F36)), // background
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .width(200.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFF2C2F36))

                // Light shadow (top-left highlight)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(30.dp),
                    ambientColor = Color.White.copy(alpha = 0.2f),
                    spotColor = Color.White.copy(alpha = 0.2f)
                )

                // Dark shadow (bottom-right)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(30.dp),
                    ambientColor = Color.Black.copy(alpha = 0.6f),
                    spotColor = Color.Black.copy(alpha = 0.6f)
                )

                .clickable { }
                .padding(horizontal = 24.dp, vertical = 14.dp),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Glass Button",
                color = Color.LightGray,
                fontSize = 16.sp
            )
        }
    }
}