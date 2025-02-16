package com.alaturing.umusicapp.authentication.data.remote.model

import com.alaturing.umusicapp.authentication.model.Artist
import com.alaturing.umusicapp.authentication.model.User
import com.alaturing.umusicapp.common.utils.parseDuration
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
    val artistsList = attributes.artists_IDS?.data?.map { artistData ->
        Artist(
            id = artistData.id,
            name = artistData.attributes.Name,
            listeners = artistData.attributes.listeners,
            imageUrl = processImageUrl(artistData.attributes.image?.data?.attributes?.url)
        )
    } ?: emptyList()

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

fun PlaylistSongResponse.toSong(): Song {
    val artistsList = attributes.artists_IDS?.data?.map { artistData ->
        Artist(
            id = artistData.id,
            name = artistData.attributes.Name,
            listeners = artistData.attributes.listeners,
            imageUrl = processImageUrl(artistData.attributes.image?.data?.attributes?.url)
        )
    } ?: emptyList()

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
    val userId = this.attributes.users_IDS?.data?.firstOrNull()?.id ?: 0

    val songsList = this.attributes.song_IDS?.data?.map { it.toSong() } ?: emptyList()
    val durationInSeconds = parseDuration(this.attributes.duration)

    return Playlist(
        id = this.id,
        name = this.attributes.name,
        author = this.attributes.author,
        duration = durationInSeconds, // Asignar el valor convertido
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
