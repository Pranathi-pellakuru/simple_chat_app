package com.example.kommunity_chat.data.model

import java.time.LocalDateTime

data class Message(
    val text: String,
    val isIncoming: Boolean,
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val isImage : Boolean = false
)