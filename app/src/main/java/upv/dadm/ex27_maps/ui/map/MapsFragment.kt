/*
 * Copyright (c) 2022-2023 Universitat Politècnica de València
 * Authors: David de Andrés and Juan Carlos Ruiz
 *          Fault-Tolerant Systems
 *          Instituto ITACA
 *          Universitat Politècnica de València
 *
 * Distributed under MIT license
 * (See accompanying file LICENSE.txt)
 */

package upv.dadm.ex27_maps.ui.map

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.launch
import upv.dadm.ex27_maps.R
import upv.dadm.ex27_maps.databinding.InfoWindowBinding

/**
 * Displays a map and enables adding and removing markers.
 */
class MapsFragment : Fragment(R.layout.fragment_maps), MenuProvider {

    // Reference to the ViewModel holding the list of markers
    private val viewModel: MapsViewModel by viewModels()

    // Reference to the ViewModel holding the details of the new marker
    private val markerViewModel: MarkerOptionsViewModel by activityViewModels()

    // Reference to the GoogleMap instance
    private var googleMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add this Fragment as MenuProvider to the MainActivity
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Wait for the GoogleMap to be ready before enabling any other action
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync { map ->
            // Store a reference to the GoogleMap instance
            googleMap = map
            // React to clicks on the InfoWindow
            setInfoWindowClickListener()
            // Define how to display InfoWindows
            setInfoWindowAdapter()
            // React to clicks on the Markers
            registerMarkerClickListener()
            // Set up all the required observers
            setupObservers()
            // Invalidate the menu to enable/disable the required action elements
            requireActivity().invalidateMenu()
        }
    }

    /**
     * Determines how to displays InfoWindow.
     */
    private fun setInfoWindowAdapter() {
        googleMap?.setInfoWindowAdapter(object : InfoWindowAdapter {

            override fun getInfoContents(p0: Marker): View? {
                return null
            }

            /**
             * Create a View to act as frame and set its content.
             */
            override fun getInfoWindow(p0: Marker): View {
                val binding = InfoWindowBinding.inflate(layoutInflater)
                binding.tvTitleInfoWindow.text = p0.title
                binding.tvSnippetInfoWindow.text = p0.snippet
                return binding.root
            }

        })
    }

    /**
     * Sets up the required observers on properties exposed by the ViewModels.
     */
    private fun setupObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Add the new marker to the map
                markerViewModel.markerOptions.collect { markerOptions ->
                    if (markerOptions != null) {
                        // Add the marker to the map
                        viewModel.addMarker(markerOptions)
                        // Clear the details for a new marker
                        markerViewModel.clearMarkerOptions()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Redraw the markers when markers are added/deleted
                viewModel.markersList.collect { list ->
                    // Clear the markers from the map
                    googleMap!!.clear()
                    // Go through the list of markers and add each of them to the map
                    list.forEach { markerOptions -> googleMap!!.addMarker(markerOptions) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Recreate the options menu when the operation mode changes
                viewModel.mode.collect { requireActivity().invalidateMenu() }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mapType.collect { mapType ->
                    when (mapType) {
                        GoogleMap.MAP_TYPE_TERRAIN -> {
                            googleMap!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
                        }

                        GoogleMap.MAP_TYPE_SATELLITE -> {
                            googleMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        }

                        else -> {
                            googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                        }
                    }
                }
            }
        }
    }

    // Populates the ActionBar with action elements
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
        menuInflater.inflate(R.menu.menu_maps, menu)

    // Allows the modification of elements of the already created menu before showing it
    override fun onPrepareMenu(menu: Menu) {
        // Display the add markers action element when in VIEWING mode (only if the map is ready)
        menu.findItem(R.id.mAddMarkers).isVisible =
            googleMap != null && viewModel.mode.value == MapsViewModel.Mode.VIEWING
        // Display the remove markers action element when in VIEWING mode (only if the map is ready)
        menu.findItem(R.id.mRemoveMarkers).isVisible =
            googleMap != null && viewModel.mode.value == MapsViewModel.Mode.VIEWING
        // Display the action element to stop the current operation when
        // in ADDING or REMOVING mode (only if the map is ready)
        menu.findItem(R.id.mStopMarkersOperation).isVisible =
            googleMap != null && viewModel.mode.value != MapsViewModel.Mode.VIEWING
        // Display the map type action element when the map is ready
        menu.findItem(R.id.mMapType).isVisible = googleMap != null
    }

    // Reacts to the selection of action elements
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        // Determine the action to take place according to its Id
        when (menuItem.itemId) {

            // Enter the ADDING markers mode
            R.id.mAddMarkers -> {
                viewModel.enableAddingMarkers()
                // React to clicking on the map
                registerMapClickListener()
                true
            }

            // Enter the REMOVE markers mode
            R.id.mRemoveMarkers -> {
                viewModel.enableRemovingMarkers()
                true
            }

            // Leave the ADDING or REMOVING markers mode
            R.id.mStopMarkersOperation -> {
                viewModel.disableMarkersOperation()
                // Stop reacting to clicks on the map
                unregisterMapClickListener()
                true
            }

            // Change the map to normal type
            R.id.mNormalMap -> {
                viewModel.setMapType(GoogleMap.MAP_TYPE_NORMAL)
                menuItem.isChecked = true
                true
            }

            // Change the map to terrain type
            R.id.mTerrainMap -> {
                viewModel.setMapType(GoogleMap.MAP_TYPE_TERRAIN)
                menuItem.isChecked = true
                true
            }

            // Change the map to satellite type
            R.id.mSatelliteMap -> {
                viewModel.setMapType(GoogleMap.MAP_TYPE_SATELLITE)
                menuItem.isChecked = true
                true
            }

            // If none of the custom action elements was selected, let the system deal with it
            else -> false
        }

    /**
     * Registers a listener to react to clicks on the Markers.
     */
    private fun registerMarkerClickListener() =
        googleMap!!.setOnMarkerClickListener { marker ->
            // Remove the clicked marker if on REMOVING mode
            if (viewModel.mode.value == MapsViewModel.Mode.REMOVING) {
                viewModel.removeMarker(marker)
                true
            }
            // Allow the default behaviour otherwise, which is display the related InfoWindow
            else false
        }

    /**
     * Registers a listener to set the location clicked on the map as the location of
     * the new marker and display the dialog to complete the required  details.
     */
    private fun registerMapClickListener() =
        googleMap!!.setOnMapClickListener { latLng ->
            // Set the obtained location as the location of the new marker
            markerViewModel.setCoordinates(latLng)
            // Display a BottomSheetDialog to complete the details of the new marker
            findNavController().currentDestination?.getAction(R.id.actionShowMarkerDetails)?.let {
                findNavController().navigate(R.id.actionShowMarkerDetails)
            }
        }

    /**
     * Stop reacting to clicks on the map .
     */
    private fun unregisterMapClickListener() =
        googleMap!!.setOnMapClickListener(null)

    /**
     * Registers a listener to zoom in on the clicked InfoWindow.
     */
    private fun setInfoWindowClickListener() {
        googleMap!!.setOnInfoWindowClickListener { marker ->
            // Animate the movement of the camera
            googleMap!!.animateCamera(
                // provide the new position and zoom level
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(marker.position, 12.0f)
                )
            )
        }
    }

}