package com.konkuk.hackathon_team3.presentation.minseo.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.hackathon_team3.presentation.minseo.component.TransparentHintTextField
import com.konkuk.hackathon_team3.presentation.util.AppGradients
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme


@Composable
fun OnBoardingEnterCodeRoute(
    navigateToOnBoardingEnterNickname: () -> Unit,
    viewModel: OnBoardingViewModel,
    modifier: Modifier = Modifier
) {
    OnBoardingEnterCodeScreen(
        navigateToOnBoardingEnterNickname = navigateToOnBoardingEnterNickname,
        modifier = modifier,
    )
}

@Composable
fun OnBoardingEnterCodeScreen(
    navigateToOnBoardingEnterNickname: () -> Unit,
    modifier: Modifier = Modifier,
    pageType: PageType = PageType.ENTER_CODE,
) {
    var code by remember { mutableStateOf("") }

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
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 32.dp)
            )
            Spacer(Modifier.height(30.dp))

            TransparentHintTextField(
                value = code,
                onValueChange = { code = it },
                hint = "GAS2025E",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .noRippleClickable {
                    if (code.isNotEmpty())
                        navigateToOnBoardingEnterNickname()
                }
                .padding(horizontal = 16.dp)
                .roundedBackgroundWithPadding(
                    backgroundBrush = (if (code.isNotEmpty()) AppGradients.Orange
                    else
                        SolidColor(Color(0xFFB6B6B6))),
                    cornerRadius = 12.dp,
                    padding = PaddingValues(vertical = 16.dp)
                )
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pageType.buttonText,
                color = (if (code.isNotEmpty()) Color.White else Color(0xFFCFCFCF)),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewOnBoardingEnterCodeScreen() {
    KONKUKHACKATHONTEAM3Theme {
        OnBoardingEnterCodeScreen(
            navigateToOnBoardingEnterNickname = {},
            pageType = PageType.ENTER_CODE
        )
    }
}