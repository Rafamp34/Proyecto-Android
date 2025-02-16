package com.alaturing.umusicapp.main.profile.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alaturing.umusicapp.authentication.data.repository.PlaylistRepository
import com.alaturing.umusicapp.authentication.data.repository.UserRepository
import com.alaturing.umusicapp.main.playlist.model.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val playlistRepository: PlaylistRepository  // Añadir esta dependencia
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _userPlaylists = MutableStateFlow<List<Playlist>>(emptyList())
    val userPlaylists: StateFlow<List<Playlist>> = _userPlaylists.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val result = userRepository.getProfile()
                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    _uiState.value = ProfileUiState.Success(user)
                    loadUserPlaylists(user.userName)
                } else {
                    _uiState.value = ProfileUiState.Error("Error cargando perfil")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun loadUserPlaylists(userName: String) {
        viewModelScope.launch {
            playlistRepository.readAll().onSuccess { playlists ->
                _userPlaylists.value = playlists.filter { it.author == userName }
            }
        }
    }

    fun createPlaylist(name: String, author: String) {
        viewModelScope.launch {
            // Pasar null como imageId
            playlistRepository.createPlaylist(name, author, null).onSuccess {
                loadUserPlaylists(author)
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.value = ProfileUiState.LoggedOut
        }
    }

    fun createPlaylist(name: String, author: String, imageUri: Uri? = null) {
        viewModelScope.launch {
            imageUri?.let { uri ->
                // Primero subimos la imagen
                playlistRepository.uploadImage(uri).onSuccess { imageId ->
                    // Luego creamos la playlist con la referencia a la imagen
                    playlistRepository.createPlaylist(name, author, imageId).onSuccess {
                        loadUserPlaylists(author)
                    }
                }
            } ?: run {
                // Crear playlist sin imagen
                playlistRepository.createPlaylist(name, author, null).onSuccess {
                    loadUserPlaylists(author)
                }
            }
        }
    }

}