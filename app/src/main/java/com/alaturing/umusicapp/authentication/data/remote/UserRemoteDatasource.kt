package com.alaturing.umusicapp.authentication.data.remote

import com.alaturing.umusicapp.authentication.model.User

/**
 *
 */
interface UserRemoteDatasource {

    // Métodos autenticación
    suspend fun login(identifier:String,password:String):Result<User>
    suspend fun register(userName:String,email:String,password:String):Result<User>
    suspend fun getProfile(): Result<User>


}