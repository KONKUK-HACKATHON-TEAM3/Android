package com.konkuk.hackathon_team3.presentation.minseo.component

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle

@Composable
fun TransparentHintTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = Color.Black,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold
        ),
        placeholder = {
            Text(
                text = hint,
                color = Color(0xFFBDBDBD),
                fontSize = 40.sp,
                style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )
    )
}
