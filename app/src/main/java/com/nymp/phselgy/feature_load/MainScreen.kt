package com.nymp.phselgy.feature_load

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eme.rald.heart.loader.LoadingScreen
import com.nymp.phselgy.feature_load.additional.NympthWebViewModel
import com.nymp.phselgy.feature_load.additional.NympthWebViewScreen
import com.nymp.phselgy.MainNempthScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "nympthL") {
        composable("nympthL") {
            LoadingScreen(
                openFirst = {
                    navController.navigate("nympthG") {
                        popUpTo("nympthL") {
                            inclusive = true
                        }
                    }
                },
                openSecond = {
                    navController.navigate("nympthH") {
                        popUpTo("nympthL") {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable("nympthG") {
            MainNempthScreen()
        }
        composable("nympthH") {
            val nympthWebViewModel: NympthWebViewModel = hiltViewModel()
            NympthWebViewScreen(nympthWebViewModel) {
                navController.navigate("nympthG") {
                    popUpTo("nympthH") {
                        inclusive = true
                    }
                }
            }
        }
    }
}