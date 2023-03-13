/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex27_maps.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Holds information about the new MarkerOptions.
 */
class MarkerOptionsViewModel : ViewModel() {

    // Backing property for the selected location
    private val _latLng = MutableLiveData<LatLng>()

    // Selected location
    val latLng: LiveData<LatLng>
        get() = _latLng

    // Backing property for the new MarkerOptions (can be null)
    private val _markerOptions = MutableLiveData<MarkerOptions?>()

    // New MarkerOptions (already added if null)
    val markerOptions: LiveData<MarkerOptions?>
        get() = _markerOptions

    /**
     * Sets the location of the new MarkerOptions.
     */
    fun setCoordinates(latLng: LatLng) {
        _latLng.value = latLng
    }

    /**
     * Sets the new MarkerOptions.
     */
    fun setMarkerOptions(options: MarkerOptions) {
        _markerOptions.value = options
    }

    /**
     * Clear the new MarkerOptions as it has already been added to the map.
     */
    fun clearMarkerOptions() {
        _markerOptions.value = null
    }
}