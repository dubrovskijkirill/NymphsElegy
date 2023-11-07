package com.nymp.phselgy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SourceLockedOrientationActivity")
@Composable
fun MainScreen(mainViewModel: MainViewModel = hiltViewModel()) {
    val activity = LocalContext.current as Activity
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    Scaffold {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "menu") {
            composable("game") {
                GameScreen(score = mainViewModel.score, setScore = mainViewModel::saveScore) {
                    navController.popBackStack()
                }
            }
            composable("menu") {
                MenuScreen {
                    navController.navigate("game")
                }
            }
        }
    }
}