package com.example.pico_botella.view.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentLoginBinding
import com.example.pico_botella.model.UserRequest
import com.example.pico_botella.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

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
        setupObservers()
        checkSession()
    }

    private fun checkSession() {
        val sharedPref = requireActivity().getSharedPreferences("PicoBotellaPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", null)
        loginViewModel.sesion(email) { isLogged ->
            if (isLogged) {
                binding.root.visibility = View.INVISIBLE
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }
    }

    private fun setupObservers() {
        loginViewModel.isRegister.observe(viewLifecycleOwner) { userResponse ->
            if (userResponse.isRegister) {
                requireActivity().getSharedPreferences("PicoBotellaPrefs", Context.MODE_PRIVATE)
                    .edit().putString("email", userResponse.email).apply()
                Toast.makeText(requireContext(), userResponse.message, Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {
                Toast.makeText(requireContext(), "Error en el registro", Toast.LENGTH_SHORT).show()
            }
        }
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
        binding.etPassword.addTextChangedListener(textWatcher)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            loginViewModel.loginUser(email, password) { isLogin ->
                if (isLogin) {
                    requireActivity().getSharedPreferences("PicoBotellaPrefs", Context.MODE_PRIVATE)
                        .edit().putString("email", email).apply()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Toast.makeText(requireContext(), "Login incorrecto", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            loginViewModel.registerUser(UserRequest(email, password))
        }
    }

    private fun updateButtonsState() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val isEnabled = email.isNotEmpty() && password.isNotEmpty()

        binding.btnLogin.isEnabled = isEnabled
        binding.tvRegister.isEnabled = isEnabled

        if (isEnabled) {
            binding.tvRegister.setTextColor(Color.WHITE)
            binding.tvRegister.setTypeface(null, Typeface.BOLD)
        } else {
            binding.tvRegister.setTextColor(Color.parseColor("#9EA1A1"))
            binding.tvRegister.setTypeface(null, Typeface.NORMAL)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}