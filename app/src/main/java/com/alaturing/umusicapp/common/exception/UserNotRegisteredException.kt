package com.alaturing.umusicapp.common.exception

class UserNotRegisteredException ():RuntimeException() {
    override fun toString() = "User cannot be registered"

}