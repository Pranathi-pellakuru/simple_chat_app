package com.example.kommunity_chat.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val dateTime : LocalDateTime = LocalDateTime.now(),
    val imageUrl : String?
)