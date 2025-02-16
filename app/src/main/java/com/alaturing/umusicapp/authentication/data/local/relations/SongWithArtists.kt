package com.alaturing.umusicapp.authentication.data.local.relations

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.alaturing.umusicapp.authentication.data.local.entities.ArtistEntity
import com.alaturing.umusicapp.authentication.data.local.entities.SongArtistCrossRef
import com.alaturing.umusicapp.authentication.data.local.entities.SongEntity

@DatabaseView(
    """
    SELECT * FROM songs
    """
)
data class SongWithArtists(
    @Embedded
    val song: SongEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = SongArtistCrossRef::class,
            parentColumn = "songId",
            entityColumn = "artistId"
        )
    )
    val artists: List<ArtistEntity>
)