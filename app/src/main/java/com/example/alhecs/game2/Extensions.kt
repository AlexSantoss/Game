package com.example.alhecs.game2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View

fun View.createAndResizeBitmap(idImage: Int, width: Int, height: Int)
        = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, idImage),
        width, height, false)