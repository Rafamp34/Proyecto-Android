package com.alaturing.umusicapp.authentication.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alaturing.umusicapp.authentication.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Gestor de estado para la IU de login
 * Publíca el estado en uiState
 *
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState>
        get() = _uiState.asStateFlow()

    /**
     * @param identifier Correo o identificador del usuario
     * @param password Contraseña
     */
    fun onLogin(identifier:String, password:String) {

        viewModelScope.launch {
            _uiState.value = LoginUiState.LoggingIn

            val result = userRepository.login(identifier,password)
            if (result.isSuccess) _uiState.value = LoginUiState.LoggedIn
            else
                result.exceptionOrNull()?.let {
                    _uiState.value = LoginUiState.Error(it.message ?: "Error desconocido")
                }

        }
    }


}