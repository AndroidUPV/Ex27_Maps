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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapsViewModel : ViewModel() {

    enum class Mode { VIEWING, ADDING, REMOVING }

    private val _mode = MutableStateFlow(Mode.VIEWING)
    val mode = _mode.asStateFlow()

    private val _markersList = MutableStateFlow(listOf<MarkerOptions>())
    val markersList = _markersList.asStateFlow()

    private val _mapType = MutableStateFlow(GoogleMap.MAP_TYPE_NORMAL)
    val mapType = _mapType.asStateFlow()

    fun enableAddingMarkers() {
        _mode.value = Mode.ADDING
    }

    fun enableRemovingMarkers() {
        _mode.value = Mode.REMOVING
    }

    fun disableMarkersOperation() {
        _mode.value = Mode.VIEWING
    }

    fun addMarker(markerOptions: MarkerOptions) =
        _markersList.update { _markersList.value.plus(markerOptions) }

    fun removeMarker(marker: Marker) {
        val markerOptions = _markersList.value.first { options ->
            options.position == marker.position && options.title == marker.title && options.snippet == marker.snippet
        }
        _markersList.value = _markersList.value.minus(markerOptions)
    }

    fun setMapType(type: Int) =
        _mapType.update { type }
}