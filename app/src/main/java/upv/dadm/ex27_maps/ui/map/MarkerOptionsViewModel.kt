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

class MarkerOptionsViewModel : ViewModel() {

    private val _latLng = MutableLiveData<LatLng>()
    val latLng: LiveData<LatLng>
        get() = _latLng

    private val _markerOptions = MutableLiveData<MarkerOptions>()
    val markerOptions: LiveData<MarkerOptions>
        get() = _markerOptions

    fun setCoordinates(latLng: LatLng) {
        _latLng.value = latLng
    }

    fun setMarkerOptions(options: MarkerOptions) {
        _markerOptions.value = options
    }
}