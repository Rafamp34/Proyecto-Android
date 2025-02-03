package com.alaturing.umusicapp.authentication.data.local


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alaturing.umusicapp.authentication.model.User
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserLocalDatasourceDS @Inject constructor(
    private val preferences: DataStore<Preferences>
): UserLocalDatasource {

    private val tokenKey = stringPreferencesKey("token")
    private val userNameKey = stringPreferencesKey("username")
    private val emailKey = stringPreferencesKey("email")
    private val idKey = intPreferencesKey("id")

    override suspend fun saveUser(user: User) {
        preferences.edit {
            p ->
            p[idKey] = user.id
            p[userNameKey] = user.userName
            p[emailKey] = user.email
            user.token?.let {
                p[tokenKey] = it
            }
        }
    }

    override suspend fun retrieveUser(): User? {
        val tokenFlow = preferences.data.map { p ->
            p[tokenKey]
        }

        val token = tokenFlow.firstOrNull()
        token?.let {
            return User(
                id = 0,
                userName = "",
                email = "",
                token = token,
                imageUrl = "",
                followers = 0,
                following = 0
            )
        }
        return null

    }

    override suspend fun clearUser() {

        preferences.edit {
                p ->
            p[idKey] = 0
            p[userNameKey] = ""
            p[emailKey] = ""
            p[tokenKey] = ""

        }
    }
}