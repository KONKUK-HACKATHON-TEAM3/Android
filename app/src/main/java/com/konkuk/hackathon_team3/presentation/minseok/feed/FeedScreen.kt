package com.konkuk.hackathon_team3.presentation.minseok.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.hackathon_team3.presentation.minseo.detailgas.DetailGasAnimationCard
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import java.time.LocalDate

@Composable
fun FeedRoute(
    dateArg: String?,
    onCloseAll: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(dateArg) {
        dateArg?.let {
            val date = LocalDate.parse(it)
            viewModel.loadFeed(date = date)
        }
    }

    FeedScreen(
        uiState = uiState,
        onCloseAll = onCloseAll,
        onToggleLike = { id, liked -> viewModel.toggleLike(id, liked) },
        modifier = modifier
    )
}

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    onCloseAll: () -> Unit,
    onToggleLike: (Long, Boolean) -> Unit,
    uiState: FeedUiState = FeedUiState()
) {
    var showDetail by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showDetail = true
    }

    DetailGasAnimationCard(
        uiState = uiState,
        visible = showDetail,
        onDismiss = {
            showDetail = false
            onCloseAll()
        },
        onToggleLike = onToggleLike,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewFeedScreen() {
    KONKUKHACKATHONTEAM3Theme {
        FeedScreen(
            onCloseAll = {},
            onToggleLike = { _, _ -> },
            uiState = FeedUiState()
        )
    }
}