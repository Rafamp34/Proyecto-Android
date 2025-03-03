package com.alaturing.umusicapp.authentication.data.local.LocalDatasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PlaylistLocalDatasourceDS @Inject constructor(
    private val preferences: DataStore<Preferences>,
    private val gson: Gson
) {
    private val playlistsKey = stringPreferencesKey("playlists")

    suspend fun savePlaylists(playlists: List<Playlist>) {
        val playlistsJson = gson.toJson(playlists)
        preferences.edit { prefs ->
            prefs[playlistsKey] = playlistsJson
        }
    }

    suspend fun getPlaylists(): List<Playlist> {
        val prefs = preferences.data.first()
        val playlistsJson = prefs[playlistsKey] ?: return emptyList()
        val type = object : TypeToken<List<Playlist>>() {}.type
        return gson.fromJson(playlistsJson, type)
    }

    suspend fun savePlaylistSongs(playlistId: Int, songs: List<Song>) {
        val key = stringPreferencesKey("playlist_songs_$playlistId")
        val songsJson = gson.toJson(songs)
        preferences.edit { prefs ->
            prefs[key] = songsJson
        }
    }

    suspend fun getPlaylistSongs(playlistId: Int): List<Song> {
        val key = stringPreferencesKey("playlist_songs_$playlistId")
        val prefs = preferences.data.first()
        val songsJson = prefs[key] ?: return emptyList()
        val type = object : TypeToken<List<Song>>() {}.type
        return gson.fromJson(songsJson, type)
    }

    suspend fun deletePlaylist(id: Int) {
        val songKey = stringPreferencesKey("playlist_songs_$id")

        val currentPlaylists = getPlaylists().filterNot { it.id == id }

        preferences.edit { prefs ->
            prefs.remove(songKey)

            val playlistsJson = gson.toJson(currentPlaylists)
            prefs[playlistsKey] = playlistsJson
        }
    }
}