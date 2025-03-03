package com.alaturing.umusicapp.authentication.data.local.LocalDatasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alaturing.umusicapp.main.song.model.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SongLocalDatasourceDS @Inject constructor(
    private val preferences: DataStore<Preferences>,
    private val gson: Gson
) {
    private val songsKey = stringPreferencesKey("songs")

    suspend fun getAllSongs(): List<Song> {
        val prefs = preferences.data.first()
        val songsJson = prefs[songsKey] ?: return emptyList()
        val type = object : TypeToken<List<Song>>() {}.type
        return gson.fromJson(songsJson, type)
    }

    suspend fun getSongById(id: Int): Song? {
        return getAllSongs().find { it.id == id }
    }
}