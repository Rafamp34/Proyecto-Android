package com.alaturing.umusicapp.main.profile.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.alaturing.umusicapp.R
import com.alaturing.umusicapp.authentication.ui.AuthenticationActivity
import com.alaturing.umusicapp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        observeUiState()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    viewModel.onLogout()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ProfileUiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.contentGroup.isVisible = false
                            binding.errorText.isVisible = false
                        }
                        is ProfileUiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.contentGroup.isVisible = true
                            binding.errorText.isVisible = false

                            with(state.user) {
                                binding.userName.text = userName
                                binding.userEmail.text = email
                                binding.followersCount.text = resources.getQuantityString(
                                    R.plurals.followers, followers, followers
                                )
                                binding.followingCount.text = resources.getQuantityString(
                                    R.plurals.following, following, following
                                )

                                // Simplified image loading with proper error handling
                                Log.d("ProfileFragment", "Loading image URL: $imageUrl")
                                if (imageUrl != null) {
                                    try {
                                        binding.userImage.load(imageUrl)
                                    } catch (e: Exception) {
                                        Log.e("ProfileFragment", "Error loading image", e)
                                        binding.userImage.setImageResource(R.drawable.ic_person)
                                    }
                                } else {
                                    binding.userImage.setImageResource(R.drawable.ic_person)
                                }
                            }
                        }
                        is ProfileUiState.Error -> {
                            binding.progressBar.isVisible = false
                            binding.contentGroup.isVisible = false
                            binding.errorText.isVisible = true
                            binding.errorText.text = state.message
                        }
                        ProfileUiState.LoggedOut -> {
                            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            requireActivity().finish()
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