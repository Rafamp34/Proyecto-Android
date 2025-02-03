package com.alaturing.umusicapp.authentication.data.repository

import com.alaturing.umusicapp.authentication.model.User

interface UserRepository {

    suspend fun login(identifier:String,password:String):Result<User>
    suspend fun register(user:String,email:String,password:String):Result<User>
    suspend fun logout()
    suspend fun getProfile():Result<User>

}