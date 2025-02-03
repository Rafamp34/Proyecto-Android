package com.alaturing.umusicapp.common.exception



class UserNotAuthorizedException :RuntimeException() {

    override fun toString() = "Incorrect identifier or password"
}