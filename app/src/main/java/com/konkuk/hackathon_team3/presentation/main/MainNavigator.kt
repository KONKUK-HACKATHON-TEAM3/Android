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

fun NavController.navigateToAddFamily() {
    navigate("addFamily") {
        launchSingleTop = true
        popUpTo("addFamily") { inclusive = true }
    }
}

fun NavController.navigateToCalendar() {
    navigate("calendar") {
        launchSingleTop = true
        popUpTo("calendar") { inclusive = true }
    }
}