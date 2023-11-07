package com.nymp.phselgy

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt


@Composable
fun Joystick(modifier: Modifier = Modifier, game: Game) {
    var pointerOffset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    Box(modifier = modifier
        .size(150.dp)
        .pointerInput(game.gameStatus) {
            val radius = this.size.height / 2f - this.size.height / 6f
            if (game.gameStatus is GameStatus.Playing)
                detectDragGestures(
                    onDragStart = {
                        pointerOffset = Offset(it.x - radius * 4 / 3, it.y - radius * 4 / 3)
                        game.gameStarted = true
                    },
                    onDrag = { _, dragAmount ->
                        pointerOffset += dragAmount
                        val distance = sqrt(pointerOffset.x.pow(2) + pointerOffset.y.pow(2))

                        if (distance < radius) {
                            game.offset = pointerOffset
                            return@detectDragGestures
                        }
                        val x = (pointerOffset.x / distance) * radius
                        val y = (pointerOffset.y / distance) * radius

                        game.offset = Offset(x, y)
                    },
                    onDragEnd = {
                        game.offset = Offset.Zero
                        pointerOffset = Offset.Zero

                    }
                )
            else {
                game.offset = Offset.Zero
                pointerOffset = Offset.Zero
            }
        }
        .drawBehind {
            drawCircle(
                Color.Black.copy(alpha = 0.2f),
                radius = this.size.height / 2,
                center = this.center,
            )
            drawCircle(
                Color.Black.copy(alpha = 0.7f),
                radius = this.size.height / 6,
                center = (this.center + game.offset)
            )
        }) {

    }
}


