package com.alaturing.umusicapp.authentication.data.remote

import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.UserLocalDatasource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Implementación de [Interceptor] para autenticar a usuarios con un token JWT
 */
class AuthenticationInterceptor @Inject constructor(
    private val userLocalDatasource: UserLocalDatasource
):Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        if (chain.request().method == "POST" && chain.request().url.encodedPath == "/api/auth/local") {
            return chain.proceed(chain.request())
        }
        // Leemos el token desde el repositorio local de usuarios
        val token: String? =
                runBlocking {
                    userLocalDatasource.retrieveUser()?.let {
                    return@runBlocking it.token
                }
                    return@runBlocking null
                }
        // Si tenemos un token almacenado, lo añadiremos como una cabecera de autenticación
        token?.let {
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization","Bearer $it")
                .build()
            return chain.proceed(newRequest)
        }
        // Si hemos llegado aquí no tenemos un token valido, continuamos con la petición
        // original
        return chain.proceed(chain.request())



    }
}