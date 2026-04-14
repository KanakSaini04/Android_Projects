package com.example.practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotificationScreen()
        }
    }
}



@Composable
fun NotificationCounter(count: Int, increment : () -> Int) {

    Column(
        modifier = Modifier.fillMaxSize(.5f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Count = $count",
            fontSize = 20.sp
        )

        Button(
            onClick = {increment() },
            shape = RoundedCornerShape(0.dp)
        ) {
            Text("Increase")
        }

    }
}
@Preview(showBackground = true)
@Composable
fun NotificationScreen() {
    var count = rememberSaveable { mutableStateOf(1) }

    Column( modifier = Modifier.fillMaxSize(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        NotificationCounter( count.value , {count.value++})
        MessageBar(count.value)

    }

}
@Composable
fun MessageBar(value: Int) {
    Card( elevation = CardDefaults.cardElevation(4.dp),) {
        Row( modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.harsh ),
                contentDescription = " ",
                modifier =  Modifier.padding(4.dp).size(25.dp)
            )
            Text(text = "Message send so far  = $value")

        }
    }
}