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
import android.location.LocationListener
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import java.lang.Exception


class RecordLocationFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: RecordLocationFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var userLocation: LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        userLocation = LatLng(getLocation()!!.latitude, getLocation()!!.longitude)
        return binding.root
    }
    
    override fun onResume() {
        super.onResume()
        binding.recordLocationMapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        binding.recordLocationMapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.recordLocationMapView.onLowMemory()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        p0.addMarker(
            MarkerOptions()
                .position(userLocation)
        )
        val update = CameraUpdateFactory.newLatLngZoom(userLocation, 11f)
        p0.uiSettings.isMapToolbarEnabled = false
        p0.animateCamera(update)
    }

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            val locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!isGPSEnabled) {
                return null
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000*60L,
                10f,
                this
            )
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onLocationChanged(location: Location) {
        this.userLocation = LatLng(location.latitude, location.longitude)
    }
}