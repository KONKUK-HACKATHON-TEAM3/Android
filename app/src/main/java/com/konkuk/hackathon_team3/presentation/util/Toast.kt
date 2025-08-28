package com.konkuk.hackathon_team3.presentation.util

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.konkuk.hackathon_team3.R

fun showCustomToast(context: Context, message: String) {
    val inflater = LayoutInflater.from(context)
    val layout = inflater.inflate(R.layout.toast_custom, null)

    val icon = layout.findViewById<ImageView>(R.id.toast_icon)
    val text = layout.findViewById<TextView>(R.id.toast_text)

    icon.setImageResource(R.drawable.ic_check)
    text.text = message

    Toast(context).apply {
        duration = Toast.LENGTH_SHORT
        view = layout
        show()
    }
}


