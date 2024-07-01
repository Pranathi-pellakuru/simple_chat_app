package com.example.kommunity_chat.compose

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.kommunity_chat.R
import com.example.kommunity_chat.data.entity.User
import com.example.kommunity_chat.data.model.Message
import com.example.kommunity_chat.viewmodel.ChatViewModel
import java.time.format.DateTimeFormatter


@Composable
fun ChatScreen(navController: NavHostController, viewModel: ChatViewModel) {
    var message by remember {
        mutableStateOf(mutableStateOf(TextFieldValue("")))
    }
    val latestMessages = viewModel.updatedMessages.observeAsState()
    val user = viewModel.selectedUser

    val textMimics =
        listOf("Hi", "Recent order information", "Connect me with an agent", "Refund status")

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.sendImage(uri.toString())
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    LaunchedEffect(key1 = true) {
        if (user != null) {
            viewModel.getAllMessages(userId = user.id)
        }
    }

    BackHandler {
        latestMessages.value?.let {
            if (user != null) {
                viewModel.updateLastMessageOfUser(
                    message = it.first(), userId = user.id
                )
            }
        }
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            if (user != null) {
                ChatTopBar(user = user) {
                    latestMessages.value?.let {
                        if (it.isNotEmpty()) {
                            viewModel.updateLastMessageOfUser(
                                message = it.first(), userId = user.id
                            )
                        }
                    }
                    navController.popBackStack()
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    itemsIndexed(items = textMimics) { _, item ->
                        Text(text = item, color = Color.White, modifier = Modifier
                            .background(
                                color = Color.Black, shape = RoundedCornerShape(8.dp)
                            )
                            .padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                            .clickable {
                                message.value =
                                    TextFieldValue(item, selection = TextRange(item.length))
                            })
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(value = message.value, onValueChange = {
                        message.value = it
                    }, modifier = Modifier
                        .weight(1f), placeholder = {
                        Text(text = "Message")
                    }, shape = RoundedCornerShape(16.dp), colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ), suffix = {
                        Image(painter = painterResource(id = R.drawable.picture_attach),
                            contentDescription = "Send image",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    pickMedia.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                })
                    })

                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(onClick = {
                        if (message.value.text.isNotEmpty()) {
                            viewModel.getResponse(message.value.text)
                            message.value = TextFieldValue("")
                        }
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.send_icon),
                            contentDescription = "double tick",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                }
            }
        },
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .padding(horizontal = 10.dp)
        ) {
            LazyColumn(
                reverseLayout = true,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
            ) {
                itemsIndexed(items = latestMessages.value ?: emptyList()) { index, item ->
                    if (item.isIncoming) {
                        if (user != null) {
                            if (index == 0) {
                                if (latestMessages.value?.size!! >= 2) {
                                    InComingMessages(
                                        message = item,
                                        showProfile = latestMessages.value?.get(index + 1)?.isIncoming != true,
                                        showTime = true,
                                        user = user
                                    )
                                } else {
                                    InComingMessages(
                                        message = item,
                                        showProfile = true,
                                        showTime = true,
                                        user = user
                                    )
                                }
                            } else {
                                if (index == (latestMessages.value?.size ?: 0) - 1) {
                                    InComingMessages(
                                        item,
                                        showProfile = true,
                                        showTime = !latestMessages.value!![index - 1].isIncoming,
                                        user = user
                                    )

                                } else {
                                    InComingMessages(
                                        message = item,
                                        showProfile = !latestMessages.value!![index + 1].isIncoming,
                                        showTime = latestMessages.value?.get(index - 1)?.isIncoming != true,
                                        user = user
                                    )
                                }
                            }
                        }
                    } else {
                        if (item.isImage) {
                            DisplaySelectedImage(message = item)
                        } else {
                            OutGoingMessages(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatTopBar(user: User, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 10.dp)
            .padding(top = 30.dp, bottom = 10.dp)
    ) {
        IconButton(onClick = {
            onClick();
        }) {
            Image(
                painter = painterResource(id = R.drawable.arrow_back_24),
                contentDescription = "back arrow",
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = user.imageUrl,
                    error = painterResource(R.drawable.ic_person_24)
                ),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(10.dp)
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = user.username,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = "online", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}


@Composable
fun DisplaySelectedImage(message: Message) {
    Row(
        horizontalArrangement = Arrangement.End, modifier = Modifier
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = message.text.toUri(),
            contentDescription = "",
            modifier = Modifier.fillMaxWidth(0.5f),
            contentScale = ContentScale.FillWidth,
            error = painterResource(id = R.drawable.error_outline_24),
            placeholder = painterResource(id = R.drawable.error_outline_24)
        )
    }
}

@Composable
fun OutGoingMessages(message: Message) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp, end = 10.dp)
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = message.text,
            modifier = Modifier
                .background(color = Color.Black, shape = RoundedCornerShape(8.dp))
                .padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
            color = Color.White
        )

        Row {
            Text(
                text = message.dateTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                fontSize = 10.sp
            )
            Image(
                painter = painterResource(id = R.drawable.double_tick),
                contentDescription = "double tick",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun InComingMessages(message: Message, showProfile: Boolean, showTime: Boolean, user: User) {
    Row(
        verticalAlignment = Alignment.Top, modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(vertical = 2.dp)
    ) {
        if (showProfile) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = user.imageUrl,
                    error = painterResource(R.drawable.baseline_person_black)
                ),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(32.dp)
                    .clip(CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.width(52.dp))
        }

        Column {
            if (showProfile) {
                Text(text = user.username, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
            Text(
                text = message.text,
                modifier = Modifier
                    .background(
                        color = Color.LightGray, shape = RoundedCornerShape(8.dp)
                    )
                    .padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            )
            if (showTime) {
                Text(
                    text = message.dateTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    fontSize = 10.sp
                )
            }
        }
    }
}