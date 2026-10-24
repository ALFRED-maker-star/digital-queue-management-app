package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.QuickQueueApp
import com.example.ui.theme.QuickQueueTheme
import com.example.ui.theme.SlateBackground
import com.example.ui.viewmodel.QueueViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: QueueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickQueueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SlateBackground
                ) {
                    QuickQueueApp(viewModel = viewModel)
                }
            }
        }
    }
}
