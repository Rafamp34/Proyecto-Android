import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.alaturing.umusicapp.R
import com.alaturing.umusicapp.databinding.PlaylistSongItemBinding
import com.alaturing.umusicapp.main.song.model.Song
import com.alaturing.umusicapp.main.song.ui.SongsAdapter

class PlaylistSongAdapter : ListAdapter<Song, PlaylistSongAdapter.SongViewHolder>(SongsAdapter.SongDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = PlaylistSongItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
    }

    class SongViewHolder(
        private val binding: PlaylistSongItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.root.post {
                binding.songName.text = song.name
                binding.songArtist.text = song.author
                binding.songDuration.text = formatDuration(song.duration)
                song.imageUrl?.let { binding.songCover.load(it) }

            }
        }

        private fun formatDuration(seconds: Int): String {
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return String.format("%d:%02d", minutes, remainingSeconds)
        }

        companion object {
            private val SongDiffCallback = object : DiffUtil.ItemCallback<Song>() {
                override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
                    oldItem == newItem
            }
        }
    }
}