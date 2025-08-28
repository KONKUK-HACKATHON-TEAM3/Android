package com.konkuk.hackathon_team3.presentation.minseok.ranking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.minseok.RankingItem
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle
import com.konkuk.hackathon_team3.ui.theme.regularStyle

@Composable
fun RankingRoute(
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RankingViewModel = viewModel()

) {
    val uiState by viewModel.uiState.collectAsState()

    RankingScreen(
        popBackStack=popBackStack,
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
fun RankingScreen(
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: RankingUiState = RankingUiState()
) {
    Column(modifier = modifier) {
        GasTopbar(
            backButtonClicked = popBackStack
        )
        Spacer(modifier = Modifier.height(11.dp))
        Column(
            modifier = Modifier
                .gasComponentDesign()
                .padding(vertical = 24.dp, horizontal = 21.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "이번주 경품",
                fontSize = 14.sp,
                style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle)
            Spacer(modifier = Modifier.height(19.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_price),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(text = uiState.price,
                    fontSize = 12.sp,
                    style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .gasComponentDesign()
                .padding(vertical = 24.dp, horizontal = 21.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "이번주 순위",
                fontSize = 14.sp,
                style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle)
            Text(text = "2025.08.25 ~ 2025.08.31",
                fontSize = 9.sp,
                style = KONKUKHACKATHONTEAM3Theme.typography.regularStyle)
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                uiState.rankingList?.forEach { rankingItem ->
                    RankingItem(
                        modifier = Modifier.fillMaxWidth(),
                        rankingData = rankingItem
                    )
                }
            }

        }

    }

}

@Preview(showBackground = true)
@Composable
private fun PreviewRankingScreen() {
    KONKUKHACKATHONTEAM3Theme {
        RankingScreen(
            popBackStack = {}
        )
    }
}