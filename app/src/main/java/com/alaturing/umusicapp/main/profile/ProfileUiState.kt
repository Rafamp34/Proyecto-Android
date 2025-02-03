package com.alaturing.umusicapp.main.profile.ui

import com.alaturing.umusicapp.authentication.model.User

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data object LoggedOut : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}