package com.example.ludico_app.ui.all.utils

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable

@Composable
fun AdaptiveScreenFun() {
    val windowSizeClass = getWindowsSizeClass()

    when (windowSizeClass.widthSizeClass){
        WindowWidthSizeClass.Compact -> {

        }
        WindowWidthSizeClass.Medium ->{

        }
        WindowWidthSizeClass.Expanded ->{

        }
    }
}