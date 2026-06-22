package com.example.pico_botella.view.fragment

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyPremiumGradient()

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.bottle_animation)
        binding.ivBottle.startAnimation(animation)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(5000)
            navigateToHome()
        }
    }

    private fun applyPremiumGradient() {
        binding.tvAppName.post {
            val paint = binding.tvAppName.paint
            val textShader: Shader = LinearGradient(
                0f, 0f, 0f, binding.tvAppName.height.toFloat(),
                intArrayOf(
                    ContextCompat.getColor(requireContext(), R.color.vibrant_orange_light),
                    ContextCompat.getColor(requireContext(), R.color.vibrant_orange)
                ), null, Shader.TileMode.CLAMP
            )
            binding.tvAppName.paint.shader = textShader
            binding.tvAppName.invalidate()
        }
    }

    private fun navigateToHome() {
        if (isAdded) {
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}