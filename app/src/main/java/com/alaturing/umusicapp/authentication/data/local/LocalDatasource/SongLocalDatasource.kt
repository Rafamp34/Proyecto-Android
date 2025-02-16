package com.alaturing.umusicapp.authentication.data.local.LocalDatasource

import com.alaturing.umusicapp.authentication.data.local.daos.ArtistDao
import com.alaturing.umusicapp.authentication.data.local.daos.SongDao
import com.alaturing.umusicapp.authentication.data.local.entities.ArtistEntity
import com.alaturing.umusicapp.authentication.data.local.entities.SongArtistCrossRef
import com.alaturing.umusicapp.authentication.data.local.entities.SongEntity
import com.alaturing.umusicapp.authentication.model.Artist
import com.alaturing.umusicapp.main.song.model.Song
import javax.inject.Inject

class SongLocalDatasource @Inject constructor(
    private val songDao: SongDao,
    private val artistDao: ArtistDao
) {
    suspend fun getAllSongs(): List<Song> {
        return songDao.getAllSongs().map { songWithArtists ->
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
    }

    suspend fun saveSongs(songs: List<Song>) {
        val songEntities = songs.map { song ->
            SongEntity(
                id = song.id,
                name = song.name,
                lyrics = song.lyrics,
                album = song.album,
                duration = song.duration,
                imageUrl = song.imageUrl
            )
        }

        val artistEntities = songs.flatMap { it.artists }.distinct().map { artist ->
            ArtistEntity(
                id = artist.id,
                name = artist.name,
                listeners = artist.listeners,
                imageUrl = artist.imageUrl
            )
        }

        val songArtistCrossRefs = songs.flatMap { song ->
            song.artists.map { artist ->
                SongArtistCrossRef(song.id, artist.id)
            }
        }

        songDao.insertSongs(songEntities)
        artistDao.insertArtists(artistEntities)
        songDao.insertSongArtistCrossRefs(songArtistCrossRefs)
    }
}