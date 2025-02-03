package com.alaturing.umusicapp.main.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alaturing.umusicapp.authentication.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val result = userRepository.getProfile()
                if (result.isSuccess) {
                    _uiState.value = ProfileUiState.Success(result.getOrNull()!!)
                } else {
                    _uiState.value = ProfileUiState.Error("Error cargando perfil")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.value = ProfileUiState.LoggedOut
        }
    }
}