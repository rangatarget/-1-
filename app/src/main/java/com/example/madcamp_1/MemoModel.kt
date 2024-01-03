package com.example.madcamp_1

import android.graphics.Bitmap
import android.service.controls.templates.ThumbnailTemplate

data class MemoModel(
    val thumbnail: Bitmap,
    val drawContent : MutableList<ColoredPath>,
    val textContent : String,
    val title: String,
    val date: String
)
