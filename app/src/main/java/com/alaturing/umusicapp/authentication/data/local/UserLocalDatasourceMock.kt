package com.alaturing.incidentify.authentication.data.local

import com.alaturing.umusicapp.authentication.data.local.UserLocalDatasource
import com.alaturing.umusicapp.authentication.model.User
import javax.inject.Inject

/**
 * Implementación mock de [UserLocalDatasource] que almacena en memoria
 */
class UserLocalDatasourceMock @Inject constructor(): UserLocalDatasource {
    private var _user: User? = null
    override suspend fun saveUser(user: User) {
        _user = user
    }

    override suspend fun retrieveUser(): User? {
        return _user
    }

    override suspend fun clearUser() {
        _user = null
    }
}