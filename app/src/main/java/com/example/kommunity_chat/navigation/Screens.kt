package com.example.kommunity_chat.navigation

sealed class Screens(val route: String) {
    object ConversationsListingScreen : Screens("Listing_Screen")

    object ChatScreen : Screens("Chat_Screen")

    object  ContactsListingScreen : Screens("Contacts_Screen")
}