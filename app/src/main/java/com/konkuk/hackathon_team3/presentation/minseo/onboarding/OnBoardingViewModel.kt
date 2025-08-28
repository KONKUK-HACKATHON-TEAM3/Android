package com.konkuk.hackathon_team3.presentation.minseo.onboarding

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.model.ProfileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PageType(
    val title: String,
    val buttonText: String = "다음"
) {
    ENTER_CODE(
        title = "입장 코드"
    ),
    ENTER_NICKNAME(
        title = "닉네임"
    ),
    ENTER_PROFILE(
        title = "프로필",
        buttonText = "시작하기"
    )
}

data class OnBoardingUiState(
    val nickname: String = "",
    val profileType: ProfileType? = null
)

class OnBoardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnBoardingUiState())
    val uiState: StateFlow<OnBoardingUiState> = _uiState.asStateFlow()

    fun updateNickname(nickname: String) {
        _uiState.value = _uiState.value.copy(nickname = nickname)
    }

    fun updateProfileType(profileType: ProfileType) {
        _uiState.value = _uiState.value.copy(profileType = profileType)
    }
}