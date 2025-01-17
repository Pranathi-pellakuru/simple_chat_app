package com.example.kommunity_chat.data.repository

import android.content.Context
import android.provider.ContactsContract
import com.example.kommunity_chat.data.model.ContactModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ContactsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // defined a couroutine scope to get list of contacts from the background
    suspend fun getContacts(): List<ContactModel> = coroutineScope {
        async(Dispatchers.IO) { getContactList() }.await()
    }

    // get list of contact details such as id, name, phone number & photo URI
    // from the `Phone.CONTENT)URI`
    private fun getContactList(): List<ContactModel> {
        val contactsList = mutableListOf<ContactModel>()
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )?.use { contactsCursor ->
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoUriIndex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
            val photoThumbNailIndex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)

            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                val number = contactsCursor.getString(numberIndex)
                val photoUri = contactsCursor.getString(photoUriIndex)
                val photoThumbnailUri = contactsCursor.getString(photoThumbNailIndex)
                contactsList.add(
                    ContactModel(
                        id = id, displayName = name, phoneNumber = number, photoThumbnailUri = photoThumbnailUri
                    )
                )
            }
        }
        return contactsList
    }
}