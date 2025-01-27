package com.bnyro.contacts.util

import com.bnyro.contacts.obj.ContactData
import com.bnyro.contacts.obj.ContactsGroup

interface ContactsRepository {
    val label: String

    suspend fun createContact(contact: ContactData)
    suspend fun updateContact(contact: ContactData)
    suspend fun deleteContacts(contacts: List<ContactData>)
    suspend fun getContactList(): List<ContactData>
    suspend fun loadAdvancedData(contact: ContactData): ContactData
    fun isAutoBackupEnabled(): Boolean
    suspend fun createGroup(groupName: String): ContactsGroup?
    suspend fun renameGroup(group: ContactsGroup, newName: String)
    suspend fun deleteGroup(group: ContactsGroup)
}
