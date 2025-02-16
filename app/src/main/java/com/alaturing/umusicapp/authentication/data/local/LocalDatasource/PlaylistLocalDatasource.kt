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
    suspend fun getAllPlaylists(): List<Playlist> {
        return playlistDao.getAllPlaylists().map { playlistWithSongs ->
            Playlist(
                id = playlistWithSongs.playlist.id,
                name = playlistWithSongs.playlist.name,
                author = playlistWithSongs.playlist.author,
                duration = playlistWithSongs.playlist.duration,
                imageUrl = playlistWithSongs.playlist.imageUrl,
                userId = playlistWithSongs.playlist.userId,
                songs = playlistWithSongs.songs.map { songWithArtists ->
                    Song(
                        id = songWithArtists.song.id,
                        name = songWithArtists.song.name,
                        lyrics = songWithArtists.song.lyrics,
                        album = songWithArtists.song.album,
                        duration = songWithArtists.song.duration,
                        imageUrl = songWithArtists.song.imageUrl,
                        artists = songWithArtists.artists.map { artistEntity ->
                            Artist(
                                id = artistEntity.id,
                                name = artistEntity.name,
                                listeners = artistEntity.listeners,
                                imageUrl = artistEntity.imageUrl
                            )
                        }
                    )
                }
            )
        }
    }

    suspend fun savePlaylist(playlist: Playlist) {
        val playlistEntity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            author = playlist.author,
            duration = playlist.duration,
            imageUrl = playlist.imageUrl,
            userId = playlist.userId
        )

        val playlistSongCrossRefs = playlist.songs.map { song ->
            PlaylistSongCrossRef(playlist.id, song.id)
        }

        playlistDao.insertPlaylist(playlistEntity)
        playlistDao.insertPlaylistSongCrossRefs(playlistSongCrossRefs)
    }


}