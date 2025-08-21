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
import com.konkuk.hackathon_team3.presentation.minseok.RecordWriteRoute

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
                    navigateToMinseo = { navController.navigateToMinseo() },
                    navigateToMinseok = { navController.navigateToMinseok() }
                )
            }

            composable(route = "minseo") {
                MinseoRoute(
                    navigateToRecordWrite = { navController.navigateToRecordWrite() }
                )
            }

            composable(route = "recordWrite") {
                RecordWriteRoute(
                    navigateToMinseo = { navController.navigateToMinseo() }
                )
            }
        }
    }
}