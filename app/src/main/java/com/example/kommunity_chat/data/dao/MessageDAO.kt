package com.example.kommunity_chat.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kommunity_chat.data.entity.MessageRecord
import com.example.kommunity_chat.data.model.Message

@Dao
interface MessageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageRecord)

    @Query("SELECT text,isIncoming,isImage,dateTime FROM messages where user_id = :userId")
    suspend fun getAllMessagesOfAUser(userId: Long): List<Message>

    @Query("SELECT text,isIncoming,isImage,dateTime FROM messages where user_id = :userId ORDER BY id DESC LIMIT 1")
    suspend fun getLastMessageOfAUserConversation(userId: Long): Message?
}