package com.alaturing.umusicapp.main.playlistDetails

import PlaylistSongAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil3.load
import com.alaturing.umusicapp.databinding.FragmentPlaylistDetailBinding
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
        observeUiState()

        // Obtener el ID de la playlist desde los argumentos
        arguments?.getInt("playlistId")?.let { playlistId ->
            viewModel.loadPlaylist(playlistId)
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        songsAdapter = PlaylistSongAdapter()
        binding.playlistSongs.adapter = songsAdapter
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is PlaylistDetailUiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.content.isVisible = false
                            binding.errorText.isVisible = false
                        }
                        is PlaylistDetailUiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.content.isVisible = true
                            binding.errorText.isVisible = false

                            with(state.playlist) {
                                binding.playlistTitle.text = name
                                binding.playlistAuthor.text = author
                                binding.playlistDuration.text = duration.toString()
                                imageUrl?.let { binding.playlistImage.load(it) }
                            }
                            songsAdapter.submitList(state.songs)
                        }
                        is PlaylistDetailUiState.Error -> {
                            binding.progressBar.isVisible = false
                            binding.content.isVisible = false
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