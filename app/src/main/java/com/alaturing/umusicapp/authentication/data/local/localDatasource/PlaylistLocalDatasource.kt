package com.alaturing.umusicapp.authentication.data.local.localDatasource

import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song

interface PlaylistLocalDatasource {
    suspend fun savePlaylists(playlists: List<Playlist>)
    suspend fun getPlaylists(): List<Playlist>
    suspend fun savePlaylistSongs(playlistId: Int, songs: List<Song>)
    suspend fun getPlaylistSongs(playlistId: Int): List<Song>
    suspend fun deletePlaylist(id: Int)
}