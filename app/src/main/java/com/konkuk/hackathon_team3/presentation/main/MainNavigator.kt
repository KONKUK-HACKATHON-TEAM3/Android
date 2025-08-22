package com.konkuk.hackathon_team3.presentation.main

import androidx.navigation.NavController

fun NavController.navigateToRanking() {
    navigate("Ranking") {
        launchSingleTop = true
        popUpTo("Ranking") { inclusive = true }
    }
}

fun NavController.navigateToGasWriting() {
    navigate("gasWriting") {
        launchSingleTop = true
        popUpTo("gasWriting") { inclusive = true }
    }
}