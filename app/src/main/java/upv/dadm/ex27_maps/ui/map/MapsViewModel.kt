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
import com.google.android.gms.maps.model.MarkerOptions

class MapsViewModel: ViewModel() {

    private val _isAddMarkersEnabled = MutableLiveData(true)
    val isAddMarkersEnabled: LiveData<Boolean> = _isAddMarkersEnabled

    fun enableAddingMarkers(enabled: Boolean) {
        _isAddMarkersEnabled.value = enabled
    }
}