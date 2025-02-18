package com.alaturing.umusicapp.main.profile.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import com.google.android.gms.maps.OnMapReadyCallback
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.alaturing.umusicapp.databinding.DialogCreatePlaylistBinding
import com.alaturing.umusicapp.databinding.FragmentProfileBinding
import com.alaturing.umusicapp.main.playlist.PlaylistsAdapter
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ProfileFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var playlistsAdapter: PlaylistsAdapter
    private var selectedImageUri: Uri? = null
    private var photoUri: Uri? = null
    private var map: GoogleMap? = null

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
        setupRecyclerView()
        setupOptionsButton()
        observeUiState()
        observeUserPlaylists()
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
                    showMapBottomSheet()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun showMapBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_map, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        // Inicializar el mapa en el bottom sheet
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        bottomSheetDialog.show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Configurar el mapa
        map?.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isZoomGesturesEnabled = true
                isScrollGesturesEnabled = true
            }

            // Mostrar una ubicación por defecto (por ejemplo, Madrid)
            val defaultLocation = LatLng(40.416775, -3.703790)
            addMarker(MarkerOptions().position(defaultLocation).title("Mi ubicación"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleImageSelection(it) }
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let { handleImageSelection(it) }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            takeNewPhoto()
        }
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
            pickImage.launch("image/*")
        }

        dialogBinding.takePhotoButton.setOnClickListener {
            requestCameraPermissionAndTakePhoto()
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

    private fun requestCameraPermissionAndTakePhoto() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takeNewPhoto()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showCameraPermissionRationale()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showCameraPermissionRationale() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permiso de cámara necesario")
            .setMessage("Se necesita acceso a la cámara para tomar fotos")
            .setPositiveButton("Conceder") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun takeNewPhoto() {
        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            requireContext().cacheDir
        )
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )
        takePhoto.launch(photoUri)
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