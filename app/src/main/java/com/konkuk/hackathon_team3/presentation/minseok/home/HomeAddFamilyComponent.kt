package com.konkuk.hackathon_team3.presentation.minseok.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.sp
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.model.HomeFamilyData
import com.konkuk.hackathon_team3.presentation.model.ProfileType
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle
import com.konkuk.hackathon_team3.ui.theme.regularStyle

@Composable
fun HomeAddFamilyComponent(
    onClick: () -> Unit,
    familyList: List<HomeFamilyData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .gasComponentDesign()
            .padding(vertical = 24.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "가족", style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle, fontSize = 14.sp)
        Spacer(Modifier.height(20.dp))
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            color = if (it.nickname == "나") Color(0xFFFF8514) else Color.Black,
                            text = it.nickname,
                            style = KONKUKHACKATHONTEAM3Theme.typography.regularStyle,
                            fontSize = 9.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(
                            imageVector = ImageVector.vectorResource(it.profileEnum.profileImage),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
            Icon(
                modifier = Modifier
                    .noRippleClickable(onClick)
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_plus),
                contentDescription = null,
                tint = Color.Unspecified
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
                profileEnum = ProfileType.MOTHER
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = ProfileType.MOTHER
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = ProfileType.MOTHER
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = ProfileType.MOTHER
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = ProfileType.MOTHER
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = ProfileType.MOTHER
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = ProfileType.MOTHER
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = ProfileType.MOTHER
            ),
            HomeFamilyData(
                nickname = "신민석",
                profileEnum = ProfileType.MOTHER
            ),
        ),
        onClick = {}
    )
}