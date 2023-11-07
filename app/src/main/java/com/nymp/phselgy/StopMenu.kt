package com.nymp.phselgy

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun BoxScope.StopMenu(
    onMenuPressed: () -> Unit,
    onButtonPressed: () -> Unit,
    buttonText: String,
    title: String,
    highScore: Int,
    score: Int
) {
    val activity = LocalContext.current as Activity
    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.7f))
            .align(Alignment.Center)
            .padding(20.dp)
            .zIndex(2f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Highest score: $highScore", color = Color.Black)
        Text(text = "Your score: $score", color = Color.Black)
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onButtonPressed() },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color(0xFF7B32B7)
            ),
            modifier = Modifier.fillMaxWidth(0.8f)

        ) {
            Text(text = buttonText)
        }
        Button(
            onClick = { onMenuPressed() },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color(0xFF7B32B7),
            ),
            modifier = Modifier.fillMaxWidth(0.8f)

        ) {
            Text(text = "Menu")
        }
        Button(
            onClick = { activity.finish() },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color(0xFF7B32B7)
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Quit")
        }
        
    }
}