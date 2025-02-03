package com.alaturing.umusicapp.authentication.ui.register

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
 * [ViewModel] para mantener el estado de la pantalla de registro
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel()
{
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Initial)
    val uiState:StateFlow<RegisterUiState>
        get() = _uiState.asStateFlow()

    /**
     * Método para manejar la petición de registro
     * @param userName
     * @param email
     * @param password
     */
    fun onRegister(userName:String,email:String,password:String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Registering
            val result = repository.register(userName,email,password)
            if (result.isFailure) {
                result.exceptionOrNull()?.let {
                _uiState.value = RegisterUiState.Error(it.toString())
                }
            }
            else {
                _uiState.value = RegisterUiState.Registered
            }
        }
    }

}