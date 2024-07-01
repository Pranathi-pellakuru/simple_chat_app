package com.example.kommunity_chat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kommunity_chat.data.entity.MessageRecord
import com.example.kommunity_chat.data.entity.User
import com.example.kommunity_chat.data.model.ContactUiState
import com.example.kommunity_chat.data.model.Message
import com.example.kommunity_chat.data.repository.ChatBotRepository
import com.example.kommunity_chat.data.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatBotRepository,
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _updatedMessages: MutableLiveData<List<Message>> = MutableLiveData(emptyList())
    val updatedMessages: LiveData<List<Message>> = _updatedMessages

    private val _users: MutableLiveData<List<User>> = MutableLiveData(emptyList())
    val users: LiveData<List<User>> = _users

    private val _lastMessageOfAllConversations: MutableLiveData<Map<Long, Message?>> =
        MutableLiveData(
            emptyMap()
        )
    val lastMessageOfAllConversations: LiveData<Map<Long, Message?>> =
        _lastMessageOfAllConversations

    var selectedUser: User? = null

    private val _uiState = MutableStateFlow(ContactUiState(loading = true))
    val uiState = _uiState.asStateFlow()

    fun sendImage(message: String) {
        updateMessages(Message(message, false, isImage = true))
        viewModelScope.launch {
            selectedUser?.let {
                MessageRecord(
                    userId = it.id,
                    text = message,
                    isIncoming = false,
                    isImage = true
                )
            }?.let {
                Log.d("messagess", it.toString())
                repository.addMessageRecord(
                    it
                )
            }
        }
    }


    fun getResponse(message: String) {
        updateMessages(Message(message, false))
        viewModelScope.launch {
            selectedUser?.let {
                MessageRecord(
                    userId = it.id,
                    text = message,
                    isIncoming = false
                )
            }?.let {
                Log.d("messagess", it.toString())
                repository.addMessageRecord(
                    it
                )
            }
            val data = repository.getResponse(message.replace(" ", ""))
            if (message == "Refund status") {
                delay(3000)
                getRefundUpdate()
            }
            if (data != null) {
                updateMessages(data)
                selectedUser?.let {
                    MessageRecord(
                        userId = it.id,
                        text = data.text,
                        isIncoming = data.isIncoming
                    )
                }?.let {
                    repository.addMessageRecord(
                        it
                    )
                }
            }
        }
    }

    private fun getRefundUpdate() {
        viewModelScope.launch {
            val data = repository.getRefundUpdate()
            if (data != null) {
                updateMessages(data)
                selectedUser?.let {
                    MessageRecord(
                        userId = it.id,
                        text = data.text,
                        isIncoming = data.isIncoming
                    )
                }?.let {
                    repository.addMessageRecord(
                        it
                    )
                }
            }
        }
    }

    fun setSelecetedUser(user: User) {
        selectedUser = user
    }

    fun getAllUsers() {
        viewModelScope.launch {
            val data = repository.getUsers()
            _users.postValue(data.reversed())
            getLastMessagesOfAllConversations(data)
        }
    }

    fun fetchContacts() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            getContacts()
        }
    }

    private fun getContacts() = viewModelScope.launch {
        val contacts = contactsRepository.getContacts().groupBy { contact ->
            contact.displayName.first().toString()
        }
        _uiState.update {
            it.copy(
                loading = false,
                contacts = contacts
            )
        }
    }

    fun getAllMessages(userId: Long) {
        viewModelScope.launch {
            val messages = repository.getAllMessages(userId)
            _updatedMessages.postValue(messages.reversed())
        }
    }

    fun getLastMessagesOfAllConversations(users: List<User>) {
        val lastMessageList: MutableMap<Long, Message?> = mutableMapOf()
        viewModelScope.launch {
            users.let {
                for (each in it) {
                    val message = repository.getLastMessageOfAUser(each.id)
                    lastMessageList.put(each.id, message)
                }
                _lastMessageOfAllConversations.postValue(lastMessageList)
            }

        }
    }

    fun updateLastMessageOfUser(message: Message, userId: Long) {
        val lastMessages = _lastMessageOfAllConversations.value?.toMutableMap()
        lastMessages?.put(userId, message)
        lastMessages?.let {
            _lastMessageOfAllConversations.postValue(it)
        }
    }

    fun addNewUser(user: String, image: String?) {
        viewModelScope.launch {
            repository.addUser(User(username = user, imageUrl = image))
        }
        val usersList = _users.value?.toMutableList()
        usersList?.add(0, User(username = user, id = usersList.size.toLong() + 1, imageUrl = image))
        _users.postValue(usersList ?: emptyList())
    }

    private fun updateMessages(message: Message) {
        val messages = mutableListOf(message)
        _updatedMessages.value?.let { messages.addAll(it) }
        _updatedMessages.postValue(messages)
    }

}