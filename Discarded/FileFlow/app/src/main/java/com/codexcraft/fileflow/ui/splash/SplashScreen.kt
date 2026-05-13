package com.codexcraft.fileflow.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.codexcraft.fileflow.R
@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 0.5f else 1f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "scale"
    )

    val offset by animateDpAsState(
        targetValue = if (startAnimation) (-300).dp else 0.dp,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "offset"
    )

    LaunchedEffect(Unit) {
        delay(500)
        startAnimation = true
        delay(1200)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_flow_logo),
            contentDescription = "FileFlow Logo",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .offset(y = offset)
        )
    }
}
