package com.example.pico_botella.di

import android.content.Context
import androidx.room.Room
import com.example.pico_botella.data.AppDatabase
import com.example.pico_botella.data.ChallengeDao
import com.example.pico_botella.repository.ChallengeRepository
import com.example.pico_botella.repository.LoginRepository
import com.example.pico_botella.repository.PokemonRepository
import com.example.pico_botella.webservice.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pico_botella_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideChallengeDao(appDatabase: AppDatabase): ChallengeDao {
        return appDatabase.challengeDao()
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideChallengeRepository(challengeDao: ChallengeDao): ChallengeRepository {
        return ChallengeRepository(challengeDao)
    }

    @Singleton
    @Provides
    fun provideLoginRepository(): LoginRepository {
        return LoginRepository()
    }

    @Singleton
    @Provides
    fun providePokemonRepository(apiService: ApiService): PokemonRepository {
        return PokemonRepository(apiService)
    }
}
