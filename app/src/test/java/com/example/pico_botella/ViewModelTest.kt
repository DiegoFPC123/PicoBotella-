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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    lateinit var challengesViewModel: ChallengesViewModel
    lateinit var homeViewModel: HomeViewModel
    lateinit var loginViewModel: LoginViewModel

    @Mock lateinit var challengeRepository: ChallengeRepository
    @Mock lateinit var loginRepository: LoginRepository
    @Mock lateinit var pokemonRepository: PokemonRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Stub para evitar NullPointerException en el init de ChallengesViewModel
        `when`(challengeRepository.allChallenges).thenReturn(flowOf(emptyList()))

        challengesViewModel = ChallengesViewModel(challengeRepository)
        homeViewModel = HomeViewModel(challengeRepository, pokemonRepository)
        loginViewModel = LoginViewModel(loginRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testUpdateAngleCalculaAnguloCorrectamente() {
        homeViewModel.updateAngle(450f)
        assertEquals(90f, homeViewModel.lastAngle, 0.01f)
    }

    @Test
    fun testToggleAudioCambiaEstado() {
        homeViewModel.toggleAudio()
        assertEquals(false, homeViewModel.isAudioEnabled.value)
    }

    @Test
    fun testAddChallengeLlamaAlRepositorio() = runBlocking {
        challengesViewModel.addChallenge("Reto nuevo")
        verify(challengeRepository).insertChallenge(any())
    }

    @Test
    fun testDeleteChallengeLlamaAlRepositorio() = runBlocking {
        val challenge = Challenge(id = "1", description = "Test")
        challengesViewModel.deleteChallenge(challenge)
        verify(challengeRepository).deleteChallenge(challenge)
    }

    @Test
    fun testFetchRandomMuestraErrorCuandoNoHayRetos() = runBlocking {
        `when`(challengeRepository.getRandomChallenge()).thenReturn(null)
        homeViewModel.fetchRandomChallengeAndPokemon()
        assertEquals("No hay retos guardados. ¡Agrega uno primero!", homeViewModel.error.value)
    }

    @Test
    fun testSesionActivaCuandoHayEmail() {
        var resultado = false
        // Configuramos el mock para que ejecute el callback con 'true'
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean) -> Unit>(1)
            callback(true)
            null
        }.`when`(loginRepository).sesion(any(), any())

        loginViewModel.sesion("test@test.com") { resultado = it }

        verify(loginRepository).sesion(eq("test@test.com"), any())
        assertTrue(resultado)
    }
}
