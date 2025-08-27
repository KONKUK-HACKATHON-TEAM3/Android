package com.konkuk.hackathon_team3.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KONKUKHACKATHONTEAM3Theme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavHost(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.White)
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
