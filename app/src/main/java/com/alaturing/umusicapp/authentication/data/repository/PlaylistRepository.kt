package com.alaturing.umusicapp.authentication.data.repository

import android.net.Uri
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun readAll(): Result<List<Playlist>>
    suspend fun readById(id: Int): Result<Playlist>
    fun observeAll(): Flow<Result<List<Playlist>>>
    suspend fun getPlaylistSongs(id: Int): Result<List<Song>>
    suspend fun addSongToPlaylist(playlistId: Int, songId: Int): Result<Unit>
    suspend fun removeSongFromPlaylist(playlistId: Int, songId: Int): Result<Unit>
    suspend fun createPlaylist(name: String, author: String, imageId: Int?): Result<Playlist>
    suspend fun uploadImage(uri: Uri): Result<Int>
    suspend fun deletePlaylist(id: Int): Result<Unit>

}