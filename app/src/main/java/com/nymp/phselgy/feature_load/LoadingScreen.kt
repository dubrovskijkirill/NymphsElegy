package com.nymp.phselgy.feature_load

import android.provider.Settings
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nymp.phselgy.R


@Composable
fun LoadingScreen(
    openFirst: () -> Unit,
    openSecond: () -> Unit,
    viewModel: NympthLoadingViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {

        val adbEnabled = Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.ADB_ENABLED, 0
        ) == 1

        viewModel.nympthLoadingRepository.loadNympthGame(
            openGame = { openFirst() },
            openWeb = { openSecond() },
            adbEnabled = adbEnabled
        )
    }
    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.back),
            contentDescription = "background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        val infiniteTransition = rememberInfiniteTransition(label = "angle")
        val angle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = InfiniteRepeatableSpec(tween(1000, easing = LinearEasing)),
            label = "angle"
        )
        Image(
            painter = painterResource(id = R.drawable.mushroom), contentDescription = "clever",
            modifier = Modifier
                .size(88.dp)
                .graphicsLayer {
                    rotationZ = angle
                }
        )
    }
}