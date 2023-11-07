package com.nymp.phselgy

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import java.text.DecimalFormat

@Composable
fun GameScreen(
    score: Int,
    setScore: (Int) -> Unit,
    goToMenu: () -> Unit
) {
    Box {
        Image(
            painter = painterResource(id = R.drawable.battleground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            val density = LocalDensity.current
            val game = remember {
                Game(
                    with(density) { maxWidth.toPx() },
                    with(density) { maxHeight.toPx() },
                    with(density) { Size(100.dp.toPx(), 100.dp.toPx()) },
                    with(density) { Size(64.dp.toPx(), 98.dp.toPx()) },
                    with(density) { Size(58.dp.toPx(), 98.dp.toPx()) },
                    with(density) { 75.dp.toPx() }
                )
            }
            Column(modifier = Modifier.zIndex(2f)) {
                Text(
                    text = "Time left: ${DecimalFormat("#,##0.00").format(game.timer)}",
                    fontWeight = Bold,
                    color = Color.White
                )
                Text(
                    text = "Score: ${game.score}",
                    fontWeight = Bold,
                    color = Color.White
                )
            }

            LaunchedEffect(key1 = Unit) {
                while (true) {
                    withFrameNanos {
                        game.update(it)
                    }
                }
            }

            if (game.gameStatus is GameStatus.Starting) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.7f))
                        .align(Alignment.Center)
                        .padding(20.dp)
                        .zIndex(2f)
                ) {
                    Text(
                        text = "Collect as much crystals as you can in 60 seconds. Be carefully with grown mushrooms. If you touch them the game will be over",
                        color = Color.Black
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { game.startGame() },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color(0xFF7B32B7)
                            ),
                            modifier = Modifier.weight(1f)

                        ) {
                            Text(text = "Start")
                        }
                        Spacer(modifier = Modifier.width(30.dp))
                        Button(
                            onClick = { goToMenu() },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color(0xFF7B32B7)
                            ),
                            modifier = Modifier.weight(1f)

                        ) {
                            Text(text = "Menu")
                        }
                    }

                }
            }
            if (game.gameStatus is GameStatus.Playing) {
                Button(
                    onClick = { game.gameStatus = GameStatus.Pause }, modifier = Modifier.align(
                        Alignment.TopEnd
                    )
                ) {
                    Text(text = "Pause")
                }

                if (!game.gameStarted) {
                    Text(
                        text = "Use Joystick to move", modifier = Modifier.align(Alignment.Center),
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }

            if (game.gameStatus is GameStatus.Pause) {
                StopMenu(
                    onMenuPressed = { goToMenu() },
                    onButtonPressed = { game.gameStatus = GameStatus.Playing },
                    buttonText = "Resume",
                    title = "Pause",
                    highScore = score,
                    score = game.score
                )
            }

            if (game.gameStatus is GameStatus.End) {
                LaunchedEffect(key1 = Unit) {
                    setScore(game.score)
                }
                StopMenu(
                    onMenuPressed = { goToMenu() },
                    onButtonPressed = { game.startGame() },
                    buttonText = "Try again",
                    title = "Game Over",
                    highScore = score,
                    score = game.score
                )
            }

            Image(
                painter = painterResource(id = R.drawable.nympth1),
                contentDescription = null,
                modifier = Modifier
                    .width(64.dp)
                    .aspectRatio(214f / 325f)
                    .graphicsLayer {
                        translationX = game.nymphOffset.x
                        translationY += game.nymphOffset.y
                        scaleX = if (game.offset.x < 0) 1f else -1f
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.another_crystal),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .height(100.dp)
                    .graphicsLayer {
                        translationX = game.crystalOffset.x
                        translationY = game.crystalOffset.y
                    }
            )

            val image = painterResource(id = R.drawable.mushroom)
            game.mushrooms.forEach {
                if (it.isVisible) {
                    Image(
                        painter = image,
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .height(100.dp)
                            .graphicsLayer {
                                translationX = it.x
                                translationY = it.y
                                scaleX = it.scale.coerceAtMost(1f)
                                scaleY = it.scale.coerceAtMost(1f)
                            }
                            .drawBehind {
                                if (it.scale >= 1f) {
                                    drawCircle(
                                        Color(0xD1123123),
                                        radius = game.mushRoomSize.height / 2f
                                    )
                                }
                            }
                    )
                }
            }

            Joystick(Modifier.align(Alignment.BottomEnd), game)
        }
    }
}