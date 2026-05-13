package com.example.demo

import android.os.Bundle
import com.example.quadrant_ui.Quote
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.demo.screens.QuoteListItem
import com.example.demo.ui.theme.DemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val quotes = listOf(
                Quote("Time is the most valuable thing a man can spend.", "Theophrastus"),
                Quote("Stay hungry, stay foolish.", "Steve Jobs")
            )

            QuoteListScreen(quotes)
        }
    }
}
@Composable
fun QuoteListScreen(quotes: List<Quote>) {
    LazyColumn {
        items(quotes) {
            QuoteListItem(it)
        }
    }
}