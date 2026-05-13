package com.codexcraft.fileflow.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.codexcraft.fileflow.app.navigation.AppNavGraph
import com.codexcraft.fileflow.core.designsystem.theme.FileFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FileFlowTheme {
                AppNavGraph()
            }
        }
    }
}
