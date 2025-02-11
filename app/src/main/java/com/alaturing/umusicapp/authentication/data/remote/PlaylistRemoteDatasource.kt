package com.alaturing.umusicapp.authentication.data.remote

import android.net.Uri
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song

interface PlaylistRemoteDatasource {
    suspend fun getAll(): Result<List<Playlist>>
    suspend fun getById(id: Int): Result<Playlist>
    suspend fun getPlaylistSongs(id: Int): Result<List<Song>>
    suspend fun addSongToPlaylist(playlistId: Int, songId: Int): Result<Unit>
    suspend fun removeSongFromPlaylist(playlistId: Int, songId: Int): Result<Unit>
    suspend fun createPlaylist(name: String, author: String, imageId: Int? = null): Result<Playlist>
    suspend fun uploadImage(uri: Uri): Result<Int>
}