package com.example.rotiku.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bakery_item")
data class BakeryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String
)