package com.example.pico_botella.view.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.pico_botella.R
import com.example.pico_botella.databinding.DialogAddChallengeBinding
import com.example.pico_botella.viewmodel.ChallengesViewModel

class AddChallengeDialogFragment : DialogFragment() {

    private var _binding: DialogAddChallengeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChallengesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupDialogBehavior()
        setupValidation()
        setupListeners()
    }

    private fun setupDialogBehavior() {
        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupValidation() {
        updateSaveButton(binding.etChallenge.text.toString())

        binding.etChallenge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButton(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateSaveButton(text: String) {
        val isValid = text.trim().isNotEmpty()
        binding.btnSave.isEnabled = isValid
        binding.btnSave.isClickable = isValid

        if (isValid) {
            binding.btnSave.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.orange_pico)
            )
        } else {
            binding.btnSave.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#E0E0E0")
            )
        }
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val description = binding.etChallenge.text.toString().trim()
            if (description.isNotEmpty()) {
                viewModel.addChallenge(description)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddChallengeDialogFragment"
        fun newInstance() = AddChallengeDialogFragment()
    }
}