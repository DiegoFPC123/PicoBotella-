package com.example.pico_botella.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pico_botella.databinding.ItemChallengeBinding
import com.example.pico_botella.model.Challenge

class ChallengesAdapter(
    private val onEditClick: (Challenge) -> Unit,
    private val onDeleteClick: (Challenge) -> Unit
) : ListAdapter<Challenge, ChallengesAdapter.ChallengeViewHolder>(ChallengeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = ItemChallengeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChallengeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.bind(getItem(position), onEditClick, onDeleteClick)
    }

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

    class ChallengeDiffCallback : DiffUtil.ItemCallback<Challenge>() {
        override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean = oldItem == newItem
    }
}