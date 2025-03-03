package com.alaturing.umusicapp.common.utils

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.alaturing.umusicapp.R

/**
 * Componente para manejar la funcionalidad de mapas en la aplicación.
 */
class MapComponents(private val fragment: Fragment) {

    private var map: GoogleMap? = null
    private var onMapReadyListener: ((GoogleMap) -> Unit)? = null
    private lateinit var locationComponents: LocationComponents
    private var userLocation: LatLng? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog

    init {
        locationComponents = LocationComponents(fragment)
        locationComponents.setOnLocationReadyListener { latLng ->
            userLocation = latLng
            addMarker(latLng, "Mi ubicación")
        }
    }

    /**
     * Callback que se ejecuta cuando el mapa está listo.
     */
    private val mapReadyCallback = OnMapReadyCallback { googleMap ->
        map = googleMap

        // Configurar el mapa
        map?.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isZoomGesturesEnabled = true
                isScrollGesturesEnabled = true
                isMyLocationButtonEnabled = true
                isCompassEnabled = true
            }

            // Intentar activar la capa de "Mi ubicación" si hay permisos
            try {
                isMyLocationEnabled = true
            } catch (e: SecurityException) {
                // No hay permisos de ubicación
            }
        }

        // Notificar al listener que el mapa está listo
        onMapReadyListener?.invoke(googleMap)

        // Obtener la ubicación del usuario
        locationComponents.requestLocationPermissionAndGetLocation()
    }

    /**
     * Establece un listener para cuando el mapa esté listo.
     */
    fun setOnMapReadyListener(listener: (GoogleMap) -> Unit) {
        onMapReadyListener = listener
    }

    /**
     * Muestra un mapa en un BottomSheet con la ubicación del usuario.
     */
    fun showMapBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(fragment.requireContext())
        val bottomSheetView = fragment.layoutInflater.inflate(R.layout.bottom_sheet_map, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        // Configurar título y botón
        val titleTextView = bottomSheetView.findViewById<TextView>(R.id.tvMapTitle)
        titleTextView.text = "Mi Ubicación"

        val saveButton = bottomSheetView.findViewById<Button>(R.id.btnSaveLocation)
        saveButton.setOnClickListener {
            // Cerrar el bottom sheet
            bottomSheetDialog.dismiss()
        }

        // Inicializar el mapa en el bottom sheet
        val mapFragment = fragment.childFragmentManager
            .findFragmentById(R.id.mapView) as? SupportMapFragment

        mapFragment?.getMapAsync(mapReadyCallback)

        bottomSheetDialog.show()
    }

    /**
     * Añade un marcador en el mapa en la ubicación especificada.
     */
    fun addMarker(latLng: LatLng, title: String = "Ubicación") {
        map?.clear() // Limpiar marcadores anteriores
        map?.addMarker(MarkerOptions()
            .position(latLng)
            .title(title))
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    /**
     * Mueve la cámara a la ubicación especificada.
     */
    fun moveCamera(latLng: LatLng, zoom: Float = 15f) {
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    /**
     * Obtiene la ubicación actual del usuario.
     */
    fun getUserLocation() {
        locationComponents.requestLocationPermissionAndGetLocation()
    }
}