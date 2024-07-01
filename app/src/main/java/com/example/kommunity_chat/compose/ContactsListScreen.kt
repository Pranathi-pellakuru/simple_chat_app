package com.example.kommunity_chat.compose

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.kommunity_chat.R
import com.example.kommunity_chat.data.model.ContactModel
import com.example.kommunity_chat.data.model.ContactUiState
import com.example.kommunity_chat.viewmodel.ChatViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Collections


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactsListingScreen(navController: NavHostController, viewModel: ChatViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
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
                    navController.popBackStack()
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.arrow_back_24),
                        contentDescription = "back arrow",
                    )
                }
                Text(
                    text = "Contacts",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
    ) { innerPadding ->
        val showMainContent = remember { mutableStateOf(false) }

        val permissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted -> showMainContent.value = isGranted }

        when {
            permissionState.status.isGranted -> {
                showMainContent.value = true
            }

            else -> {
                LaunchedEffect(permissionState) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }
        }

        if (showMainContent.value) {

            PermissionGrantedScreen(
                Modifier.padding(innerPadding),
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}

@Composable
private fun PermissionGrantedScreen(
    modifier: Modifier,
    viewModel: ChatViewModel,
    navController: NavHostController
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )
    LaunchedEffect(key1 = true) {
        viewModel.fetchContacts()
    }

    ContactsListScreen(
        modifier = modifier,
        state = state,
        navController = navController,
        viewModel = viewModel
    )
}


@Composable
fun ContactsListScreen(
    modifier: Modifier = Modifier,
    state: ContactUiState,
    viewModel: ChatViewModel,
    navController: NavHostController
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        ContactsList(
            modifier = Modifier.fillMaxSize(),
            contacts = state.contacts,
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
fun ContactsList(
    modifier: Modifier = Modifier,
    contacts: Map<String, List<ContactModel>> = Collections.emptyMap(),
    viewModel: ChatViewModel,
    navController: NavHostController
) {
    LazyColumn(modifier) {
        contacts.map { entry ->
            items(
                entry.value.size
            ) { index ->
                ContactListItem(contact = entry.value[index]) {
                    viewModel.addNewUser(
                        entry.value[index].displayName,
                        entry.value[index].photoThumbnailUri
                    )
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
fun ContactListItem(contact: ContactModel, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(end = 10.dp)
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onClick();
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = contact.photoThumbnailUri,
                error = painterResource(R.drawable.baseline_person_black)
            ),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(10.dp)
                .size(40.dp)
                .clip(CircleShape)
        )
        Column(modifier = Modifier.weight(1f, true)) {
            Text(
                text = contact.displayName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = contact.phoneNumber,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}