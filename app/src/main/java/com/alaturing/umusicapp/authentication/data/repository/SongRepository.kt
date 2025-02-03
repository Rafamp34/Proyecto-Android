package com.alaturing.umusicapp.authentication.data.repository

import com.alaturing.umusicapp.main.song.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    suspend fun readAll(): Result<List<Song>>
    suspend fun readById(id: Int): Result<Song>
    fun observeAll(): Flow<Result<List<Song>>>
}