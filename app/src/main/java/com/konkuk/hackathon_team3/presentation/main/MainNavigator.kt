package com.konkuk.hackathon_team3.presentation.main

import androidx.navigation.NavController

fun NavController.navigateToOnBoardingEnterCode(){
    navigate("enterCode"){
        launchSingleTop = true
        popUpTo("onBoarding") { inclusive = true }
    }
}

fun NavController.navigateToOnBoardingEnterNickname() {
    navigate("enterNickname") {
        launchSingleTop = true
        popUpTo("onBoarding") { inclusive = true }
    }
}

fun NavController.navigateToOnBoardingEnterProfile() {
    navigate("enterProfile") {
        launchSingleTop = true
        popUpTo("onBoarding") { inclusive = true }
    }
}

fun NavController.navigateToHome() {
    navigate("main") {
        launchSingleTop = true
        popUpTo("main") { inclusive = true }
    }
}

fun NavController.navigateToRanking() {
    navigate("ranking") {
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

fun NavController.navigateToAlarm() {
    navigate("alarm") {
        launchSingleTop = true
        popUpTo("alarm") { inclusive = true }
    }
}

fun NavController.navigateToFeed() {
    navigate("feed") {
        launchSingleTop = true
        popUpTo("feed") { inclusive = true }
    }
}