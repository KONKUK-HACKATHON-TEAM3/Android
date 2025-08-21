package com.konkuk.hackathon_team3.presentation.main

import androidx.navigation.NavController

fun NavController.navigateToMinseo() {
    navigate("minseo") {
        launchSingleTop = true
        popUpTo("minseo") { inclusive = true }
    }
}

fun NavController.navigateToRecordWrite() {
    navigate("recordWrite") {
        launchSingleTop = true
        popUpTo("recordWrite") { inclusive = true }
    }
}