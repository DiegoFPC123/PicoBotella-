package com.example.pico_botella.repository

import com.example.pico_botella.data.ChallengeDao
import com.example.pico_botella.model.Challenge
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChallengeRepository(private val challengeDao: ChallengeDao) {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val challengesCollection = firestore.collection("challenges")

    val allChallenges: Flow<List<Challenge>> = challengeDao.getAllChallenges()

    /**
     * Inicia la escucha en tiempo real de Firestore y sincroniza con Room.
     * Al usar el ID de Firestore como Primary Key en Room, evitamos duplicados.
     */
    fun startFirestoreSync(scope: CoroutineScope) {
        challengesCollection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            scope.launch(Dispatchers.IO) {
                snapshot.documents.forEach { doc ->
                    val challenge = doc.toObject<Challenge>()
                    challenge?.copy(id = doc.id)?.let {
                        challengeDao.insertChallenge(it)
                    }
                }
            }
        }
    }

    suspend fun insertChallenge(challenge: Challenge) {
        try {
            // 1. Crear documento en Firestore para obtener su ID
            val docRef = challengesCollection.document() 
            val challengeWithId = challenge.copy(id = docRef.id)
            
            // 2. Guardar en Firestore
            docRef.set(challengeWithId).await()
            
            // 3. Guardar en Room
            challengeDao.insertChallenge(challengeWithId)
        } catch (e: Exception) {
            e.printStackTrace()
            // Si falla la red, guardamos local con un ID temporal
            val tempId = System.currentTimeMillis().toString()
            challengeDao.insertChallenge(challenge.copy(id = tempId))
        }
    }

    suspend fun updateChallenge(challenge: Challenge) {
        try {
            if (challenge.id.isNotEmpty()) {
                // Actualizar en Firestore
                challengesCollection.document(challenge.id).set(challenge).await()
                // Actualizar local
                challengeDao.updateChallenge(challenge)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            challengeDao.updateChallenge(challenge)
        }
    }

    suspend fun deleteChallenge(challenge: Challenge) {
        try {
            if (challenge.id.isNotEmpty()) {
                challengesCollection.document(challenge.id).delete().await()
            }
            challengeDao.deleteChallenge(challenge)
        } catch (e: Exception) {
            e.printStackTrace()
            challengeDao.deleteChallenge(challenge)
        }
    }

    suspend fun getRandomChallenge(): Challenge? {
        return challengeDao.getRandomChallenge()
    }
}