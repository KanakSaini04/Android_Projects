package com.example.calci

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.SoundEffectConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter

data class CalcHistory(val expression: String, val result: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CalciApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalciApp() {

    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    val historyList = remember { mutableStateListOf<CalcHistory>() }

    var isDark by remember { mutableStateOf(true) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var dominantColor by remember { mutableStateOf(Color.Cyan) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("History", modifier = Modifier.padding(16.dp), fontSize = 20.sp)

                LazyColumn {
                    items(historyList.reversed()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expression = it.expression
                                    result = it.result
                                    scope.launch { drawerState.close() }
                                }
                                .padding(12.dp)
                        ) {
                            Text(it.expression, color = Color.Gray)
                            Text(it.result, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            // 🌫️ Background
            imageUri?.let {
                val painter = rememberAsyncImagePainter(it)

                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp)
                )

                if (painter.state is AsyncImagePainter.State.Success) {
                    val drawable = (painter.state as AsyncImagePainter.State.Success).result.drawable
                    val bitmap = (drawable as BitmapDrawable).bitmap

                    Palette.from(bitmap).generate { palette ->
                        palette?.getVibrantColor(android.graphics.Color.CYAN)?.let {
                            dominantColor = Color(it)
                        }
                    }
                }
            } ?: run {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(if (isDark) Color(0xFF0B1F1F) else Color.White)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // 🔝 Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("☰", fontSize = 24.sp, modifier = Modifier.clickable {
                        scope.launch { drawerState.open() }
                    })

                    Text("🎨", fontSize = 24.sp, modifier = Modifier.clickable {
                        imagePicker.launch("image/*")
                    })
                }

                // 🔢 Display
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(expression, fontSize = 28.sp, color = Color.White.copy(0.7f))
                    Text(result, fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                // 🔘 Buttons
                Column {

                    Row { Btn("AC", dominantColor) { expression = ""; result = "" }
                        Btn("(", dominantColor) { expression += "(" }
                        Btn(")", dominantColor) { expression += ")" }
                        Btn("÷", dominantColor) { expression += "/" } }

                    Row { Btn("7", dominantColor) { expression += "7" }
                        Btn("8", dominantColor) { expression += "8" }
                        Btn("9", dominantColor) { expression += "9" }
                        Btn("×", dominantColor) { expression += "*" } }

                    Row { Btn("4", dominantColor) { expression += "4" }
                        Btn("5", dominantColor) { expression += "5" }
                        Btn("6", dominantColor) { expression += "6" }
                        Btn("-", dominantColor) { expression += "-" } }

                    Row { Btn("1", dominantColor) { expression += "1" }
                        Btn("2", dominantColor) { expression += "2" }
                        Btn("3", dominantColor) { expression += "3" }
                        Btn("+", dominantColor) { expression += "+" } }

                    Row {
                        Btn("0", dominantColor, 2f) { expression += "0" }
                        Btn(".", dominantColor) { expression += "." }
                        Btn("=", dominantColor) {
                            val res = calculate(expression)
                            result = res

                            if (historyList.size >= 30) historyList.removeAt(0)
                            historyList.add(CalcHistory(expression, res))
                        }
                    }
                }
            }
        }
    }
}

fun calculate(exp: String): String {
    return try {
        ExpressionBuilder(exp).build().evaluate().toString()
    } catch (e: Exception) {
        "Error"
    }
}

@Composable
fun RowScope.Btn(
    text: String,
    color: Color,
    weight: Float = 1f,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(if (pressed) 0.9f else 1f)

    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    val bg =
        if (text in listOf("+","-","×","÷","="))
            color.copy(alpha = 0.8f)
        else Color.White.copy(0.1f)

    Box(
        modifier = Modifier
            .padding(6.dp)
            .weight(weight)
            .aspectRatio(1f)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        tryAwaitRelease()
                        pressed = false
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 22.sp, color = Color.White)
    }
}