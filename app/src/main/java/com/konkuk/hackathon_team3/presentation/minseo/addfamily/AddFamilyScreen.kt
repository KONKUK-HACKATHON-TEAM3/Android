package com.konkuk.hackathon_team3.presentation.minseo.addfamily

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle

@Composable
fun AddFamilyRoute(
    popBackStack:()->Unit,
    modifier: Modifier = Modifier
) {
    AddFamilyScreen(
        popBackStack=popBackStack,
        modifier = modifier
    )
}

@Composable
fun AddFamilyScreen(
    popBackStack:()->Unit,
    modifier: Modifier = Modifier
) {
    val inviteCode = "QRSFDFESVS"
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        GasTopbar(
            backButtonClicked = popBackStack,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Column(
            modifier = modifier
                .gasComponentDesign()
                .padding(vertical = 24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "입장 코드",
                fontSize = 14.sp,
                style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle
            )
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_copy),
                    contentDescription = null,
                    modifier = Modifier
                        .noRippleClickable {
                            clipboard.setText(AnnotatedString(inviteCode))
                        }
                )
                Spacer(Modifier.width(8.dp))

                Text(
                    text = inviteCode,
                    fontSize = 20.sp,
                    style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAddFamilyScreen() {
    KONKUKHACKATHONTEAM3Theme {
        AddFamilyScreen(
            popBackStack = {},
        )
    }
}