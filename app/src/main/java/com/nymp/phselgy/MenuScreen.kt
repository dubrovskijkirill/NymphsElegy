package com.nymp.phselgy

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MenuScreen(start: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.back), contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.TopStart)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Nymph's\nElegy",
                fontSize = 64.sp,
                lineHeight = 64.sp,
                letterSpacing = 3.sp,
                color = Color.White,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.drawBehind {
                    drawRoundRect(
                        color = Color(0xA3391241),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                }
            )
            Spacer(modifier = Modifier.height(100.dp))
            Button(
                onClick = start,
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(3.dp, Color.White),
                modifier = Modifier.width(200.dp)
            ) {
                Text(
                    text = "Start",
                    fontSize = 26.sp
                )
            }
            val activity = LocalContext.current as Activity
            Spacer(modifier = Modifier.height(50.dp))
            Button(
                onClick = { activity.finish() },
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(3.dp, Color.White),
                modifier = Modifier.width(200.dp)
            ) {
                Text(
                    text = "Exit",
                    fontSize = 26.sp
                )
            }
        }
    }
}