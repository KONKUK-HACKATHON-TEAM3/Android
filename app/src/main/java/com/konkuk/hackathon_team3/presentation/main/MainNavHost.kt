package com.konkuk.hackathon_team3.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.konkuk.hackathon_team3.presentation.minseo.MinseoRoute
import com.konkuk.hackathon_team3.presentation.minseok.MinseokRoute

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = "minseo"
        ) {
            composable(route = "minseo") {
                MinseoRoute(
                    navigateToMinseok = { navController.navigateToMinseok() }
                )
            }

            composable(route = "minseok") {
                MinseokRoute(
                    navigateToMinseo = { navController.navigateToMinseo() }
                )
            }
        }
    }
}