package com.example.yourstory.today.thought

import android.annotation.SuppressLint
import android.content.Context
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

import com.google.android.gms.maps.model.CameraPosition

import android.location.Criteria
import android.location.Location

import androidx.core.content.ContextCompat.getSystemService

import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate





class RecordLocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: RecordLocationFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RecordLocationFragmentBinding.inflate(inflater, container, false)
        hostFragmentNavController = NavHostFragment.findNavController(this)
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        binding.confirmThoughtDialogLocation.setOnClickListener {
            viewModelShared.location.value = "10"
            hostFragmentNavController.navigate(R.id.action_recordLocationFragment_to_thought_dialog)
        }

        binding.cancelThoughtDialogLocation.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_recordLocationFragment_to_thought_dialog)
        }
        binding.recordLocationMapView.onCreate(savedInstanceState)
        binding.recordLocationMapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        //val mapFragment = getSupportFragmentManager().findFragmentById(R.id.record_location_map_view)
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
                val myLocation = LatLng(location!!.latitude, location!!.longitude)
                p0.addMarker(
                    MarkerOptions()
                        .position(myLocation)
                )
                val update = CameraUpdateFactory.newLatLngZoom(myLocation, 11f)
                p0.animateCamera(update)
                /*p0.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude,
                            location.longitude
                        ), 10f
                    )
                )
                val cameraPosition = CameraPosition.Builder()
                    .target(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    ) // Sets the center of the map to location user
                    .zoom(10f) // Sets the zoom
                    .bearing(90f) // Sets the orientation of the camera to east
                    .tilt(40f) // Sets the tilt of the camera to 30 degrees
                    .build() // Creates a CameraPosition from the builder
                p0.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))*/
            }

    }

        //p0.setMyLocationEnabled(true)
        /*val sydney = LatLng(-33.852, 151.211)
        p0.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )*/
        /*val pos_Marker: Marker = googleMap.addMarker(
            MarkerOptions().position(starting)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_laumcher))
                .title("Starting Location").draggable(false)
        )

        pos_Marker.showInfoWindow()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(START_locationpoint, 10f))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)*/

}