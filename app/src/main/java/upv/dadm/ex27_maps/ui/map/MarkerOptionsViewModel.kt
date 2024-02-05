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

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds information about the new MarkerOptions.
 */
class MarkerOptionsViewModel : ViewModel() {

    // Backing property for the selected location
    private val _latLng = MutableStateFlow(LatLng(0.0, 0.0))

    // Selected location
    val latLng = _latLng.asStateFlow()

    // Backing property for the new MarkerOptions (can be null)
    private val _markerOptions = MutableStateFlow<MarkerOptions?>(null)

    // New MarkerOptions (already added if null)
    val markerOptions = _markerOptions.asStateFlow()

    /**
     * Sets the location of the new MarkerOptions.
     */
    fun setCoordinates(latLng: LatLng) {
        _latLng.value = latLng
    }

    /**
     * Sets the new MarkerOptions.
     */
    fun setMarkerOptions(options: MarkerOptions) =
        _markerOptions.update { options }

    /**
     * Clear the new MarkerOptions as it has already been added to the map.
     */
    fun clearMarkerOptions() =
        _markerOptions.update { null }
}