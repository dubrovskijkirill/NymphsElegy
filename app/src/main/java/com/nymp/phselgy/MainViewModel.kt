package com.nymp.phselgy

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
): ViewModel() {

    var score by mutableStateOf(0)

    init {
        score = getScoreFromSp()
    }

    fun saveScore(score: Int) {
        if (score > getScoreFromSp()) {
            sharedPreferences.edit().putInt("Score", score).apply()
            this.score = score

        }
    }

    private fun getScoreFromSp(): Int {
        return sharedPreferences.getInt("Score", 0)
    }}