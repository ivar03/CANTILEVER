package com.ivar7284.rcipemanagementapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val ingredients: String,
    val instructions: String,
    val category: String,
    val imagePath: String? = null,
    val rating: Float = 0f,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)