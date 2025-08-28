package com.konkuk.hackathon_team3.presentation.minseo.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.util.AppGradients
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle
import com.konkuk.hackathon_team3.ui.theme.regularStyle

@Composable
fun StartRoute(
    navigateToOnBoardingEnterCode: () -> Unit,
    navigateToOnBoardingEnterNickName: () -> Unit,
    modifier: Modifier = Modifier
) {
    StartScreen(
        navigateToOnBoardingEnterCode = navigateToOnBoardingEnterCode,
        navigateToOnBoardingEnterNickName = navigateToOnBoardingEnterNickName,
        modifier = modifier
    )
}

@Composable
fun StartScreen(
    navigateToOnBoardingEnterCode: () -> Unit,
    navigateToOnBoardingEnterNickName: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_start_logo),
            contentDescription = "logo",
            tint = Color.Unspecified

        )
        Spacer(Modifier.height(27.dp))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_start_text),
            contentDescription = "logo",
            tint = Color.Unspecified
        )
        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 68.dp),
        ) {
            Box(
                modifier = Modifier
                    .noRippleClickable(navigateToOnBoardingEnterCode)
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .roundedBackgroundWithPadding(
                        backgroundBrush = AppGradients.Orange,
                        cornerRadius = 12.dp,
                        padding = PaddingValues(vertical = 16.dp)
                    )
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(top = 7.dp, bottom = 3.dp),
                    text = "입장하기",
                    color = Color.White,
                    fontSize = 20.sp,
                    style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle
                )
            }
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF8D8D8D),
                    thickness = 1.dp
                )

                Text(
                    text = "처음이라면",
                    color = Color(0xFF8D8D8D),
                    fontSize = 14.sp,
                    style = KONKUKHACKATHONTEAM3Theme.typography.regularStyle,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF8D8D8D),
                    thickness = 1.dp
                )
            }
            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .noRippleClickable(navigateToOnBoardingEnterNickName)
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 0.5.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),

                    text = "새로 만들기",
                    color = Color.Black,
                    fontSize = 20.sp,
                    style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewStartScreen() {
    KONKUKHACKATHONTEAM3Theme {
        StartScreen(
            navigateToOnBoardingEnterCode = {},
            navigateToOnBoardingEnterNickName = {}
        )
    }
}