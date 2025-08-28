package com.konkuk.hackathon_team3.presentation.minseok.feed

import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.data.service.ServicePool
import com.konkuk.hackathon_team3.presentation.model.FeedData
import com.konkuk.hackathon_team3.presentation.model.ProfileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FeedUiState(
    val feedList: List<FeedData> = listOf(
        FeedData(
            feedId = 1L,
            profile = ProfileType.FATHER,
            nickname = "아빠",
            text = "오늘도 즐거운 하루였어요!",
            imageUrl = "",
            tag = "일상",
            likeCount = 3,
            likeStatus = false
        ),
        FeedData(
            feedId = 1L,
            profile = ProfileType.MOTHER,
            nickname = "엄마",
            text = "아 개졸림핑핑이!",
            imageUrl = "",
            tag = "해커톤",
            likeCount = 3,
            likeStatus = true
        ),
        FeedData(
            feedId = 1L,
            profile = ProfileType.FATHER,
            nickname = "아빠",
            text = "오늘도 즐거운 하루였어요!",
            imageUrl = "",
            tag = "일상",
            likeCount = 3,
            likeStatus = false
        ),
        FeedData(
            feedId = 1L,
            profile = ProfileType.MOTHER,
            nickname = "엄마",
            text = "아 개졸림핑핑이!",
            imageUrl = "",
            tag = "해커톤",
            likeCount = 3,
            likeStatus = true
        )
    )
)

class FeedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

}