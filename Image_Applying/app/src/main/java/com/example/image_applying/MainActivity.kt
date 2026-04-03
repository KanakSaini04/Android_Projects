package com.example.image_applying

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.image_applying.ui.theme.Image_ApplyingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Image_ApplyingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "",
                        from = "from kanak",
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, from: String, modifier: Modifier = Modifier) {

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.androidparty), // your image
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        // 🔝 Top Center
        Text(
            text = "Top",
            fontSize = 40.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        )

        // ⬅️ Start (Left Center)
        Text(
            text = "Start",
            fontSize = 40.sp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
        )

        // ➡️ End (Right Center)
        Text(
            text = "End",
            fontSize = 40.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
        )

        // ⬇️ Bottom Center
        Text(
            text = "Bottom",
            fontSize = 40.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Image_ApplyingTheme {
        Greeting("Eternal", from ="from kanak")
    }
}