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
import javax.inject.Inject

class ChallengeRepository @Inject constructor(private val challengeDao: ChallengeDao) {
    
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val challengesCollection by lazy { firestore.collection("challenges") }

    // Usamos get() para que Mockito no intente inicializar el DAO al mockear el repo
    val allChallenges: Flow<List<Challenge>> get() = challengeDao.getAllChallenges()

    fun startFirestoreSync(scope: CoroutineScope) {
        try {
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
        } catch (e: Exception) {
            // Ignorar en tests unitarios
        }
    }

    suspend fun insertChallenge(challenge: Challenge) {
        try {
            val docRef = challengesCollection.document() 
            val challengeWithId = challenge.copy(id = docRef.id)
            docRef.set(challengeWithId).await()
            challengeDao.insertChallenge(challengeWithId)
        } catch (e: Exception) {
            val tempId = System.currentTimeMillis().toString()
            challengeDao.insertChallenge(challenge.copy(id = tempId))
        }
    }

    suspend fun updateChallenge(challenge: Challenge) {
        try {
            if (challenge.id.isNotEmpty()) {
                challengesCollection.document(challenge.id).set(challenge).await()
                challengeDao.updateChallenge(challenge)
            }
        } catch (e: Exception) {
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
            challengeDao.deleteChallenge(challenge)
        }
    }

    suspend fun getRandomChallenge(): Challenge? {
        return challengeDao.getRandomChallenge()
    }
}
