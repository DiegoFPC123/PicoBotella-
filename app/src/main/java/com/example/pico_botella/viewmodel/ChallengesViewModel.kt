package com.example.pico_botella.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pico_botella.model.Challenge
import com.example.pico_botella.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val repository: ChallengeRepository
) : ViewModel() {
    
    val allChallenges = repository.allChallenges.asLiveData()

    init {
        // Iniciamos la sincronización en tiempo real con Firestore
        repository.startFirestoreSync(viewModelScope)
    }

    fun addChallenge(description: String) {
        viewModelScope.launch {
            repository.insertChallenge(Challenge(description = description))
        }
    }

    fun updateChallenge(challenge: Challenge) {
        viewModelScope.launch {
            repository.updateChallenge(challenge)
        }
    }

    fun deleteChallenge(challenge: Challenge) {
        viewModelScope.launch {
            repository.deleteChallenge(challenge)
        }
    }
}
