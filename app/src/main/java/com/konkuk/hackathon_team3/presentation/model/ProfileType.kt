package com.konkuk.hackathon_team3.presentation.model

import androidx.annotation.DrawableRes
import com.konkuk.hackathon_team3.R

enum class ProfileType(
    @DrawableRes val profileImage: Int
) {
    FATHER(
        profileImage = R.drawable.ic_father
    ),
    MOTHER(
        profileImage = R.drawable.ic_mother
    ),
    GRANDFATHER(
        profileImage = R.drawable.ic_grandfather
    ),
    GRANDMOTHER(
        profileImage = R.drawable.ic_grandmother
    ),
    BOY(
        profileImage = R.drawable.ic_boy
    ),
    GIRL(
        profileImage = R.drawable.ic_girl
    ),
    MAN_1(
        profileImage = R.drawable.ic_man_1
    ),
    WOMAN_1(
        profileImage = R.drawable.ic_woman_1
    ),
    MAN_2(
        profileImage = R.drawable.ic_man_2
    ),
    WOMAN_2(
        profileImage = R.drawable.ic_woman_2
    )
}
