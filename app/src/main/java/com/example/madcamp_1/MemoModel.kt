package com.example.madcamp_1

import android.graphics.Bitmap

data class MemoModel(
    val thumbnail: Bitmap,
    val drawContent : MutableList<ColoredPath>,
    val textContent : String,
    val title: String,
    val date: String
)
