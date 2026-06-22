package com.example.pico_botella.webservice

import com.example.pico_botella.model.ProductModelResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("master/pokedex.json")
    suspend fun getPokedex(): ProductModelResponse

    companion object {
        private const val BASE_URL = "https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}