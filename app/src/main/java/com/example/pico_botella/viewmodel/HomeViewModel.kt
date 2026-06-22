package com.example.pico_botella.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pico_botella.data.AppDatabase
import com.example.pico_botella.model.Challenge
import com.example.pico_botella.model.Pokemon
import com.example.pico_botella.webservice.ApiService
import com.example.pico_botella.repository.ChallengeRepository
import com.example.pico_botella.repository.PokemonRepository
import kotlinx.coroutines.launch

data class ChallengeResult(val challenge: Challenge, val pokemon: Pokemon)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val challengeRepository: ChallengeRepository
    private val pokemonRepository: PokemonRepository
    
    private val _isAudioEnabled = MutableLiveData<Boolean>(true)
    val isAudioEnabled: LiveData<Boolean> get() = _isAudioEnabled

    private val _challengeResult = MutableLiveData<ChallengeResult?>()
    val challengeResult: LiveData<ChallengeResult?> get() = _challengeResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private var _lastAngle = 0f
    val lastAngle: Float get() = _lastAngle

    init {
        val dao = AppDatabase.getDatabase(application).challengeDao()
        challengeRepository = ChallengeRepository(dao)
        pokemonRepository = PokemonRepository(ApiService.create())
    }

    fun updateAngle(newAngle: Float) {
        _lastAngle = newAngle % 360f
    }

    fun toggleAudio() {
        _isAudioEnabled.value = !(_isAudioEnabled.value ?: true)
    }

    fun fetchRandomChallengeAndPokemon() {
        viewModelScope.launch {
            try {
                val challenge = challengeRepository.getRandomChallenge()
                val pokemon = pokemonRepository.getRandomPokemon()

                if (challenge != null && pokemon != null) {
                    _challengeResult.postValue(ChallengeResult(challenge, pokemon))
                } else if (challenge == null) {
                    _error.postValue("No hay retos guardados. ¡Agrega uno primero!")
                }
            } catch (e: Exception) {
                _error.postValue("Error al conectar con la Pokedex")
            }
        }
    }

    fun clearChallengeResult() {
        _challengeResult.value = null
    }

    fun clearError() {
        _error.value = null
    }
}