package com.alaturing.umusicapp.main.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.alaturing.umusicapp.authentication.ui.AuthenticationActivity
import com.alaturing.umusicapp.R
import com.alaturing.umusicapp.databinding.FragmentHomeBinding
import com.alaturing.umusicapp.main.playlist.PlaylistsAdapter
import com.alaturing.umusicapp.main.song.ui.SongsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var playlistsAdapter: PlaylistsAdapter
    private lateinit var songsAdapter: SongsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    viewModel.onLogout()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerViews() {
        // Setup Playlists Grid
        playlistsAdapter = PlaylistsAdapter(
            onPlaylistClick = { playlist ->
                // Navegar al detalle de la playlist
                findNavController().navigate(
                    R.id.playlistDetailFragment,
                    Bundle().apply {
                        putInt("playlistId", playlist.id)
                    }
                )
            },
            onDeleteClick = null  // No permitimos eliminar playlists desde la pantalla principal
        )
        binding.playlistsGrid.adapter = playlistsAdapter

        songsAdapter = SongsAdapter()
        binding.songsRecyclerView.adapter = songsAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is HomeUiState.Success -> {
                            playlistsAdapter.submitList(state.playlists)
                            songsAdapter.submitList(state.recentSongs)
                        }
                        is HomeUiState.LoggedOut -> {
                            startActivity(Intent(requireContext(), AuthenticationActivity::class.java))
                            requireActivity().finish()
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }
}