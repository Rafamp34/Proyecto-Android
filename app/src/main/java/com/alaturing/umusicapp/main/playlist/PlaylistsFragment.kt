// PlaylistsFragment.kt
package com.alaturing.umusicapp.main.playlist.ui

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
import androidx.navigation.fragment.findNavController
import com.alaturing.umusicapp.databinding.FragmentPlaylistsBinding
import com.alaturing.umusicapp.main.playlist.PlaylistsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModels()
    private lateinit var adapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = PlaylistsAdapter { playlist ->
            // Navegar al detalle de la playlist
            val action = PlaylistsFragmentDirections.actionPlaylistsFragmentToPlaylistDetailFragment(playlist.id)
            findNavController().navigate(action)
        }
        binding.playlistsRv.adapter = adapter
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is PlaylistsUiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.playlistsRv.isVisible = false
                            binding.errorText.isVisible = false
                        }
                        is PlaylistsUiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.playlistsRv.isVisible = true
                            binding.errorText.isVisible = false
                            adapter.submitList(state.playlists)
                        }
                        is PlaylistsUiState.Error -> {
                            binding.progressBar.isVisible = false
                            binding.playlistsRv.isVisible = false
                            binding.errorText.isVisible = true
                            binding.errorText.text = state.message
                        }

                        else -> {}
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