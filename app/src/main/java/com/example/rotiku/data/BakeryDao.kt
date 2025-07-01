package com.example.rotiku.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BakeryDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Insert
    suspend fun insertBakeryItem(item: BakeryItem)

    @Query("SELECT * FROM bakery_item")
    suspend fun getAllBakeryItems(): List<BakeryItem>

    @Insert
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM `order`")
    suspend fun getAllOrders(): List<Order>
}