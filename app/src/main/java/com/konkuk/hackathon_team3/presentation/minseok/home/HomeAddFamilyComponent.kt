package com.konkuk.hackathon_team3.presentation.minseok.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.model.HomeFamilyData
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign

@Composable
fun HomeAddFamilyComponent(familyList: List<HomeFamilyData>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .gasComponentDesign()
            .padding(vertical = 24.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "가족")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = familyList
                ) {
                    Column {
                        Text(text = it.nickname)
                        Text(text = it.profileEnum)
                    }
                }
            }
            Icon(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                imageVector = ImageVector.vectorResource(
                    R.drawable.ic_plus
                ), contentDescription = null, tint = Color.Unspecified
            )
        }
    }
}

@Preview
@Composable
private fun HomeAddFamilyComponentPreview() {
    HomeAddFamilyComponent(
        familyList = listOf(
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = "이넘입니다"
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = "이넘입니다"
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = "이넘입니다"
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = "이넘입니다"
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = "이넘입니다"
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = "이넘입니다"
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = "이넘입니다"
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = "이넘입니다"
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = "이넘입니다"
            ),
        )
    )
}