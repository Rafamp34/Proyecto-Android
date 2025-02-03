package com.alaturing.umusicapp.authentication.model

data class User(
    val id:Int,
    val userName:String,
    val email:String,
    val imageUrl: String?,
    val followers: Int,
    val following: Int,
    //val password:String, // TODO quitar este campo
    var token: String?,
)
{
    val isLoggedIn:Boolean
        get() = token != null
}
