package com.example.kommunity_chat.data.model

import java.util.Collections

data class ContactUiState(
    val loading: Boolean = false,
    val contacts: GroupedContacts = Collections.emptyMap()
)

typealias GroupedContacts = Map<String, List<ContactModel>>