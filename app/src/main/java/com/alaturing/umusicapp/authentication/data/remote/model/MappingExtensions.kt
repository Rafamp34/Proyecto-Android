package com.alaturing.umusicapp.authentication.data.remote.model

import android.util.Log
import com.alaturing.umusicapp.authentication.model.Artist
import com.alaturing.umusicapp.authentication.model.User
import com.alaturing.umusicapp.di.NetworkModule
import com.alaturing.umusicapp.main.song.model.Song
import com.alaturing.umusicapp.main.playlist.model.Playlist

private fun processImageUrl(url: String?): String? {
    return url?.let {
        if (it.startsWith("http")) it else NetworkModule.STRAPI + it.removePrefix("/")
    }
}

fun AuthResponseBody.toModel(): User {
    return User(
        id = this.user.id,
        userName = this.user.username,
        email = this.user.email,
        imageUrl = this.user.image?.url,
        followers = this.user.followers,
        following = this.user.following,
        token = this.jwt
    )
}

fun SongResponse.toSong(): Song {
    Log.d("SongMapping", "Mapping song: ${attributes.name}")
    val artistsList = attributes.artists_IDS?.data?.map { artistData ->
        Artist(
            id = artistData.id,
            name = artistData.attributes.Name,
            listeners = artistData.attributes.listeners,
            imageUrl = processImageUrl(artistData.attributes.image?.data?.attributes?.url)
        )
    } ?: emptyList()
    Log.d("SongMapping", "Found ${artistsList.size} artists for song ${attributes.name}")

    return Song(
        id = id,
        name = attributes.name,
        album = attributes.album,
        duration = attributes.duration,
        lyrics = attributes.lyrics,
        imageUrl = processImageUrl(attributes.image?.data?.attributes?.url),
        artists = artistsList
    )
}

fun ArtistResponse.toModel(): Artist {
    return Artist(
        id = this.id,
        name = this.attributes.Name,
        listeners = this.attributes.listeners,
        imageUrl = processImageUrl(this.attributes.image?.data?.attributes?.url)
    )
}

fun PlaylistResponse.toModel(): Playlist {
    Log.d("PlaylistMapping", "Starting to map playlist: ${attributes.name}")

    // Try to find a user ID, defaulting to 0 if not found
    val userId = this.attributes.users_IDS?.data?.firstOrNull()?.id ?: 0

    val songsList = this.attributes.song_IDS?.data?.map { songResponse ->
        Log.d("PlaylistMapping", "Mapping song: ${songResponse.attributes.name}")

        // Fetch artists directly from the song's artists_IDS
        val artistsList = songResponse.attributes.artists_IDS?.data?.map { artistData ->
            Log.d("PlaylistMapping", "Mapping artist for song ${songResponse.attributes.name}: ${artistData.attributes.Name}")

            Artist(
                id = artistData.id,
                name = artistData.attributes.Name,
                listeners = artistData.attributes.listeners,
                imageUrl = processImageUrl(artistData.attributes.image?.data?.attributes?.url)
            )
        } ?: emptyList()

        Log.d("PlaylistMapping", "Found ${artistsList.size} artists for song ${songResponse.attributes.name}")

        Song(
            id = songResponse.id,
            name = songResponse.attributes.name,
            album = songResponse.attributes.album,
            duration = songResponse.attributes.duration,
            lyrics = songResponse.attributes.lyrics,
            imageUrl = processImageUrl(songResponse.attributes.image?.data?.attributes?.url),
            artists = artistsList
        )
    } ?: emptyList()

    Log.d("PlaylistMapping", "Found ${songsList.size} songs for playlist ${attributes.name}")

    return Playlist(
        id = this.id,
        name = this.attributes.name,
        author = this.attributes.author,
        duration = this.attributes.duration,
        imageUrl = processImageUrl(this.attributes.image?.data?.attributes?.url),
        songs = songsList,
        userId = userId
    )
}

fun AuthResponseUser.toModel(): User {
    return User(
        id = this.id,
        userName = this.username,
        email = this.email,
        imageUrl = this.image?.url,
        followers = this.followers,
        following = this.following,
        token = null
    )
}

fun PlaylistSongResponse.toModel(): Song {
    Log.d("SongMapping", "Mapping playlist song: ${attributes.name}")
    val artistsList = attributes.artists_IDS?.data?.map { artistData ->
        Artist(
            id = artistData.id,
            name = artistData.attributes.Name,  // Asegurarnos de usar Name con mayúscula
            listeners = artistData.attributes.listeners,
            imageUrl = processImageUrl(artistData.attributes.image?.data?.attributes?.url)
        ).also { artist ->
            Log.d("SongMapping", "Mapped artist: ${artist.name} for song ${attributes.name}")
        }
    } ?: emptyList()

    Log.d("SongMapping", "Found ${artistsList.size} artists for playlist song ${attributes.name}")
    artistsList.forEach { artist ->
        Log.d("SongMapping", "Artist for ${attributes.name}: ${artist.name}")
    }

    return Song(
        id = this.id,
        name = this.attributes.name,
        lyrics = this.attributes.lyrics,
        album = this.attributes.album,
        duration = this.attributes.duration,
        imageUrl = processImageUrl(this.attributes.image?.data?.attributes?.url),
        artists = artistsList
    ).also { song ->
        Log.d("SongMapping", "Final song mapping - ${song.name} has ${song.artists.size} artists")
    }
}