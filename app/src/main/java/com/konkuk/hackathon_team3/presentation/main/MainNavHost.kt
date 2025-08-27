package com.konkuk.hackathon_team3.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.konkuk.hackathon_team3.presentation.minseok.ranking.RankingRoute
import com.konkuk.hackathon_team3.presentation.minseok.writing.GasWritingRoute

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
            startDestination = "main"
        ) {

            composable(route = "main") {
                MainRoute(
                    navigateToRanking = { navController.navigateToRanking() },
                    navigateToGasWriting = { navController.navigateToGasWriting() }
                )
            }

            composable(route = "Ranking") {
                RankingRoute(
                    navigateToRecordWrite = { navController.navigateToGasWriting() }
                )
            }

            composable(route = "gasWriting") {
                GasWritingRoute(
                    navigateToRanking = { navController.navigateToRanking() }
                )
            }
        }
    }
}