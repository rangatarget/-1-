package com.example.madcamp_1

import android.graphics.Bitmap

data class DrawingMemoModel(
    val thumbnail : Bitmap,
    val DrawPaths : MutableList<ColoredPath>,
    val title : String,
    val date : String
)
