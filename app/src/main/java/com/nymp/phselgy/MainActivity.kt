package com.nymp.phselgy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.nymp.phselgy.feature_load.Son
import com.nymp.phselgy.feature_load.MainScreen
import com.nymp.phselgy.ui.theme.NymphsElegyTheme
import com.onesignal.OneSignal
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OneSignal.initWithContext(
            this,
            Son.getValue("strong")
        )
        setContent {
            NymphsElegyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

