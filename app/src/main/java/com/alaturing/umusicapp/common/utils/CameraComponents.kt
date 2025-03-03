package com.alaturing.umusicapp.common.utils

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

/**
 * Componentes relacionados con la cámara para ser utilizados en fragments que necesiten
 * funcionalidad de captura de fotos.
 */
class CameraComponents(private val fragment: Fragment) {

    private var photoUri: Uri? = null
    private var onImageSelectedListener: ((Uri) -> Unit)? = null

    /**
     * Contrato para seleccionar una imagen de la galería.
     */
    val pickImage = fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleImageSelection(it) }
    }

    /**
     * Contrato para tomar una foto con la cámara.
     */
    val takePhoto = fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let { handleImageSelection(it) }
        }
    }

    /**
     * Contrato para solicitar permisos de cámara.
     */
    val requestPermissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            takeNewPhoto()
        }
    }

    /**
     * Establece el listener para cuando se selecciona una imagen.
     */
    fun setOnImageSelectedListener(listener: (Uri) -> Unit) {
        onImageSelectedListener = listener
    }

    /**
     * Solicita permisos de cámara y toma una foto si los permisos están concedidos.
     */
    fun requestCameraPermissionAndTakePhoto() {
        when {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takeNewPhoto()
            }
            fragment.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showCameraPermissionRationale()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    /**
     * Muestra un diálogo explicando por qué se necesita el permiso de cámara.
     */
    private fun showCameraPermissionRationale() {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle("Permiso de cámara necesario")
            .setMessage("Se necesita acceso a la cámara para tomar fotos")
            .setPositiveButton("Conceder") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Toma una nueva foto y guarda el URI temporal.
     */
    private fun takeNewPhoto() {
        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            fragment.requireContext().cacheDir
        )
        photoUri = FileProvider.getUriForFile(
            fragment.requireContext(),
            "${fragment.requireContext().packageName}.provider",
            photoFile
        )
        takePhoto.launch(photoUri)
    }

    /**
     * Maneja la selección de una imagen y notifica al listener.
     */
    private fun handleImageSelection(uri: Uri) {
        onImageSelectedListener?.invoke(uri)
    }

    /**
     * Inicia la selección de una imagen desde la galería.
     */
    fun selectImageFromGallery() {
        pickImage.launch("image/*")
    }
}