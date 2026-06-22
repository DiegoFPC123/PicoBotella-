package com.example.pico_botella.view.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonsState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (password.isNotEmpty() && password.length < 6) {
                    binding.tilPassword.error = "Mínimo 6 dígitos"
                    binding.tilPassword.isErrorEnabled = true
                } else {
                    binding.tilPassword.error = null
                    binding.tilPassword.isErrorEnabled = false
                }
                updateButtonsState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun updateButtonsState() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val isEnabled = email.isNotEmpty() && password.isNotEmpty()

        binding.btnLogin.isEnabled = isEnabled
        binding.tvRegister.isEnabled = isEnabled

        if (isEnabled) {
            // Login Button enabled state
            binding.btnLogin.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btnLogin.setTypeface(null, Typeface.BOLD)

            // Register Button enabled state
            binding.tvRegister.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.tvRegister.setTypeface(null, Typeface.BOLD)
        } else {
            // Login Button disabled state (using default selector color if defined, or setting manually)
            // Color #80FFFFFF was used in button_text_selector
            binding.btnLogin.setTypeface(null, Typeface.NORMAL)

            // Register Button disabled state
            binding.tvRegister.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_text))
            binding.tvRegister.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val sharedPref = requireActivity().getSharedPreferences("PicoBotellaPrefs", Context.MODE_PRIVATE)
        val registeredPassword = sharedPref.getString(email, null)

        if (registeredPassword != null && registeredPassword == password) {
            // Login successful
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        } else {
            // Login failed
            Toast.makeText(requireContext(), "Login incorrecto", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}