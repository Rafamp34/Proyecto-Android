package com.alaturing.umusicapp.authentication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alaturing.umusicapp.authentication.data.local.daos.ArtistDao
import com.alaturing.umusicapp.authentication.data.local.daos.PlaylistDao
import com.alaturing.umusicapp.authentication.data.local.daos.SongDao
import com.alaturing.umusicapp.authentication.data.local.daos.UserDao
import com.alaturing.umusicapp.authentication.data.local.entities.*
import com.alaturing.umusicapp.authentication.data.local.relations.PlaylistWithSongs
import com.alaturing.umusicapp.authentication.data.local.relations.SongWithArtists

@Database(
    entities = [
        UserEntity::class,
        SongEntity::class,
        ArtistEntity::class,
        PlaylistEntity::class,
        SongArtistCrossRef::class,
        PlaylistSongCrossRef::class
    ],
    views = [
        SongWithArtists::class,
        PlaylistWithSongs::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun songDao(): SongDao
    abstract fun artistDao(): ArtistDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "umusic_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}