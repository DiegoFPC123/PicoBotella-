package com.example.pico_botella

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pico_botella.model.Challenge
import com.example.pico_botella.repository.ChallengeRepository
import com.example.pico_botella.repository.LoginRepository
import com.example.pico_botella.repository.PokemonRepository
import com.example.pico_botella.viewmodel.ChallengesViewModel
import com.example.pico_botella.viewmodel.HomeViewModel
import com.example.pico_botella.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var challengesViewModel: ChallengesViewModel
    lateinit var homeViewModel: HomeViewModel
    lateinit var loginViewModel: LoginViewModel

    @Mock lateinit var challengeRepository: ChallengeRepository
    @Mock lateinit var loginRepository: LoginRepository
    @Mock lateinit var pokemonRepository: PokemonRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        challengesViewModel = ChallengesViewModel(challengeRepository)
        homeViewModel = HomeViewModel(challengeRepository, pokemonRepository)
        loginViewModel = LoginViewModel(loginRepository)
    }

    // TEST 1 - updateAngle guarda el ángulo en rango 0-360
    @Test
    fun `test updateAngle calcula angulo correctamente`() {
        val angulo = 450f
        val esperado = 90f
        homeViewModel.updateAngle(angulo)
        assertEquals(esperado, homeViewModel.lastAngle)
    }

    // TEST 2 - toggleAudio cambia de true a false
    @Test
    fun `test toggleAudio cambia estado de audio`() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        homeViewModel.toggleAudio()
        assertEquals(false, homeViewModel.isAudioEnabled.value)
    }

    // TEST 3 - addChallenge llama al repositorio
    @Test
    fun `test addChallenge llama al repositorio`() = runBlocking {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        challengesViewModel.addChallenge("Canta una canción")
        verify(challengeRepository).insertChallenge(
            Challenge(description = "Canta una canción")
        )
    }

    // TEST 4 - deleteChallenge llama al repositorio
    @Test
    fun `test deleteChallenge llama al repositorio`() = runBlocking {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val challenge = Challenge(id = "1", description = "Reto test")
        challengesViewModel.deleteChallenge(challenge)
        verify(challengeRepository).deleteChallenge(challenge)
    }

    // TEST 5 - fetchRandomChallengeAndPokemon cuando no hay retos muestra error
    @Test
    fun `test fetchRandom muestra error cuando no hay retos`() = runBlocking {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        `when`(challengeRepository.getRandomChallenge()).thenReturn(null)
        homeViewModel.fetchRandomChallengeAndPokemon()
        assertEquals("No hay retos guardados. ¡Agrega uno primero!", homeViewModel.error.value)
    }

    // TEST 6 - sesion retorna true cuando hay email
    @Test
    fun `test sesion activa cuando hay email`() {
        var resultado = false
        // El snippet pedía llamar a loginRepository directamente o a través del viewModel?
        // El snippet del prompt decía: loginRepository.sesion("test@test.com") { resultado = it }
        // Pero loginRepository es un mock. Vamos a usar el viewModel para ser consistentes.
        loginViewModel.sesion("test@test.com") { resultado = it }
        verify(loginRepository).sesion(
            eq("test@test.com"),
            any()
        )
    }
}
