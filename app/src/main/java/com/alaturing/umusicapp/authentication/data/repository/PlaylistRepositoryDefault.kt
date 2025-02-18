package com.alaturing.umusicapp.authentication.data.repository

import android.net.Uri
import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.PlaylistLocalDatasourceDS
import com.alaturing.umusicapp.authentication.data.remote.PlaylistRemoteDatasource
import com.alaturing.umusicapp.di.NetworkUtils
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PlaylistRepositoryDefault @Inject constructor(
    private val remote: PlaylistRemoteDatasource,
    private val local: PlaylistLocalDatasourceDS,
    private val networkUtils: NetworkUtils
) : PlaylistRepository {

    override suspend fun readAll(): Result<List<Playlist>> {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                val result = remote.getAll()
                if (result.isSuccess) {
                    val playlists = result.getOrNull()!!
                    local.savePlaylists(playlists)
                    Result.success(playlists)
                } else {
                    // Si falla la red, intentar usar datos locales
                    Result.success(local.getPlaylists())
                }
            } else {
                // Si no hay red, usar datos locales
                Result.success(local.getPlaylists())
            }
        } catch (e: Exception) {
            try {
                Result.success(local.getPlaylists())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun readById(id: Int): Result<Playlist> {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                val result = remote.getById(id)
                if (result.isSuccess) {
                    val playlist = result.getOrNull()!!
                    local.savePlaylistSongs(playlist.id, playlist.songs)
                    Result.success(playlist)
                } else {
                    val playlists = local.getPlaylists()
                    val playlist = playlists.find { it.id == id }
                    if (playlist != null) {
                        val songs = local.getPlaylistSongs(id)
                        Result.success(playlist.copy(songs = songs))
                    } else {
                        Result.failure(Exception("Playlist no encontrada"))
                    }
                }
            } else {
                val playlists = local.getPlaylists()
                val playlist = playlists.find { it.id == id }
                if (playlist != null) {
                    val songs = local.getPlaylistSongs(id)
                    Result.success(playlist.copy(songs = songs))
                } else {
                    Result.failure(Exception("Playlist no encontrada"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeAll(): Flow<Result<List<Playlist>>> = flow {
        // Emitir datos locales primero
        emit(Result.success(local.getPlaylists()))

        // Si hay conexión, actualizar con datos remotos
        if (networkUtils.isNetworkAvailable()) {
            val result = remote.getAll()
            if (result.isSuccess) {
                val playlists = result.getOrNull()!!
                local.savePlaylists(playlists)
                emit(Result.success(playlists))
            }
        }
    }

    override suspend fun getPlaylistSongs(id: Int): Result<List<Song>> {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                val result = remote.getPlaylistSongs(id)
                if (result.isSuccess) {
                    val songs = result.getOrNull()!!
                    local.savePlaylistSongs(id, songs)
                    Result.success(songs)
                } else {
                    Result.success(local.getPlaylistSongs(id))
                }
            } else {
                Result.success(local.getPlaylistSongs(id))
            }
        } catch (e: Exception) {
            try {
                Result.success(local.getPlaylistSongs(id))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun addSongToPlaylist(playlistId: Int, songId: Int): Result<Unit> {
        return remote.addSongToPlaylist(playlistId, songId)
    }

    override suspend fun removeSongFromPlaylist(playlistId: Int, songId: Int): Result<Unit> {
        return remote.removeSongFromPlaylist(playlistId, songId)
    }

    override suspend fun createPlaylist(name: String, author: String, imageId: Int?): Result<Playlist> {
        return remote.createPlaylist(name, author, imageId)
    }

    override suspend fun uploadImage(uri: Uri): Result<Int> {
        return remote.uploadImage(uri)
    }

    override suspend fun deletePlaylist(id: Int): Result<Unit> {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                val result = remote.deletePlaylist(id)
                if (result.isSuccess) {
                    // Si la eliminación fue exitosa en el servidor, eliminar también localmente
                    local.deletePlaylist(id)
                    Result.success(Unit)
                } else {
                    result // Propagar el error del remoto
                }
            } else {
                Result.failure(Exception("No hay conexión a internet"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}