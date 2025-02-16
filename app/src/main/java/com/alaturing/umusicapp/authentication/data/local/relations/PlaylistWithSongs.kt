package com.alaturing.umusicapp.authentication.data.local.relations

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.alaturing.umusicapp.authentication.data.local.entities.PlaylistEntity
import com.alaturing.umusicapp.authentication.data.local.entities.PlaylistSongCrossRef
import com.alaturing.umusicapp.authentication.data.local.entities.SongEntity

@DatabaseView(
    """
    SELECT * FROM playlists
    """
)
data class PlaylistWithSongs(
    @Embedded
    val playlist: PlaylistEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PlaylistSongCrossRef::class,
            parentColumn = "playlistId",
            entityColumn = "songId"
        )
    )
    val songs: List<SongWithArtists>
)