package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ItemListRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Representative

class RepresentativeListAdapter
    : ListAdapter<Representative, RepresentativeViewHolder>(RepresentativeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class RepresentativeViewHolder(val binding: ItemListRepresentativeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private var twitterURL: String? = null
    private var facebookURL: String? = null
    private var url: String? = null

    fun bind(item: Representative) {
        binding.representative = item
        binding.representativePhoto.setImageResource(R.drawable.ic_profile)

        // Show social links ** Hint: Use provided helper methods
        bindSocialMediaLinks(item)

        // Show www link ** Hint: Use provided helper methods
        binding.executePendingBindings()
    }

    // Add companion object to inflate ViewHolder (from)
    companion object {
        fun from(parent: ViewGroup): RepresentativeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemListRepresentativeBinding.inflate(layoutInflater, parent, false)
            return RepresentativeViewHolder(binding)
        }
    }

    private fun bindSocialMediaLinks(item: Representative) {
        twitterURL = getTwitterURL(item)
        facebookURL = getFacebookURL(item)
        url = getRepresentativeURL(item)

        if (twitterURL == null) {
            binding.twitterIcon.visibility = View.GONE
        } else {
            binding.twitterIcon.setOnClickListener {
                startActivityUrlIntent(twitterURL!!)
            }
        }
        if (facebookURL == null) {
            binding.facebookIcon.visibility = View.GONE
        } else {
            binding.facebookIcon.setOnClickListener {
                startActivityUrlIntent(facebookURL!!)
            }
        }
        if (url == null) {
            binding.wwwIcon.visibility = View.GONE
        } else {
            binding.wwwIcon.setOnClickListener {
                startActivityUrlIntent(url!!)
            }
        }
    }

    private fun getTwitterURL(representative: Representative): String? {
        val listChannel = representative.official.channels?.filter {
            it.type == "Twitter"
        }
        val channel = listChannel?.firstOrNull()
        var twitterURL: String? = null
        channel?.run {
            twitterURL = "https://www.twitter.com/${channel.id}"
        }
        return twitterURL
    }

    private fun getFacebookURL(representative: Representative): String? {
        val listChannels = representative.official.channels?.filter {
            it.type == "Facebook"
        }
        val channel = listChannels?.firstOrNull()
        var URL: String? = null
        channel?.run {
            URL = "https://www.facebook.com/${channel.id}"
        }
        return URL
    }

    private fun getRepresentativeURL(representative: Representative): String? {
        return representative.official.urls?.firstOrNull()
    }

    private fun startActivityUrlIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }

}

// Create RepresentativeDiffCallback
class RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
    override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem.official.name == newItem.official.name
    }

    override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem == newItem
    }
}

// Create RepresentativeListener