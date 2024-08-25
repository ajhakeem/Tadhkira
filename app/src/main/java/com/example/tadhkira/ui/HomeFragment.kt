package com.example.tadhkira.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tadhkira.R
import com.example.tadhkira.data.adhan.PrayerName
import com.example.tadhkira.data.adhan.PrayerTime
import com.example.tadhkira.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment: Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private val latKey = "latitude"
    private val longKey = "longitude"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isRefreshing = false

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        if (permissionGranted) {
            checkForSavedLocation()
        } else {
            println("Permissions denial message here")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE)
        checkForSavedLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        bindViewElements()

        checkPermissionsAndGetData()

        lifecycleScope.launch {
            homeViewModel.uiState.collectLatest { state ->
                updateUiWithState(uiState = state)
            }
        }

        return binding.root
    }

    private fun bindViewElements() {
        binding.prayerTimesTable.refreshBtn.setOnClickListener {
            if (isRefreshing.not()) {
                isRefreshing = !isRefreshing

                checkPermissionsAndGetData()
            }

        }
    }

    private fun checkPermissionsAndGetData() {
        if (hasLocationPermissions()) {
            checkForSavedLocation()
        } else {
            locationPermissionRequest.launch(ACCESS_COARSE_LOCATION)
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun checkForSavedLocation() {
        val savedLatitude = sharedPreferences.getFloat(latKey, 0f)
        val savedLongitude = sharedPreferences.getFloat(longKey, 0f)

        if (savedLatitude == 0f && savedLongitude == 0f) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val latitudeFloat = location.latitude.toFloat()
                val longitudeFloat = location.longitude.toFloat()
                saveLastLocation(latitude = latitudeFloat, longitude = longitudeFloat)
                getPrayerTimesByLocation(latitude = latitudeFloat, longitude = longitudeFloat)
            }
        } else {
            getPrayerTimesByLocation(latitude = savedLatitude, longitude = savedLongitude)
        }
    }

    private fun saveLastLocation(latitude: Float, longitude: Float) {
        with(sharedPreferences.edit()) {
            putFloat(latKey, latitude)
            putFloat(longKey, longitude)
            apply()
        }
    }

    private fun getPrayerTimesByLocation(latitude: Float, longitude: Float) {
        lifecycleScope.launch {
            homeViewModel.getTimingsByCity(latitude = latitude, longitude = longitude)
        }
    }

    private fun updateUiWithState(uiState: HomeUiState) {
        isRefreshing = false

        when (uiState) {
            is HomeUiState.SuccessState -> {
                binding.apply {
                    prayerTimesTable.refreshBtn.clearAnimation()

                    startsInTv.text = uiState.startsInLabel
                    hoursMinTv.text = uiState.hourMinLabel
                    prayerTimesTable.fajrTimeTv.text = uiState.prayerTimes?.fajrTime
                    prayerTimesTable.dhuhrTimeTv.text = uiState.prayerTimes?.dhuhrTime
                    prayerTimesTable.asrTimeTv.text = uiState.prayerTimes?.asrTime
                    prayerTimesTable.maghribTimeTv.text = uiState.prayerTimes?.maghribTime
                    prayerTimesTable.ishaTimeTv.text = uiState.prayerTimes?.ishaTime
                    dayOfWeekTv.text = uiState.dayLabel
                    dateTv.text = uiState.dateLabel
                    atTimeTv.text = uiState.atTimeLabel
                }

                uiState.nextPrayerTime?.let {nextPrayerTime ->
                    setBannerResources(nextPrayerTime = nextPrayerTime)
                }
            }
            is HomeUiState.LoadingState -> {
                animateRefreshButton()
            }
        }
    }

    private fun animateRefreshButton() {
        binding.prayerTimesTable.refreshBtn.animate().rotation(360f).setDuration(1500).start()
    }

    private fun setBannerResources(nextPrayerTime: PrayerTime) {
            when (nextPrayerTime.prayerName) {
                PrayerName.Dhuhr -> {
                    binding.apply {
                        bannerIv.setBackgroundResource(R.drawable.banner_morning)
                        bannerMosqueIv.setImageResource(R.drawable.mosque_morning)
                    }
                }
                PrayerName.Asr, PrayerName.Maghrib -> {
                    binding.apply {
                        bannerIv.setBackgroundResource(R.drawable.banner_day)
                        bannerMosqueIv.setImageResource(R.drawable.mosque_day)
                    }
                }
                PrayerName.Isha -> {
                    binding.apply {
                        bannerIv.setBackgroundResource(R.drawable.banner_evening)
                        bannerMosqueIv.setImageResource(R.drawable.mosque_evening)
                    }
                }
                PrayerName.Fajr -> {
                    binding.apply {
                        bannerIv.setBackgroundResource(R.drawable.banner_night)
                        bannerMosqueIv.setImageResource(R.drawable.mosque_night)
                    }
                }
        }
    }

}