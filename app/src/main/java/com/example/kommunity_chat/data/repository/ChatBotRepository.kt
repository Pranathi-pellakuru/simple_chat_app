package com.example.kommunity_chat.data.repository

import android.util.Log
import com.example.kommunity_chat.data.dao.MessageDAO
import com.example.kommunity_chat.data.dao.UserDAO
import com.example.kommunity_chat.data.entity.MessageRecord
import com.example.kommunity_chat.data.entity.User
import com.example.kommunity_chat.data.model.Message
import com.example.kommunity_chat.data.model.ResponseBody
import com.example.kommunity_chat.api.ChatBotAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class ChatBotRepository @Inject constructor(
    private val userDao: UserDAO,
    private val messageDAO: MessageDAO,
    private val retrofitService : ChatBotAPI
) {

    suspend fun getResponse(message: String): Message? {
        return try {
            var data: Response<ResponseBody>?
            withContext(Dispatchers.IO) {
                data = retrofitService.getMessage(message)
            }
            if (data?.isSuccessful == true) {
                data?.body()?.message?.let { Message(it, true) }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getRefundUpdate(): Message?{
        return try {
            var data: Response<ResponseBody>?
            withContext(Dispatchers.IO) {
                data = retrofitService.getRefundUpdate()
            }
            if (data?.isSuccessful == true) {
                data?.body()?.message?.let { Message(it, true) }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun addUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun addMessageRecord(messageRecord: MessageRecord) {
        messageDAO.insertMessage(messageRecord)
    }

    suspend fun getAllMessages(userId : Long):List<Message>{
        return  messageDAO.getAllMessagesOfAUser(userId)
    }

    suspend fun getLastMessageOfAUser(userId: Long): Message?{
        Log.d("Messagess", "suspend function")
        return  messageDAO.getLastMessageOfAUserConversation(userId)
    }
}