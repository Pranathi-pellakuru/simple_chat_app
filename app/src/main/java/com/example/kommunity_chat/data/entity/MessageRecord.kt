package com.example.kommunity_chat.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = User::class,
        childColumns = ["user_id"],
        parentColumns = ["id"]
    )])
data class MessageRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "isIncoming") val isIncoming : Boolean,
    @ColumnInfo(name = "isImage")val isImage : Boolean = false,
    val dateTime: LocalDateTime = LocalDateTime.now(),
)