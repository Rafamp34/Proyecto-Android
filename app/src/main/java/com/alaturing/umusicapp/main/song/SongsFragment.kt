package com.alaturing.umusicapp.main.song.ui

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
import com.alaturing.umusicapp.databinding.FragmentSongsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SongsFragment : Fragment() {
    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SongsViewModel by viewModels()
    private lateinit var adapter: SongsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = SongsAdapter()
        binding.songsRv.adapter = adapter
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is SongsUiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.songsRv.isVisible = false
                            binding.errorText.isVisible = false
                        }
                        is SongsUiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.songsRv.isVisible = true
                            binding.errorText.isVisible = false
                            adapter.submitList(state.songs)
                        }
                        is SongsUiState.Error -> {
                            binding.progressBar.isVisible = false
                            binding.songsRv.isVisible = false
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