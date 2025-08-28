package com.konkuk.hackathon_team3.presentation.minseo.onboarding

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.konkuk.hackathon_team3.presentation.minseo.component.TransparentHintTextField
import com.konkuk.hackathon_team3.presentation.util.AppGradients
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle

@Composable
fun OnBoardingEnterNickNameRoute(
    navigateToOnBoardingEnterProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnBoardingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OnBoardingEnterNickNameScreen(
        nickname = uiState.nickname,
        onNicknameChange = viewModel::updateNickname,
        navigateToOnBoardingEnterProfile = navigateToOnBoardingEnterProfile,
        modifier = modifier,
    )
}

@Composable
fun OnBoardingEnterNickNameScreen(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    navigateToOnBoardingEnterProfile: () -> Unit,
    modifier: Modifier = Modifier,
    pageType: PageType = PageType.ENTER_NICKNAME,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 104.dp, bottom = 70.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = pageType.title,
                fontSize = 20.sp,
                style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle,
                modifier = Modifier.padding(start = 32.dp)
            )
            Spacer(Modifier.height(30.dp))

            TransparentHintTextField(
                value = nickname,
                onValueChange = {
                    if (it.length <= 6) onNicknameChange(it)
                },
                hint = "홍길동",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .noRippleClickable {
                    if (nickname.isNotEmpty())
                        navigateToOnBoardingEnterProfile()
                }
                .padding(horizontal = 16.dp)
                .then(
                    if (nickname.isNotEmpty())
                        Modifier.border(
                            width = 2.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    else
                        Modifier
                )
                .roundedBackgroundWithPadding(
                    backgroundBrush = (if (nickname.isNotEmpty()) AppGradients.Orange
                    else
                        SolidColor(Color(0xFFB6B6B6).copy(alpha = 0.2f))),
                    cornerRadius = 12.dp,
                    padding = PaddingValues(vertical = 16.dp)
                )
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pageType.buttonText,
                color = (if (nickname.isNotEmpty()) Color.White else Color(0xFFCFCFCF)),
                fontSize = 20.sp,
                style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewOnBoardingEnterNickNameScreen() {
    KONKUKHACKATHONTEAM3Theme {
        OnBoardingEnterNickNameScreen(
            navigateToOnBoardingEnterProfile = {},
            nickname = "GAS2025E",
            onNicknameChange = {},
            pageType = PageType.ENTER_NICKNAME
        )
    }
}