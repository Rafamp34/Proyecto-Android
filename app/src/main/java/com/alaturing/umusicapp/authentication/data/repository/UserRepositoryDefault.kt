package com.alaturing.umusicapp.authentication.data.repository

import com.alaturing.umusicapp.authentication.model.User
import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.UserLocalDatasource
import com.alaturing.umusicapp.authentication.data.remote.UserRemoteDatasource
import javax.inject.Inject

class UserRepositoryDefault @Inject constructor(
    private val remote: UserRemoteDatasource,
    private val local: UserLocalDatasource
) : UserRepository {

    override suspend fun login(identifier: String, password: String): Result<User> {
        try {
            val remoteResult = remote.login(identifier, password)
            if (remoteResult.isSuccess) {
                val user = remoteResult.getOrNull()!!
                local.saveUser(user)
                return Result.success(user)
            }
        } catch (e: Exception) {
            val localUser = local.retrieveUser()
            if (localUser != null) {
                return Result.success(localUser)
            }
        }

        return Result.failure(Exception("No se pudo iniciar sesión"))
    }

    override suspend fun register(user: String, email: String, password: String): Result<User> {
        return try {
            val result = remote.register(user, email, password)
            if (result.isSuccess) {
                val newUser = result.getOrNull()!!
                local.saveUser(newUser)
            }
            result
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo registrar el usuario"))
        }
    }

    override suspend fun logout() {
        local.clearUser()
    }

    override suspend fun getProfile(): Result<User> {
        val localUser = local.retrieveUser()

        try {
            val remoteResult = remote.getProfile()
            if (remoteResult.isSuccess) {
                val user = remoteResult.getOrNull()!!
                val userWithToken = user.copy(token = localUser?.token)
                local.saveUser(userWithToken)
                return Result.success(userWithToken)
            }
        } catch (e: Exception) {
        }

        return if (localUser != null) {
            Result.success(localUser)
        } else {
            Result.failure(Exception("No se pudo obtener el perfil"))
        }
    }
}