package com.example.pico_botella.view.fragment

import android.animation.Animator
import android.content.Intent
import android.net.Uri
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentHomeBinding
import com.example.pico_botella.databinding.DialogRandomChallengeBinding
import com.example.pico_botella.viewmodel.HomeViewModel
import com.example.pico_botella.viewmodel.ChallengeResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by activityViewModels()
    
    private var blinkAnimation: Animation? = null
    private var bgMediaPlayer: MediaPlayer? = null
    private var spinMediaPlayer: MediaPlayer? = null

    private var pendingResult: ChallengeResult? = null
    private var isCountdownFinished = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClicks()
        setupAnimations()
        
        binding.ivBottle.rotation = viewModel.lastAngle
    }

    private fun setupObservers() {
        viewModel.isAudioEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnPower.isActivated = !isEnabled
            if (isEnabled) {
                resumeBackgroundMusic()
            } else {
                pauseBackgroundMusic()
            }
        }

        viewModel.challengeResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                pendingResult = it
                checkAndShowDialog()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                binding.btnPressMeContainer.isVisible = true
                binding.vCircle.startAnimation(blinkAnimation)
                viewModel.clearError()
            }
        }
    }

    private fun checkAndShowDialog() {
        val result = pendingResult
        if (isCountdownFinished && result != null) {
            showRandomChallengeDialog(result)
            pendingResult = null
            isCountdownFinished = false
            viewModel.clearChallengeResult()
        }
    }

    private fun setupClicks() {
        binding.btnPressMeContainer.setOnClickListener {
            startBottleTurn()
        }

        binding.btnPower.setOnClickListener {
            viewModel.toggleAudio()
        }

        binding.btnInfo.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_instructionsFragment)
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_challengesFragment)
        }

        binding.btnStar.setOnClickListener {
            val url = "https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo abrir la tienda", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnShare.setOnClickListener {
            shareApp()
        }
    }

    private fun shareApp() {
        val title = "App pico botella"
        val slogan = "Solo los valientes lo juegan !!"
        val url = "https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es"
        
        val shareMessage = "$title\n$slogan\n$url"
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        
        val chooser = Intent.createChooser(intent, "Compartir usando:")
        startActivity(chooser)
    }

    private fun setupAnimations() {
        blinkAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
        binding.vCircle.startAnimation(blinkAnimation)
    }

    private fun startBottleTurn() {
        binding.vCircle.clearAnimation()
        binding.btnPressMeContainer.isVisible = false
        
        pauseBackgroundMusic()

        binding.ivBottle.rotation = viewModel.lastAngle

        val randomDuration = Random.nextLong(3000, 5001)
        val randomExtraRotation = Random.nextFloat() * 360f
        val fullSpins = (5..8).random() * 360f 
        val targetRotation = viewModel.lastAngle + fullSpins + randomExtraRotation

        playSpinSound()

        binding.ivBottle.animate()
            .rotation(targetRotation)
            .setDuration(randomDuration)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {
                    stopSpinSound()
                }
                override fun onAnimationEnd(animation: Animator) {
                    stopSpinSound()
                    viewModel.updateAngle(targetRotation)
                    startCountdown()
                }
            })
            .start()
        
        viewModel.fetchRandomChallengeAndPokemon()
    }

    private fun startCountdown() {
        lifecycleScope.launch {
            isCountdownFinished = false
            binding.tvCountdown.isVisible = true
            for (i in 3 downTo 0) {
                binding.tvCountdown.text = i.toString()
                if (i == 0) {
                    isCountdownFinished = true
                    checkAndShowDialog()
                }
                delay(1000)
            }
            binding.tvCountdown.isVisible = false
        }
    }

    private fun showRandomChallengeDialog(result: ChallengeResult) {
        val dialogBinding = DialogRandomChallengeBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.tvChallenge.text = result.challenge.description

        val imageUrl = result.pokemon.img.replace("http://", "https://")
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(dialogBinding.ivPokemon)

        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
            
            binding.btnPressMeContainer.isVisible = true
            binding.vCircle.startAnimation(blinkAnimation)
            
            if (viewModel.isAudioEnabled.value == true) {
                resumeBackgroundMusic()
            }
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun playSpinSound() {
        try {
            spinMediaPlayer?.release()
            spinMediaPlayer = MediaPlayer.create(requireContext(), R.raw.sonido_botella)
            spinMediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopSpinSound() {
        spinMediaPlayer?.stop()
        spinMediaPlayer?.release()
        spinMediaPlayer = null
    }

    private fun resumeBackgroundMusic() {
        if (bgMediaPlayer == null) {
            bgMediaPlayer = MediaPlayer.create(requireContext(), R.raw.background_music)
            bgMediaPlayer?.isLooping = true
        }
        if (!bgMediaPlayer!!.isPlaying) {
            bgMediaPlayer?.start()
        }
    }

    private fun pauseBackgroundMusic() {
        if (bgMediaPlayer?.isPlaying == true) {
            bgMediaPlayer?.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isAudioEnabled.value == true) {
            resumeBackgroundMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseBackgroundMusic()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bgMediaPlayer?.release()
        bgMediaPlayer = null
        stopSpinSound()
        _binding = null
    }
}
