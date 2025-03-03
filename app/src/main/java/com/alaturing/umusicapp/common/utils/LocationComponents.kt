package com.alaturing.umusicapp.common.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Componente para manejar la ubicación del usuario.
 */
class LocationComponents(private val fragment: Fragment) {

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(fragment.requireActivity())
    private var onLocationReadyListener: ((LatLng) -> Unit)? = null

    /**
     * Contrato para solicitar permisos de ubicación.
     */
    val requestLocationPermissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationPermissionsGranted = permissions.entries.all { it.value }

        if (locationPermissionsGranted) {
            getUserLocation()
        } else {
            showLocationPermissionDeniedMessage()
        }
    }

    /**
     * Establece el listener para cuando la ubicación esté lista.
     */
    fun setOnLocationReadyListener(listener: (LatLng) -> Unit) {
        onLocationReadyListener = listener
    }

    /**
     * Solicita permisos de ubicación y obtiene la ubicación del usuario si los permisos están concedidos.
     */
    fun requestLocationPermissionAndGetLocation() {
        when {
            hasLocationPermissions() -> {
                getUserLocation()
            }
            fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showLocationPermissionRationale()
            }
            else -> {
                requestLocationPermissions()
            }
        }
    }

    /**
     * Verifica si la aplicación tiene permisos de ubicación.
     */
    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    fragment.requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Solicita permisos de ubicación.
     */
    private fun requestLocationPermissions() {
        requestLocationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    /**
     * Muestra un diálogo explicando por qué se necesitan los permisos de ubicación.
     */
    private fun showLocationPermissionRationale() {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle("Permiso de ubicación necesario")
            .setMessage("Se necesita acceso a tu ubicación para mostrarla en el mapa")
            .setPositiveButton("Conceder") { _, _ ->
                requestLocationPermissions()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Muestra un mensaje cuando los permisos de ubicación son denegados.
     */
    private fun showLocationPermissionDeniedMessage() {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle("Permiso denegado")
            .setMessage("No se puede acceder a tu ubicación. Se mostrará una ubicación predeterminada.")
            .setPositiveButton("Aceptar", null)
            .show()
    }

    /**
     * Obtiene la ubicación actual del usuario.
     */
    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        if (hasLocationPermissions()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        onLocationReadyListener?.invoke(userLatLng)
                    } else {
                        // Si no se puede obtener la ubicación, usar ubicación predeterminada (Madrid)
                        val defaultLocation = LatLng(40.416775, -3.703790)
                        onLocationReadyListener?.invoke(defaultLocation)
                    }
                }
                .addOnFailureListener {
                    // En caso de fallo, usar ubicación predeterminada
                    val defaultLocation = LatLng(40.416775, -3.703790)
                    onLocationReadyListener?.invoke(defaultLocation)
                }
        }
    }
}