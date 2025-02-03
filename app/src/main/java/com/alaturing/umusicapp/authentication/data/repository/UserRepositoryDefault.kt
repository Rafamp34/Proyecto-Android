package com.alaturing.umusicapp.authentication.data.repository

import com.alaturing.umusicapp.authentication.model.User
import com.alaturing.umusicapp.authentication.data.local.UserLocalDatasource
import com.alaturing.umusicapp.authentication.data.remote.UserRemoteDatasource
import javax.inject.Inject

class UserRepositoryDefault @Inject constructor(
    private val remote: UserRemoteDatasource,
    private val local: UserLocalDatasource
): UserRepository {
    override suspend fun login(identifier: String, password: String): Result<User> {
        val result = remote.login(identifier, password)
        if (result.isSuccess) {
            local.saveUser(result.getOrNull()!!)
        }
        return result
    }

    override suspend fun register(user: String, email: String, password: String): Result<User> {
        return remote.register(user, email, password)
    }

    override suspend fun logout() = local.clearUser()

    override suspend fun getProfile(): Result<User> {
        return remote.getProfile()
    }
}