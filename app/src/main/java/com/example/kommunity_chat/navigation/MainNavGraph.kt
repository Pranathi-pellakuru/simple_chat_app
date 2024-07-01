package com.example.kommunity_chat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kommunity_chat.compose.ChatScreen
import com.example.kommunity_chat.compose.ContactsListingScreen
import com.example.kommunity_chat.compose.ConversationListingScreen
import com.example.kommunity_chat.viewmodel.ChatViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
    viewModel: ChatViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screens.ConversationsListingScreen.route
    ) {
        composable(Screens.ConversationsListingScreen.route) {
            ConversationListingScreen(navController = navController,viewModel = viewModel)
        }
        composable(Screens.ChatScreen.route) {
            ChatScreen(navController = navController, viewModel = viewModel )
        }
        composable(Screens.ContactsListingScreen.route){
            ContactsListingScreen(navController = navController, viewModel = viewModel)
        }
    }
}