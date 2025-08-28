package com.konkuk.hackathon_team3.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.konkuk.hackathon_team3.presentation.minseo.addfamily.AddFamilyRoute
import com.konkuk.hackathon_team3.presentation.minseo.alarm.AlarmRoute
import com.konkuk.hackathon_team3.presentation.minseo.calendar.GasCalendarRoute
import com.konkuk.hackathon_team3.presentation.minseo.onboarding.OnBoardingEnterCodeRoute
import com.konkuk.hackathon_team3.presentation.minseo.onboarding.OnBoardingEnterNickNameRoute
import com.konkuk.hackathon_team3.presentation.minseo.onboarding.OnBoardingProfileRoute
import com.konkuk.hackathon_team3.presentation.minseo.onboarding.OnBoardingViewModel
import com.konkuk.hackathon_team3.presentation.minseo.onboarding.StartRoute
import com.konkuk.hackathon_team3.presentation.minseok.feed.FeedRoute
import com.konkuk.hackathon_team3.presentation.minseok.home.MainRoute
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
        val onBoardingViewModel: OnBoardingViewModel = viewModel()

        NavHost(
            navController = navController,
            startDestination = "start"
        ) {

            composable(route = "start") {
                StartRoute(
                    navigateToOnBoardingEnterCode = { navController.navigateToOnBoardingEnterCode() },
                    navigateToOnBoardingEnterNickName = { navController.navigateToOnBoardingEnterNickname() }
                )
            }

            composable(route = "enterCode") {
                OnBoardingEnterCodeRoute(
                    navigateToOnBoardingEnterNickname = { navController.navigateToOnBoardingEnterNickname() },
                    viewModel = onBoardingViewModel
                )
            }

            composable(route = "enterNickname") {
                OnBoardingEnterNickNameRoute(
                    navigateToOnBoardingEnterProfile = { navController.navigateToOnBoardingEnterProfile() },
                    viewModel = onBoardingViewModel
                )
            }

            composable(route = "enterProfile") {
                OnBoardingProfileRoute(
                    navigateToHome = { navController.navigateToHome() },
                    viewModel = onBoardingViewModel
                )
            }

            composable(route = "main") {
                MainRoute(
                    navigateToRanking = { navController.navigateToRanking() },
                    navigateToGasWriting = { navController.navigateToGasWriting() },
                    navigateToCalendar = { navController.navigateToCalendar() },
                    navigateToAddFamily = { navController.navigateToAddFamily() },
                    navigateToAlarm = { navController.navigateToAlarm() },
                    navigateToFeed = { }

                )
            }

            composable(route = "ranking") {
                RankingRoute(
                    navigateToRecordWrite = { navController.navigateToGasWriting() }
                )
            }

            composable(route = "gasWriting") {
                GasWritingRoute(
                    navigateToRanking = { navController.navigateToRanking() }
                )
            }

            composable(route = "calendar") {
                GasCalendarRoute(
                    navigateToFeed = { date ->
                        navController.navigateToFeed(date)
                    }
                )
            }

            composable(route = "addFamily") {
                AddFamilyRoute()
            }

            composable(route = "alarm") {
                AlarmRoute()
            }

            composable(
                route = "feed/{date}",
                arguments = listOf(navArgument("date") { type = NavType.StringType })
            ) { backStackEntry ->
                val dateArg = backStackEntry.arguments?.getString("date")
                FeedRoute(
                    dateArg = dateArg,
                    onCloseAll = { navController.popBackStack() }
                )
            }
        }
    }
}