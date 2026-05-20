package com.university.newsapp.ui

import android.graphics.Color
import kotlin.math.abs

fun initialsFor(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "U"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts.first().first().toString() + parts[1].first().toString()).uppercase()
    }
}

fun avatarColorFor(seed: String): Int {
    val palette = listOf(
        "#164A9C",
        "#0F766E",
        "#B45309",
        "#7C3AED",
        "#BE123C",
        "#0369A1"
    )
    return Color.parseColor(palette[abs(seed.hashCode()) % palette.size])
}
