package com.alaturing.umusicapp.authentication.data.remote.model

import com.alaturing.umusicapp.authentication.data.remote.PlaylistSongResponse
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
        imageUrl = processImageUrl(this.user.image?.data?.attributes?.url),
        followers = this.user.followers,
        following = this.user.following,
        token = this.jwt
    )
}

fun SongResponse.toModel(): Song {
    return Song(
        id = this.id,
        name = this.attributes.name,
        author = this.attributes.author,
        album = this.attributes.album,
        duration = this.attributes.duration,
        imageUrl = processImageUrl(this.attributes.image?.data?.attributes?.url)
    )
}

fun PlaylistResponse.toModel(): Playlist {
    return Playlist(
        id = this.id,
        name = this.attributes.name,
        author = this.attributes.author,
        duration = this.attributes.duration,
        imageUrl = processImageUrl(this.attributes.image?.data?.attributes?.url),
        songs = this.attributes.song_IDS?.data?.map { it.toModel() } ?: emptyList()
    )
}

fun AuthResponseUser.toModel(): User {
    return User(
        id = this.id,
        userName = this.username,
        email = this.email,
        imageUrl = processImageUrl(this.image?.data?.attributes?.url),
        followers = this.followers,
        following = this.following,
        token = null
    )
}

fun PlaylistSongResponse.toModel(): Song {
    return Song(
        id = this.id,
        name = this.attributes.name,
        author = this.attributes.author,
        album = this.attributes.album,
        duration = this.attributes.duration,
        imageUrl = processImageUrl(this.attributes.image?.data?.attributes?.url)
    )
}