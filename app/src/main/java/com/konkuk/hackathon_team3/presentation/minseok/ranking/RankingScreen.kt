package com.konkuk.hackathon_team3.presentation.minseok.ranking

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.minseok.RankingItem
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme

@Composable
fun RankingRoute(
    navigateToRecordWrite: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RankingViewModel = viewModel()

) {
    val uiState by viewModel.uiState.collectAsState()

    RankingScreen(
        uiState = uiState,
        navigateToRecordWrite = navigateToRecordWrite,
        modifier = modifier
    )
}

@Composable
fun RankingScreen(
    navigateToRecordWrite: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: RankingUiState = RankingUiState()
    ) {
    Column(modifier = modifier) {
        GasTopbar(
            backButtonClicked = {}
        )
        Spacer(modifier = Modifier.height(11.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(width = 1.dp, color = Color(0xFF997C90), shape = RoundedCornerShape(16.dp))
                .roundedBackgroundWithPadding(
                    padding = PaddingValues(vertical = 24.dp, horizontal = 21.dp),
                    cornerRadius = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "이번주 경품")
            Spacer(modifier = Modifier.height(19.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_price),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(text = "심부름 면제권")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(width = 1.dp, color = Color(0xFF997C90), shape = RoundedCornerShape(16.dp))
                .roundedBackgroundWithPadding(
                    padding = PaddingValues(vertical = 24.dp, horizontal = 21.dp),
                    cornerRadius = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "이번주 순위")
            Text(text = "2025.08.14 ~ 2025.08.20")
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
            navigateToRecordWrite = {},
        )
    }
}