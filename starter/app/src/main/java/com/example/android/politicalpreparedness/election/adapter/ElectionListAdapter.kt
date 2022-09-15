package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ItemListElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener) :
    ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    // Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    // Create ElectionViewHolder
    // Add companion object to inflate ViewHolder (from)
    class ElectionViewHolder(val binding: ItemListElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Election, clickListener: ElectionListener) {
            binding.election = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ElectionViewHolder {
                val binding = ItemListElectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ElectionViewHolder(binding)
            }
        }
    }

    // Create ElectionDiffCallback
    class DiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

// Create ElectionListener
class ElectionListener(private val clickListener: (Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}