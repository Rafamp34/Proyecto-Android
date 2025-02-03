package com.alaturing.umusicapp.authentication.ui.login

/**
 * Clase sellada que representa los estados de la pantalla de login en función
 * del estado de autenticación del usuario:
 *
 * Initial: Lista para loguear
 * LoggingIn: En proceso de autenticación
 * LoggedIn: Se ha autenticado correctamente
 * Error: Hay un error en la autenticación
 */
sealed class LoginUiState {
    data object Initial: LoginUiState()
    data object LoggingIn: LoginUiState()
    data object LoggedIn: LoginUiState()
    data class Error(val errorMessage:String): LoginUiState()
}