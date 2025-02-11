package com.alaturing.umusicapp.main.song.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil3.load
import com.alaturing.umusicapp.databinding.SongItemBinding
import com.alaturing.umusicapp.main.song.model.Song

class SongsAdapter : ListAdapter<Song, SongsAdapter.SongViewHolder>(SongDiffCallback) {

    inner class SongViewHolder(
        private val binding: SongItemBinding
    ) : ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.songTitle.text = song.name
            val artistsNames = song.artists.map { it.name }.joinToString(", ")
            binding.songArtist.text = artistsNames
            binding.songArtist.isVisible = artistsNames.isNotBlank()
            binding.songDuration.text = formatDuration(song.duration)
            song.imageUrl?.let { binding.songImage.load(it) }
        }

        private fun formatDuration(seconds: Int): String {
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return "%d:%02d".format(minutes, remainingSeconds)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = SongItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) =
        holder.bind(getItem(position))

    object SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Song, newItem: Song) =
            oldItem == newItem
    }
}