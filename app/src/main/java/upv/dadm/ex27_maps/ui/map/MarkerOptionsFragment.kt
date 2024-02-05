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

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import upv.dadm.ex27_maps.R
import upv.dadm.ex27_maps.databinding.FragmentMarkerBinding

/**
 * Displays a form, as a BottomSheetDialog, to provide the details of a new marker.
 */
class MarkerOptionsFragment : BottomSheetDialogFragment(R.layout.fragment_marker) {

    // Reference to the ViewModel holding the details of the new marker
    private val viewModel: MarkerOptionsViewModel by activityViewModels()

    // Backing property to resource binding
    private var _binding: FragmentMarkerBinding? = null

    // Property valid between onCreateView() and onDestroyView()
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get the automatically generated view binding for the layout resource
        _binding = FragmentMarkerBinding.bind(view)

        // Update the color of the sample location icon whenever the Slider value changes
        binding.sColor.addOnChangeListener { _, _, _ ->
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
        // Manage the Save Button
        binding.bSave.setOnClickListener {
            if (binding.etTitle.text.toString().trim().isEmpty())
            // Display an error message if the title is empty
                Snackbar.make(binding.root, R.string.no_title, Snackbar.LENGTH_SHORT).show()
            else {
                // Add the new MarkerOptions to the ViewModel
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
                // Dismiss the dialog
                dismiss()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Display as location of the marker the position clicked by the user on the map
                viewModel.latLng.collect { latLng ->
                    binding.tvLatitude.text = latLng.latitude.toString()
                    binding.tvLongitude.text = latLng.longitude.toString()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear resources to make them eligible for garbage collection
        _binding = null
    }
}