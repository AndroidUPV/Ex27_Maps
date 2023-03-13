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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsViewModel : ViewModel() {

    enum class Mode { VIEWING, ADDING, REMOVING }

    private val _mode = MutableLiveData(Mode.VIEWING)
    val mode: LiveData<Mode>
        get() = _mode

    private val _markersList = MutableLiveData(listOf<MarkerOptions>())
    val markersList: LiveData<List<MarkerOptions>>
        get() = _markersList

    fun enableAddingMarkers() {
        _mode.value = Mode.ADDING
    }

    fun enableRemovingMarkers() {
        _mode.value = Mode.REMOVING
    }

    fun disableMarkersOperation() {
        _mode.value = Mode.VIEWING
    }

    fun addMarker(markerOptions: MarkerOptions) {
        _markersList.value = _markersList.value?.plus(markerOptions)
    }

    fun removeMarker(marker: Marker) {
        val markerOptions = _markersList.value?.first { options ->
            options.position == marker.position && options.title == marker.title && options.snippet == marker.snippet
        }
        _markersList.value = _markersList.value?.minus(markerOptions!!)
    }

}