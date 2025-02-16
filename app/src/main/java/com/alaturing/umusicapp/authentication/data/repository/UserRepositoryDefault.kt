package com.alaturing.umusicapp.authentication.data.repository

import com.alaturing.umusicapp.authentication.model.User
import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.UserLocalDatasource
import com.alaturing.umusicapp.authentication.data.remote.UserRemoteDatasource
import com.alaturing.umusicapp.di.NetworkUtils
import javax.inject.Inject

class UserRepositoryDefault @Inject constructor(
    private val remote: UserRemoteDatasource,
    private val local: UserLocalDatasource,
    private val networkUtils: NetworkUtils
) : UserRepository {

    override suspend fun login(identifier: String, password: String): Result<User> {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                val result = remote.login(identifier, password)
                if (result.isSuccess) {
                    result.getOrNull()?.let { user ->
                        local.saveUser(user)
                    }
                }
                result
            } else {
                // Si no hay conexión, intentar usar el usuario guardado localmente
                val localUser = local.retrieveUser()
                if (localUser != null) {
                    Result.success(localUser)
                } else {
                    Result.failure(Exception("No hay conexión a internet y no hay usuario guardado"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(user: String, email: String, password: String): Result<User> {
        return if (networkUtils.isNetworkAvailable()) {
            remote.register(user, email, password)
        } else {
            Result.failure(Exception("Se requiere conexión a internet para registrarse"))
        }
    }

    override suspend fun logout() {
        local.clearUser()
    }

    override suspend fun getProfile(): Result<User> {
        return try {
            // Primero intentamos obtener el perfil guardado localmente
            val localUser = local.retrieveUser()

            if (networkUtils.isNetworkAvailable()) {
                // Si hay conexión, actualizamos con datos remotos
                val remoteResult = remote.getProfile()
                if (remoteResult.isSuccess) {
                    val user = remoteResult.getOrNull()!!
                    // Preservamos el token del usuario local
                    val userWithToken = user.copy(token = localUser?.token)
                    local.saveUser(userWithToken)
                    Result.success(userWithToken)
                } else if (localUser != null) {
                    // Si falla la actualización pero tenemos datos locales, los usamos
                    Result.success(localUser)
                } else {
                    remoteResult
                }
            } else if (localUser != null) {
                // Si no hay conexión pero tenemos datos locales, los usamos
                Result.success(localUser)
            } else {
                Result.failure(Exception("No hay conexión y no hay datos guardados"))
            }
        } catch (e: Exception) {
            val localUser = local.retrieveUser()
            if (localUser != null) {
                Result.success(localUser)
            } else {
                Result.failure(e)
            }
        }
    }
}