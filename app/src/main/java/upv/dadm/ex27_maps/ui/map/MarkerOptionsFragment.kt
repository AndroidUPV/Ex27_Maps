/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex27_maps.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import upv.dadm.ex27_maps.R
import upv.dadm.ex27_maps.databinding.FragmentMarkerBinding

class MarkerOptionsFragment : BottomSheetDialogFragment(R.layout.fragment_marker) {

    private val viewModel: MarkerOptionsViewModel by activityViewModels()

    private var _binding: FragmentMarkerBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMarkerBinding.bind(view)

        binding.sColor.addOnChangeListener { _, value, _ ->
            binding.ivColor.setColorFilter(
                Color.HSVToColor(
                    floatArrayOf(
                        binding.sColor.value,
                        1.0f,
                        1.0f
                    )
                )
            )
        }
        binding.bSave.setOnClickListener {
            viewModel.setMarkerOptions(
                MarkerOptions()
                    .position(
                        LatLng(
                            binding.tvLatitude.text.toString().toDouble(),
                            binding.tvLongitude.text.toString().toDouble()
                        )
                    )
                    .title(binding.etTitle.text.toString())
                    .snippet(binding.etDescription.text.toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(binding.sColor.value))
            )
            dismiss()
        }

        viewModel.latLng.observe(viewLifecycleOwner) { latLng ->
            binding.tvLatitude.text = latLng.latitude.toString()
            binding.tvLongitude.text = latLng.longitude.toString()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}