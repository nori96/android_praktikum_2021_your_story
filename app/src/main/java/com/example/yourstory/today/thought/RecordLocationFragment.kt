package com.example.yourstory.today.thought

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.yourstory.R
import com.example.yourstory.databinding.RecordLocationFragmentBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class RecordLocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: RecordLocationFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userLocation: LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RecordLocationFragmentBinding.inflate(inflater, container, false)
        hostFragmentNavController = NavHostFragment.findNavController(this)
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        binding.confirmThoughtDialogLocation.setOnClickListener {
            viewModelShared.location.value = userLocation
            hostFragmentNavController.navigate(R.id.action_recordLocationFragment_to_thought_dialog)
        }

        binding.cancelThoughtDialogLocation.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_recordLocationFragment_to_thought_dialog)
        }
        binding.recordLocationMapView.onCreate(savedInstanceState)
        binding.recordLocationMapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    // life cycle methods need to be implemented to get the map view working
    override fun onResume() {
        super.onResume()
        binding.recordLocationMapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        binding.recordLocationMapView.onPause()
    }

    override fun onDestroy() {
        binding.recordLocationMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.recordLocationMapView.onLowMemory()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState!!)
        binding.recordLocationMapView.onSaveInstanceState(outState)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                userLocation = LatLng(location!!.latitude, location.longitude)
                p0.addMarker(
                    MarkerOptions()
                        .position(userLocation)
                )
                val update = CameraUpdateFactory.newLatLngZoom(userLocation, 11f)
                p0.animateCamera(update)
            }
    }
}