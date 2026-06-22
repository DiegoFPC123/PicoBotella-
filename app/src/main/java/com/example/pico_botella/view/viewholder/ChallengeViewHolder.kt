package com.example.pico_botella.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.pico_botella.databinding.ItemChallengeBinding
import com.example.pico_botella.model.Challenge

class ChallengeViewHolder(private val binding: ItemChallengeBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(challenge: Challenge, onEditClick: (Challenge) -> Unit, onDeleteClick: (Challenge) -> Unit) {
        binding.tvChallengeDescription.text = challenge.description
        
        binding.btnEdit.setOnClickListener {
            onEditClick(challenge)
        }
        
        binding.btnDelete.setOnClickListener {
            onDeleteClick(challenge)
        }
    }
}