package com.konkuk.hackathon_team3.presentation.main

import androidx.navigation.NavController

fun NavController.navigateToMinseo() {
    navigate("minseo") {
        launchSingleTop = true
        popUpTo("minseo") { inclusive = true }
    }
}

fun NavController.navigateToMinseok() {
    navigate("minseok") {
        launchSingleTop = true
        popUpTo("minseok") { inclusive = true }
    }
}