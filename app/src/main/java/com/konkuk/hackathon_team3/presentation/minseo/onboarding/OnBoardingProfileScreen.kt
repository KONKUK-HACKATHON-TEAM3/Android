package com.konkuk.hackathon_team3.presentation.minseo.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.konkuk.hackathon_team3.presentation.model.ProfileType
import com.konkuk.hackathon_team3.presentation.util.AppGradients
import com.konkuk.hackathon_team3.presentation.util.gradientBorder
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun OnBoardingProfileRoute(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnBoardingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OnBoardingProfileScreen(
        profile = uiState.profileType,
        onProfileChange = viewModel::updateProfileType,
        navigateToHome = navigateToHome,
        modifier = modifier,
    )
}

@Composable
fun OnBoardingProfileScreen(
    profile: ProfileType?,
    onProfileChange: (ProfileType) -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    pageType: PageType = PageType.ENTER_PROFILE,
) {
    val types = remember { ProfileType.entries }
    val current = profile ?: types.first()

    val itemSize: Dp = 79.dp
    val itemSpacing: Dp = 16.dp

    var selectedIndex by rememberSaveable { mutableStateOf(types.indexOf(current)) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedIndex
    )
    val scope = rememberCoroutineScope()

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
            Spacer(Modifier.height(16.dp))

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val sidePadding = (maxWidth - itemSize) / 2

                val centeredIndex by remember {
                    derivedStateOf {
                        centeredItemIndex(listState)
                    }
                }

                LaunchedEffect(listState) {
                    snapshotFlow { listState.isScrollInProgress }
                        .map { it }
                        .collectLatest { inProgress ->
                            if (!inProgress) {
                                val center = centeredItemIndex(listState)
                                if (center != null) {
                                    selectedIndex = center
                                    onProfileChange(types[center])
                                    scope.launch {
                                        listState.animateScrollToItem(center)
                                    }
                                }
                            }
                        }
                }

                LazyRow(
                    state = listState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = sidePadding),
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing)
                ) {
                    itemsIndexed(types, key = { _, t -> t.name }) { index, type ->
                        val isSelected = index == (centeredIndex ?: selectedIndex)

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.2f else 1f,
                            label = "profileScale"
                        )

                        Box(
                            modifier = Modifier
                                .size(itemSize)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(CircleShape)
                                .then(
                                    if (isSelected)
                                        Modifier.gradientBorder(
                                            width = (3.5).dp,
                                            brush = AppGradients.Orange,
                                            shape = CircleShape
                                        )
                                    else
                                        Modifier
                                )
                                .noRippleClickable {
                                    scope.launch {
                                        listState.animateScrollToItem(index)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = type.profileImage),
                                contentDescription = type.name,
                                tint = Color.Unspecified,
                                modifier = Modifier.fillMaxSize(0.85f)
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .noRippleClickable(navigateToHome)
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
                text = pageType.buttonText,
                color = Color.White,
                fontSize = 20.sp,
                style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle
            )
        }
    }
}

private fun centeredItemIndex(state: LazyListState): Int? {
    val layout = state.layoutInfo
    val items = layout.visibleItemsInfo
    if (items.isEmpty()) return null

    val viewportCenter = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
    var min = Int.MAX_VALUE
    var targetIndex: Int? = null

    for (info in items) {
        val itemCenter = info.offset + info.size / 2
        val distance = abs(itemCenter - viewportCenter)
        if (distance < min) {
            min = distance
            targetIndex = info.index
        }
    }
    return targetIndex
}

@Preview(showBackground = true)
@Composable
private fun PreviewOnBoardingProfileScreen() {
    KONKUKHACKATHONTEAM3Theme {
        OnBoardingProfileScreen(
            navigateToHome = {},
            profile = ProfileType.FATHER,
            onProfileChange = {},
            pageType = PageType.ENTER_PROFILE
        )
    }
}