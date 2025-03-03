package com.alaturing.umusicapp.main.profile

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil3.load
import com.alaturing.umusicapp.R
import com.alaturing.umusicapp.authentication.ui.AuthenticationActivity
import com.alaturing.umusicapp.common.utils.CameraComponents
import com.alaturing.umusicapp.common.utils.MapComponents
import com.alaturing.umusicapp.databinding.DialogCreatePlaylistBinding
import com.alaturing.umusicapp.databinding.FragmentProfileBinding
import com.alaturing.umusicapp.main.playlist.PlaylistsAdapter
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var playlistsAdapter: PlaylistsAdapter
    private var selectedImageUri: Uri? = null

    // Componentes
    private lateinit var cameraComponents: CameraComponents
    private lateinit var mapComponents: MapComponents

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar componentes
        initializeComponents()

        setupToolbar()
        setupRecyclerView()
        setupOptionsButton()
        observeUiState()
        observeUserPlaylists()
    }

    private fun initializeComponents() {
        // Inicializar componente de cámara
        cameraComponents = CameraComponents(this)
        cameraComponents.setOnImageSelectedListener { uri ->
            handleImageSelection(uri)
        }

        // Inicializar componente de mapa
        mapComponents = MapComponents(this)
    }

    private fun setupRecyclerView() {
        playlistsAdapter = PlaylistsAdapter(
            onPlaylistClick = { playlist ->
                findNavController().navigate(
                    R.id.playlistDetailFragment,
                    Bundle().apply {
                        putInt("playlistId", playlist.id)
                    }
                )
            },
            onDeleteClick = { playlist ->
                showDeletePlaylistDialog(playlist)
            }
        )
        binding.userPlaylistsRecyclerView.adapter = playlistsAdapter
    }

    private fun setupOptionsButton() {
        binding.optionsButton.setOnClickListener { view ->
            showOptionsMenu(view)
        }
    }

    private fun showOptionsMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.profile_options_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_playlist -> {
                    showCreatePlaylistDialog()
                    true
                }
                R.id.action_show_map -> {
                    // Usar el componente de mapa para mostrar la ubicación del usuario
                    mapComponents.showMapBottomSheet()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private lateinit var dialogBinding: DialogCreatePlaylistBinding

    private fun showCreatePlaylistDialog() {
        dialogBinding = DialogCreatePlaylistBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.create_playlist))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.create), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val playlistName = dialogBinding.playlistNameInput.text.toString()
                if (playlistName.isNotEmpty()) {
                    (viewModel.uiState.value as? ProfileUiState.Success)?.let { state ->
                        viewModel.createPlaylist(playlistName, state.user.userName, selectedImageUri)
                    }
                    dialog.dismiss()
                }
            }
        }

        dialogBinding.selectImageButton.setOnClickListener {
            // Usar el componente de cámara para seleccionar imagen
            cameraComponents.selectImageFromGallery()
        }

        dialogBinding.takePhotoButton.setOnClickListener {
            // Usar el componente de cámara para tomar foto
            cameraComponents.requestCameraPermissionAndTakePhoto()
        }

        dialog.show()
    }

    private fun showDeletePlaylistDialog(playlist: Playlist) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_playlist)
            .setMessage(getString(R.string.delete_playlist_message, playlist.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deletePlaylist(playlist)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun handleImageSelection(uri: Uri) {
        selectedImageUri = uri
        dialogBinding.playlistImage.load(uri)
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
                            binding.playlistsGroup.isVisible = false
                            binding.errorText.isVisible = false
                        }
                        is ProfileUiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.contentGroup.isVisible = true
                            binding.playlistsGroup.isVisible = true
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
                            binding.playlistsGroup.isVisible = false
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

    private fun observeUserPlaylists() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userPlaylists.collect { playlists ->
                    playlistsAdapter.submitList(playlists)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}