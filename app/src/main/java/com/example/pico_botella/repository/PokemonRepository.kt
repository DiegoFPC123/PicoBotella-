package com.example.pico_botella.repository

import com.example.pico_botella.model.Pokemon
import com.example.pico_botella.webservice.ApiService

class PokemonRepository(private val apiService: ApiService) {
    private var cachedPokemonList: List<Pokemon>? = null

    suspend fun getRandomPokemon(): Pokemon? {
        return try {
            val list = cachedPokemonList ?: apiService.getPokedex().pokemon.also {
                cachedPokemonList = it
            }
            if (list.isNotEmpty()) list.random() else null
        } catch (e: Exception) {
            null
        }
    }
}