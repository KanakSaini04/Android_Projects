package com.codexcraft.lensora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.codexcraft.lensora.core.navigation.LensoraNavGraph
import com.codexcraft.lensora.core.theme.LensoraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle incoming App Links (Privacy / Terms)
        val deepLinkPath = intent?.data?.path

        setContent {
            LensoraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.codexcraft.lensora.core.theme.MatteBlack
                ) {
                    LensoraNavGraph(deepLinkPath = deepLinkPath)
                }
            }
        }
    }
}