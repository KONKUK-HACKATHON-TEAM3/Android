package com.konkuk.hackathon_team3.presentation.minseo.onboarding

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ProfileType(
    @DrawableRes val profileImage: Int
) {
    FATHER(
        profileImage = R.drawable.ic_father
    ),
    MOTHER(
        profileImage = R.drawable.ic_mother
    ),
    GRANDFATHER(
        profileImage = R.drawable.ic_grandfather
    ),
    GRANDMOTHER(
        profileImage = R.drawable.ic_grandmother
    ),
    BOY(
        profileImage = R.drawable.ic_boy
    ),
    GIRL(
        profileImage = R.drawable.ic_girl
    ),
    MAN_1(
        profileImage = R.drawable.ic_man_1
    ),
    WOMAN_1(
        profileImage = R.drawable.ic_woman_1
    ),
    MAN_2(
        profileImage = R.drawable.ic_man_2
    ),
    WOMAN_2(
        profileImage = R.drawable.ic_woman_2
    )
}

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