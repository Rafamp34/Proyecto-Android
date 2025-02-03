// PlaylistsAdapter.kt
package com.alaturing.umusicapp.main.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil3.load
import com.alaturing.umusicapp.databinding.PlaylistItemBinding
import com.alaturing.umusicapp.main.playlist.model.Playlist

class PlaylistsAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistsAdapter.PlaylistViewHolder>(PlaylistDiffCallback) {

    inner class PlaylistViewHolder(
        private val binding: PlaylistItemBinding
    ) : ViewHolder(binding.root) {
        fun bind(playlist: Playlist) {
            binding.root.setOnClickListener { onPlaylistClick(playlist) }

            binding.playlistTitle.text = playlist.name
            binding.playlistAuthor.text = playlist.author
            binding.playlistDuration.text = formatDuration(playlist.duration)
            playlist.imageUrl?.let { binding.playlistCover.load(it) }
        }

        private fun formatDuration(seconds: Int): String {
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return "%d:%02d".format(minutes, remainingSeconds)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) =
        holder.bind(getItem(position))

    object PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist) =
            oldItem == newItem
    }
}