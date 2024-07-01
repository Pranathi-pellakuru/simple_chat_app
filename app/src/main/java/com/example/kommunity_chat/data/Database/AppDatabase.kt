package com.example.kommunity_chat.data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.kommunity_chat.data.dao.MessageDAO
import com.example.kommunity_chat.data.dao.UserDAO
import com.example.kommunity_chat.data.entity.MessageRecord
import com.example.kommunity_chat.data.entity.User
import com.example.kommunity_chat.utils.Converters

@Database(entities = [User::class, MessageRecord::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun messageDao(): MessageDAO
}