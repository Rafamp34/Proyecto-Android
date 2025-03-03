package com.alaturing.umusicapp.authentication.data.local.localDatasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alaturing.umusicapp.authentication.model.User
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserLocalDatasourceDS @Inject constructor(
    private val preferences: DataStore<Preferences>
): UserLocalDatasource {

    private val tokenKey = stringPreferencesKey("token")
    private val userNameKey = stringPreferencesKey("username")
    private val emailKey = stringPreferencesKey("email")
    private val idKey = intPreferencesKey("id")
    private val imageUrlKey = stringPreferencesKey("imageUrl")
    private val followersKey = intPreferencesKey("followers")
    private val followingKey = intPreferencesKey("following")

    override suspend fun saveUser(user: User) {
        preferences.edit { prefs ->
            prefs[idKey] = user.id
            prefs[userNameKey] = user.userName
            prefs[emailKey] = user.email
            prefs[followersKey] = user.followers
            prefs[followingKey] = user.following
            user.imageUrl?.let { prefs[imageUrlKey] = it }
            user.token?.let { prefs[tokenKey] = it }
        }
    }

    override suspend fun retrieveUser(): User? {
        val prefs = preferences.data.first()

        // Verificamos si existe el token
        val token = prefs[tokenKey] ?: return null

        return User(
            id = prefs[idKey] ?: 0,
            userName = prefs[userNameKey] ?: "",
            email = prefs[emailKey] ?: "",
            imageUrl = prefs[imageUrlKey],
            followers = prefs[followersKey] ?: 0,
            following = prefs[followingKey] ?: 0,
            token = token
        )
    }

    override suspend fun clearUser() {
        preferences.edit { prefs ->
            prefs.remove(idKey)
            prefs.remove(userNameKey)
            prefs.remove(emailKey)
            prefs.remove(tokenKey)
            prefs.remove(imageUrlKey)
            prefs.remove(followersKey)
            prefs.remove(followingKey)
        }
    }
}