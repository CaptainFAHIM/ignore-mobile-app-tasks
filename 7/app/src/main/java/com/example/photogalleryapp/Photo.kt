package com.example.photogalleryapp

data class Photo(
    val id: Int,
    val resourceId: Int,
    val title: String,
    val category: String,
    var isSelected: Boolean = false
)