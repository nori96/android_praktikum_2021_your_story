package com.example.yourstory.today.thought

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.Exception


class RecordLocationFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var mapView: GoogleMap? = null
    private var _binding: RecordLocationFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var userLocation: LatLng
    private var loadingDialogBuilder: MaterialAlertDialogBuilder? = null
    private var loadingDialog: androidx.appcompat.app.AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RecordLocationFragmentBinding.inflate(inflater, container, false)
        hostFragmentNavController = NavHostFragment.findNavController(this)
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        if (viewModelShared.loading.value!!) {
            loadingDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            loadingDialogBuilder!!.setView(R.layout.loading_dialog_add_location)
            loadingDialogBuilder!!.setCancelable(false)
            loadingDialog = loadingDialogBuilder!!.show()
        }

        viewModelShared.loading.observe(viewLifecycleOwner, {
            if (!viewModelShared.loading.value!!) {
                if (loadingDialogBuilder != null && loadingDialog != null) {
                    loadingDialogBuilder!!.setCancelable(true)
                    loadingDialog!!.dismiss()
                }
            }
        })

        binding.confirmThoughtDialogLocation.setOnClickListener {
            viewModelShared.location.value = userLocation
            hostFragmentNavController.navigate(R.id.action_recordLocationFragment_to_thought_dialog)
        }

        binding.cancelThoughtDialogLocation.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_recordLocationFragment_to_thought_dialog)
        }

        viewModelShared.tmpLocation.observe(viewLifecycleOwner,{
            userLocation = it
            if(this.mapView == null){
                return@observe
            }
            viewModelShared.loading.value = false
            viewModelShared.loading.value = true
            mapView?.addMarker(
                MarkerOptions()
                    .position(it)
            )
            val update = CameraUpdateFactory.newLatLngZoom(it, 11f)
            mapView!!.uiSettings.isMapToolbarEnabled = false
            mapView!!.animateCamera(update)
        })

        getLocation()
        binding.recordLocationMapView.onCreate(savedInstanceState)
        binding.recordLocationMapView.getMapAsync(this)
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
        this.mapView = p0
    }

    @SuppressLint("MissingPermission")
    fun getLocation(){
        try {
            val locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!isGPSEnabled) {
                return
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000*60L,
                10f,
                this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return
    }

    override fun onLocationChanged(location: Location) {
        viewModelShared.tmpLocation.postValue(LatLng(location.latitude, location.longitude))
    }
}