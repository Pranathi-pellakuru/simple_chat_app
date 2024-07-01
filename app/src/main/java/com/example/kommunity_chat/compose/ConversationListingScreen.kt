package com.example.kommunity_chat.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.kommunity_chat.R
import com.example.kommunity_chat.data.entity.User
import com.example.kommunity_chat.data.model.Message
import com.example.kommunity_chat.navigation.Screens
import com.example.kommunity_chat.viewmodel.ChatViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ConversationListingScreen(navController: NavHostController, viewModel: ChatViewModel) {
    val users = viewModel.users.observeAsState()
    val lastMessages = viewModel.lastMessageOfAllConversations.observeAsState()

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(horizontal = 20.dp)
                    .padding(top = 40.dp, bottom = 20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        floatingActionButton = {
            Image(
                painter = painterResource(id = R.drawable.add_24),
                contentDescription = "add new conversation",
                modifier = Modifier
                    .size(44.dp)
                    .background(color = Color.Black, shape = CircleShape)
                    .padding(4.dp)
                    .clickable {
                        navController.navigate(Screens.ContactsListingScreen.route)
                    }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(vertical = 20.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items = users.value ?: emptyList()) { _: Int, item: User ->
                ConversationCard(user = item, message = lastMessages.value?.get(item.id)) {
                    viewModel.setSelecetedUser(item)
                    navController.navigate(Screens.ChatScreen.route)
                }
            }
        }
    }
}

@Composable
fun ConversationCard(user: User, message: Message?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClick()
            }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = user.imageUrl,
                error = painterResource(R.drawable.baseline_person_black)
            ),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(10.dp)
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = user.username, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = message?.dateTime?.format(DateTimeFormatter.ofPattern("dd MMM")) ?: "",
                    fontSize = 12.sp
                )
            }
            if (message != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!message.isIncoming) {
                        Image(
                            painter = painterResource(id = R.drawable.double_tick),
                            contentDescription = "double tick",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    if (!message.isImage) {
                        Text(
                            text = message.text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.picture_attach),
                            contentDescription = "img icon",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.photo),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}