package com.alaturing.umusicapp.authentication.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.alaturing.umusicapp.authentication.data.local.entities.PlaylistEntity
import com.alaturing.umusicapp.authentication.data.local.entities.PlaylistSongCrossRef
import com.alaturing.umusicapp.authentication.data.local.relations.PlaylistWithSongs

@Dao
interface PlaylistDao {
    @Transaction
    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylists(): List<PlaylistWithSongs>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylist(playlistId: Int): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSongCrossRefs(crossRefs: List<PlaylistSongCrossRef>)
}
