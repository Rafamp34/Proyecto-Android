package com.alaturing.umusicapp.main.playlistDetails

import PlaylistSongAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.alaturing.umusicapp.R
import com.alaturing.umusicapp.common.utils.formatDuration
import com.alaturing.umusicapp.databinding.FragmentPlaylistDetailBinding
import com.alaturing.umusicapp.main.song.model.Song
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistDetailFragment : Fragment() {
    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistDetailViewModel by viewModels()
    private lateinit var songsAdapter: PlaylistSongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupToolbar()
        observeUiState()

        arguments?.getInt("playlistId")?.let { playlistId ->
            viewModel.loadPlaylist(playlistId)
        }

        binding.addSongButton.setOnClickListener {
            showAddSongDialog()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_playlist -> {
                    // Implementar edición de playlist
                    true
                }

                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        songsAdapter = PlaylistSongAdapter(
            onDeleteClick = { song ->
                if ((viewModel.uiState.value as? PlaylistDetailUiState.Success)?.playlist?.isEditable == true) {
                    showDeleteSongDialog(song)
                }
            },
            isEditable = false  // Valor inicial, se actualizará cuando se cargue el estado
        )
        binding.playlistSongs.adapter = songsAdapter
    }

    private class SongDialogAdapter(
        private val context: Context,
        private val songs: List<Song>
    ) : ArrayAdapter<Song>(context, R.layout.dialog_song_item, songs) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.dialog_song_item, parent, false)

            val song = getItem(position)
            val imageView = view.findViewById<ImageView>(R.id.songImage)
            val textView = view.findViewById<TextView>(R.id.songName)
            val authorTextView = view.findViewById<TextView>(R.id.songAuthor)
            val durationTextView = view.findViewById<TextView>(R.id.songDuration)

            song?.let {
                textView.text = it.name
                authorTextView.text = it.artists.joinToString(", ") { artist -> artist.name }
                durationTextView.text = formatDuration(it.duration)
                it.imageUrl?.let { url ->
                    imageView.load(url) {
                        placeholder(R.drawable.placeholder_song)
                        crossfade(true)
                    }
                }
            }

            return view
        }

        private fun formatDuration(seconds: Int): String {
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return String.format("%d:%02d", minutes, remainingSeconds)
        }
    }

    private fun showAddSongDialog() {
        viewModel.getAvailableSongs()?.let { availableSongs ->
            val adapter = SongDialogAdapter(requireContext(), availableSongs)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_song_to_playlist)
                .setAdapter(adapter) { _, which ->
                    viewModel.addSongToPlaylist(availableSongs[which])
                }
                .show()
        }
    }

    private fun showDeleteSongDialog(song: Song) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.remove_song)
            .setMessage(getString(R.string.remove_song_message, song.name))
            .setPositiveButton(R.string.remove) { _, _ ->
                viewModel.removeSongFromPlaylist(song)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is PlaylistDetailUiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.playlistSongs.isVisible = true
                            binding.errorText.isVisible = false

                            with(state.playlist) {
                                binding.playlistTitle.text = name
                                binding.playlistAuthor.text = author
                                binding.playlistDuration.text = formatDuration(duration)
                                imageUrl?.let { binding.playlistImage.load(it) }
                                binding.addSongButton.isVisible = isEditable

                                songsAdapter = PlaylistSongAdapter(
                                    onDeleteClick = { song ->
                                        showDeleteSongDialog(song)
                                    },
                                    isEditable = isEditable
                                )
                                binding.playlistSongs.adapter = songsAdapter
                                songsAdapter.submitList(state.songs)
                            }
                        }

                        is PlaylistDetailUiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.playlistSongs.isVisible = false
                            binding.errorText.isVisible = false
                        }

                        is PlaylistDetailUiState.Error -> {
                            binding.progressBar.isVisible = false
                            binding.playlistSongs.isVisible = false
                            binding.errorText.isVisible = true
                            binding.errorText.text = state.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}