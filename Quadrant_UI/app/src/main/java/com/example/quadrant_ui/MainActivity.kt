package com.example.quadrant_ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.quadrant_ui.ui.theme.Quadrant_UITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Quadrant_UITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ComposeQuadrant(
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}
@Composable
fun ComposeQuadrant(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()){
        Row(modifier = Modifier.weight(1f)) {
            QuadrantItem(
                title = "Text composable",
                description = "Displays text and follows the recommended Material Design guidelines.",
                color = Color(0xFFEADDFF),
                modifier = Modifier.weight(1f)
            )
            QuadrantItem(
                title = "Image composable",
                description = "Creates a composable that lays out and draws a given Painter class object.",
                color = Color(0xFFD0BCFF),
                modifier = Modifier.weight(1f)
            )
        }
            Row(modifier = Modifier.weight(1f)) {
                QuadrantItem(
                    title = "Row composable",
                    description = "A layout composable that places its children in a horizontal sequence.",
                    color = Color(0xFFB69DF8),
                    modifier = Modifier.weight(1f)
                )
                QuadrantItem(
                    title = "Column composable",
                    description = "A layout composable that places its children in a vertical sequence.",
                    color = Color(0xFFF6EDFF),
                    modifier = Modifier.weight(1f)
                )
        }
}}
@Composable
fun QuadrantItem(title: String, description: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
            .background(color)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = description,
            textAlign = TextAlign.Center,
        )
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Quadrant_UITheme {
        ComposeQuadrant()
    }
}

