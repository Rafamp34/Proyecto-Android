package com.alaturing.umusicapp.authentication.data.remote.model

data class AuthRequestBody(
    val identifier: String,
    val password: String
)

data class AuthResponseBody(
    val jwt: String,
    val user: AuthResponseUser
)

data class AuthResponseUser(
    val id: Int,
    val username: String,
    val email: String,
    val followers: Int,
    val following: Int,
    val image: ImageResponse?
)

data class ImageResponse(
    val id: Int,
    val url: String
)

data class RegisterRequestBody(
    val username: String,
    val email: String,
    val password: String
)