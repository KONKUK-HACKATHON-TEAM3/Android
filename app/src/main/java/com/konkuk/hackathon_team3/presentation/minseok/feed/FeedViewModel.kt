package com.konkuk.hackathon_team3.presentation.minseok.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.hackathon_team3.data.mapper.toFeedData
import com.konkuk.hackathon_team3.data.service.ServicePool
import com.konkuk.hackathon_team3.presentation.model.FeedData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class FeedUiState(
    val isLoading: Boolean = false,
    val feedList: List<FeedData> = emptyList()
)

class FeedViewModel : ViewModel() {
    private val feedService by lazy { ServicePool.feedService }

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    fun loadFeed(memberId: Long = 2, date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val data = feedService.getFeed(memberId, date)

                _uiState.value = _uiState.value.copy(
                    feedList = data.feedList.map { it.toFeedData() },
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("FeedViewModel", "loadFeed failed: ${e.message}", e)
            }
        }
    }

    fun toggleLike(feedId: Long, newLiked: Boolean, memberId: Long = 2) {
        _uiState.value = _uiState.value.copy(
            feedList = _uiState.value.feedList.map { f ->
                if (f.feedId == feedId) {
                    f.copy(
                        likeStatus = newLiked,
                        likeCount = (f.likeCount + if (newLiked) 1 else -1).coerceAtLeast(0)
                    )
                } else f
            }
        )
        viewModelScope.launch {
            try {
                if (newLiked) {
                    feedService.postLike(feedId, memberId)
                } else {
                    feedService.deleteLike(feedId, memberId)
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "toggleLike failed: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    feedList = _uiState.value.feedList.map { f ->
                        if (f.feedId == feedId) {
                            f.copy(
                                likeStatus = !newLiked,
                                likeCount = (f.likeCount + if (newLiked) -1 else +1).coerceAtLeast(0)
                            )
                        } else f
                    }
                )
            }
        }
    }

}