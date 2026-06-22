package com.example.pico_botella.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.databinding.FragmentChallengesBinding
import com.example.pico_botella.databinding.DialogDeleteChallengeBinding
import com.example.pico_botella.databinding.DialogEditarRetoBinding
import com.example.pico_botella.model.Challenge
import com.example.pico_botella.view.adapter.ChallengesAdapter
import com.example.pico_botella.viewmodel.ChallengesViewModel

class ChallengesFragment : Fragment() {

    private var _binding: FragmentChallengesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ChallengesViewModel by activityViewModels()
    
    private val adapter = ChallengesAdapter(
        onEditClick = { challenge -> showEditChallengeDialog(challenge) },
        onDeleteClick = { challenge -> showDeleteChallengeDialog(challenge) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        
        binding.fabAddChallenge.setOnClickListener {
            showAddChallengeDialog()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        binding.rvChallenges.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.allChallenges.observe(viewLifecycleOwner) { challenges ->
            adapter.submitList(challenges)
        }
    }

    private fun showAddChallengeDialog() {
        AddChallengeDialogFragment.newInstance().show(
            childFragmentManager, AddChallengeDialogFragment.TAG
        )
    }

    private fun showEditChallengeDialog(challenge: Challenge) {
        val dialogBinding = DialogEditarRetoBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.etReto.setText(challenge.description)

        dialogBinding.btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnGuardar.setOnClickListener {
            val description = dialogBinding.etReto.text.toString()
            if (description.isNotBlank()) {
                val updatedChallenge = challenge.copy(description = description)
                viewModel.updateChallenge(updatedChallenge)
                dialog.dismiss()
            }
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun showDeleteChallengeDialog(challenge: Challenge) {
        val dialogBinding = DialogDeleteChallengeBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.tvChallengeDescription.text = challenge.description

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSi.setOnClickListener {
            viewModel.deleteChallenge(challenge)
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}