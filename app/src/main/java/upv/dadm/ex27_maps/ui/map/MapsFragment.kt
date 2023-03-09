/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex27_maps.ui.map

import android.graphics.Camera
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import upv.dadm.ex27_maps.R
import upv.dadm.ex27_maps.databinding.FragmentMapsBinding

class MapsFragment : Fragment(R.layout.fragment_maps), MenuProvider {

    private val viewModel: MapsViewModel by viewModels()
    private val markerViewModel: MarkerOptionsViewModel by activityViewModels()

    private var _binding: FragmentMapsBinding? = null
    private val binding
        get() = _binding!!

    private var googleMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapsBinding.bind(view)

        requireActivity().addMenuProvider(this)

        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync { map ->
            googleMap = map
            registerMapClickListener()
            googleMap!!.setOnInfoWindowClickListener {marker ->
                googleMap!!.animateCamera(
                    CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(marker.position, 12.0f))
                )
            }

            markerViewModel.markerOptions.observe(viewLifecycleOwner) { markerOptions ->
                googleMap!!.addMarker(markerOptions)
            }

            requireActivity().invalidateMenu()

        }

        viewModel.isAddMarkersEnabled.observe(viewLifecycleOwner) {
            requireActivity().invalidateMenu()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
        menuInflater.inflate(R.menu.menu_maps, menu)

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.mEnableAddMarkers).isVisible =
            googleMap != null && viewModel.isAddMarkersEnabled.value == false
        menu.findItem(R.id.mDisableAddMarkers).isVisible =
            googleMap != null && viewModel.isAddMarkersEnabled.value == true
        menu.findItem(R.id.mMapType).isVisible = googleMap != null
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            R.id.mEnableAddMarkers -> {
                viewModel.enableAddingMarkers(true)
                registerMapClickListener()
                true
            }
            R.id.mDisableAddMarkers -> {
                viewModel.enableAddingMarkers(false)
                unregisterMapClickListener()
                true
            }
            R.id.mNormalMap -> {
                googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                menuItem.isChecked = true
                true
            }
            R.id.mTerrainMap -> {
                googleMap!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
                menuItem.isChecked = true
                true
            }
            R.id.mSatelliteMap -> {
                googleMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
                menuItem.isChecked = true
                true
            }
            else -> false
        }

    private fun registerMapClickListener() =
        googleMap!!.setOnMapClickListener { latLng ->
            markerViewModel.setCoordinates(latLng)
            findNavController().currentDestination?.getAction(R.id.actionShowMarkerDetails)?.let {
                findNavController().navigate(R.id.actionShowMarkerDetails)
            }
        }

    private fun unregisterMapClickListener() =
        googleMap!!.setOnMapClickListener(null)
}