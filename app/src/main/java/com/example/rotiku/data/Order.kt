package com.example.rotiku.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "order")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val bakeryItemId: Int,
    val latitude: Double,
    val longitude: Double,
    val orderDate: Date
)