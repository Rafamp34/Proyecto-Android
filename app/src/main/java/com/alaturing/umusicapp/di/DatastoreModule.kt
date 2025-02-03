package com.umusicapp.umusicapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val USER_TOKEN = "user_token"

/** [Module] de hilt que crea un datastore para usuarios
 *
 */
@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {


    @Singleton
    @Provides
    fun provideAuthenticatedUserDatastore(
        @ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(USER_TOKEN) }

        )
    }
}