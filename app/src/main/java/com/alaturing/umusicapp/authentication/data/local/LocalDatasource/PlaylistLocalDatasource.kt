package com.alaturing.umusicapp.authentication.data.local.LocalDatasource

import com.alaturing.umusicapp.authentication.data.local.daos.PlaylistDao
import com.alaturing.umusicapp.authentication.data.local.entities.PlaylistEntity
import com.alaturing.umusicapp.authentication.data.local.entities.PlaylistSongCrossRef
import com.alaturing.umusicapp.authentication.model.Artist
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song
import javax.inject.Inject

class PlaylistLocalDatasource @Inject constructor(
    private val playlistDao: PlaylistDao
) {
    suspend fun deletePlaylist(playlistId: Int) {
        playlistDao.deletePlaylistById(playlistId)
    }

}