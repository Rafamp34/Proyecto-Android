package com.alaturing.umusicapp.authentication.data.local

import com.alaturing.umusicapp.authentication.model.User

interface UserLocalDatasource {
    suspend fun saveUser(user: User)
    suspend fun retrieveUser(): User?
    suspend fun clearUser()
}