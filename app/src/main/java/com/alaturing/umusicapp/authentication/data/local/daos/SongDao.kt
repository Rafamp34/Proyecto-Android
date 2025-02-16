package com.alaturing.umusicapp.authentication.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.alaturing.umusicapp.authentication.data.local.entities.SongArtistCrossRef
import com.alaturing.umusicapp.authentication.data.local.entities.SongEntity
import com.alaturing.umusicapp.authentication.data.local.relations.SongWithArtists

@Dao
interface SongDao {
    @Transaction
    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<SongWithArtists>

    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSong(songId: Int): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongArtistCrossRefs(crossRefs: List<SongArtistCrossRef>)
}