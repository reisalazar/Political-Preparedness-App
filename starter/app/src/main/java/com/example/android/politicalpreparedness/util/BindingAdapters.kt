package com.example.android.politicalpreparedness.util

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.VoterInfo
import java.util.*

@BindingAdapter("electionTitle")
fun bindElectionTitle(view: TextView, voterInfo: VoterInfo?) {
    voterInfo?.run {
        view.text = view.resources.getString(R.string.election_information, stateName)
    }
}

@BindingAdapter("buttonFollowElection")
fun bindButtonFollowElection(button: Button, isElectionSaved: Boolean?) {
    if(isElectionSaved != null) {
        if (isElectionSaved) {
            button.text = button.resources.getString(R.string.unfollow_election)
        } else {
            button.text = button.resources.getString(R.string.follow_election)
        }
    } else {
        button.text = button.resources.getString(R.string.follow_election)
    }
}

@BindingAdapter("fetchImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        // Add Glide call to load image and circle crop - user ic_profile as a placeholder and for errors.
        Glide.with(view.context)
            .load(src)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
            .circleCrop()
            .into(view)
    }
}
@BindingAdapter("dateText")
fun bindFormatDate(textView: TextView, date: Date?) {
    textView.text = date?.toString()
}