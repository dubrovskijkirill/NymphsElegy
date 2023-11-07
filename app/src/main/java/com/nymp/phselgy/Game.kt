package com.nymp.phselgy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class Game(
    private val maxWidth: Float,
    private val maxHeight: Float,
    val mushRoomSize: Size,
    private val nymphSize: Size,
    private val nymphBoxSize: Size,
    private val actualMushroomWidth: Float
) {
    var offset by mutableStateOf(Offset(0f, 0f))

    var nymphOffset by mutableStateOf(Offset.Zero)

    private var previousTime = 0L

    private var startTime = 0L
    var timer by mutableStateOf(60f)
    private var mushroomProgress = 0f

    private var progressMultiplier = 1f

    var gameStatus by mutableStateOf<GameStatus>(GameStatus.Starting)

    private var mushroomIndexToRemove: Int? = null
    var gameStarted = false
    var mushrooms = mutableStateListOf<MushroomState>()
    var score by mutableStateOf(0)
    var crystalOffset = Offset(maxWidth / 2, maxHeight / 2)

    fun startGame() {
        mushrooms.clear()
        crystalOffset = Offset(maxWidth / 2, maxHeight / 2)
        nymphOffset = Offset.Zero
        gameStarted = false
        mushroomProgress = 0f
        progressMultiplier = 1f
        timer = 60f
        score = 0
        gameStatus = GameStatus.Playing
    }

    fun update(time: Long) {
        if (previousTime == 0L) {
            previousTime = time
            startTime = time
            return
        }
        val delta = ((time - previousTime) / 1E8).toFloat()

        previousTime = time

        if (gameStatus !is GameStatus.Playing || !gameStarted) return
        timer -= delta / 10

        if (timer <= 0) {
            gameStatus = GameStatus.End
        }

        if (mushroomProgress >= 1) {
            mushroomProgress -= 1
            mushrooms.add(
                MushroomState(
                    x = Random.nextFloat() * (maxWidth - mushRoomSize.height),
                    y = Random.nextFloat() * (maxHeight - mushRoomSize.height)
                )
            )
        }

        if (doCirclesIntersect(
                circleCenterX = crystalOffset.x + mushRoomSize.width / 2.0,
                circleCenterY = crystalOffset.y + mushRoomSize.height / 2.0,
                circleRadius = mushRoomSize.height / 2.0,
                ellipseCenterX = nymphOffset.x + nymphSize.width / 2.0,
                ellipseCenterY = nymphOffset.y + nymphSize.height / 2.0,
                ellipseRadiusX = nymphBoxSize.width / 2.0
            )
        ) {
            var newOffset: Offset
            do {
                newOffset = Offset(
                    Random.nextFloat() * (maxWidth - mushRoomSize.height),
                    Random.nextFloat() * (maxHeight - mushRoomSize.height)
                )
            } while (offsetDistanceSquared(newOffset, crystalOffset)  < 3 * mushRoomSize.height)
            crystalOffset = newOffset
            score += 1
        }

        (0 until mushrooms.size).forEach { index ->
            val mushroom = mushrooms[index]
            if (mushroom.isVisible) {
                if (mushroom.scale > 2) {
                    mushrooms[index].isVisible = false
                    mushroomIndexToRemove = index
                    progressMultiplier *= 1.01f
                }
                mushrooms[index] =
                    mushroom.copy(scale = mushroom.scale + delta * 0.05f)
                if (mushroom.scale >= 1) {
                    if (
                        doCirclesIntersect(
                            circleCenterX = mushroom.x + actualMushroomWidth / 2.0,
                            circleCenterY = mushroom.y + mushRoomSize.height / 2.0,
                            circleRadius = mushRoomSize.height / 2.0,
                            ellipseCenterX = nymphOffset.x + nymphSize.width / 2.0,
                            ellipseCenterY = nymphOffset.y + nymphSize.height / 2.0,
                            ellipseRadiusX = nymphBoxSize.width / 2.0
                        )
                    ) {
                        gameStatus = GameStatus.End
                    }
                }
            }
        }
        if (mushroomIndexToRemove != null) {
            mushrooms.removeAt(mushroomIndexToRemove!!)
            mushroomIndexToRemove = null
        }
        mushroomProgress += progressMultiplier * (delta / 10)
        nymphOffset = (nymphOffset + offset.times(delta / 3)).coerceAt(0f, maxWidth-nymphSize.width, maxHeight - nymphSize.height)
    }
}


data class MushroomState(
    var isVisible: Boolean = true,
    val scale: Float = 0f,
    val x: Float,
    val y: Float
)

fun doCirclesIntersect(
    circleCenterX: Double, circleCenterY: Double, circleRadius: Double,
    ellipseCenterX: Double, ellipseCenterY: Double, ellipseRadiusX: Double
): Boolean {
    val distanceBetweenCenters =
        sqrt((circleCenterX - ellipseCenterX).pow(2.0) + (circleCenterY - ellipseCenterY).pow(2.0))
    return distanceBetweenCenters <= (circleRadius + ellipseRadiusX) && distanceBetweenCenters >= (circleRadius - ellipseRadiusX)
}


sealed interface GameStatus {
    data object Playing : GameStatus
    data object Pause : GameStatus
    data object End : GameStatus
    data object Starting : GameStatus
}

fun offsetDistanceSquared(offset1: Offset, offset2: Offset): Float {
    val dx = offset1.x - offset2.x
    val dy = offset1.y - offset2.y
    return (dx * dx) + (dy * dy)
}

fun  Offset.coerceAt(minValue: Float, maxValueX: Float, maxValueY: Float): Offset {
    return Offset(
        this.x.coerceIn(minValue, maxValueX),
        this.y.coerceIn(minValue, maxValueY)
    )
}