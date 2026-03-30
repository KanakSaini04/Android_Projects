package com.example.birthday_greeting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.birthday_greeting.ui.theme.BirthDay_GreetingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirthDay_GreetingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Eternal",
                        from = "From Emma",
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, from: String) {

    Column (verticalArrangement = Arrangement.Center,modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Happy Birthday $name!",
            fontSize = 100.sp,
            lineHeight = 116.sp,
            modifier = modifier,
        )


    Text(
        text = from,
        fontSize = 40.sp,
    ) }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BirthDay_GreetingTheme {
        Greeting("Eternal",from="From Emma")
    }
}