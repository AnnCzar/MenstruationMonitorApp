package com.example.project

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.project.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()

        val visitAddress = intent.getStringExtra("VISIT_ADDRESS")
        if (!visitAddress.isNullOrEmpty()) {
            geocodeAddress(visitAddress)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    findNearbyPharmacies(currentLatLong)
                }
            }
        }
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        Log.d("MapsActivity", "Kliknięto marker: ${marker.title}")
        return false
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location

                val currentLatLong = LatLng(location.latitude, location.longitude)

//                placeMarkerOnMap(currentLatLong)

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 11f))

//                findNearbyPharmacies(currentLatLong)
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap.addMarker(markerOptions)
    }

private fun findNearbyPharmacies(location: LatLng) {
    val apiKey = BuildConfig.MAPS_API_KEY
    val locationString = "${location.latitude},${location.longitude}"
    val radius = 5000

    searchNearbyPlaces(locationString, radius, "pharmacy", apiKey, BitmapDescriptorFactory.HUE_RED)

    searchNearbyPlacesWithKeyword(locationString, radius, "Rossmann", apiKey, BitmapDescriptorFactory.HUE_BLUE, true)
    searchNearbyPlacesWithKeyword(locationString, radius, "Hebe", apiKey, BitmapDescriptorFactory.HUE_BLUE, false)
}


    private fun searchNearbyPlacesWithKeyword(
        locationString: String,
        radius: Int,
        keyword: String,
        apiKey: String,
        markerColor: Float,
        ignore: Boolean
    ) {
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$radius&keyword=$keyword&key=$apiKey"

        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val results = jsonObject.getJSONArray("results")

                    for (i in 0 until results.length()) {
                        val place = results.getJSONObject(i)
                        val placeName = place.getString("name")

                        if (placeName.contains(keyword, ignoreCase = ignore)) {
                            val latLng = place.getJSONObject("geometry").getJSONObject("location")
                            val lat = latLng.getDouble("lat")
                            val lng = latLng.getDouble("lng")

                            val markerOptions = MarkerOptions()
                                .position(LatLng(lat, lng))
                                .title(placeName)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor))

                            mMap.addMarker(markerOptions)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("MapsActivity", "Błąd podczas wyszukiwania: ${error.message}")
            }) {}

        Volley.newRequestQueue(this).add(request)
    }



    private fun searchNearbyPlaces(locationString: String, radius: Int, type: String, apiKey: String, markerColor: Float) {
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$radius&type=$type&key=$apiKey"

        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val results = jsonObject.getJSONArray("results")

                    for (i in 0 until results.length()) {
                        val place = results.getJSONObject(i)
                        val latLng = place.getJSONObject("geometry").getJSONObject("location")
                        val lat = latLng.getDouble("lat")
                        val lng = latLng.getDouble("lng")
                        val placeName = place.getString("name")

                        val markerOptions = MarkerOptions().position(LatLng(lat, lng)).title(placeName)

                        mMap.addMarker(markerOptions)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("MapsActivity", "Błąd podczas wyszukiwania: ${error.message}")
            }) {}

        Volley.newRequestQueue(this).add(request)
    }

    private fun geocodeAddress(address: String) {
        val apiKey = BuildConfig.MAPS_API_KEY
        val encodedAddress = URLEncoder.encode(address, "UTF-8")
        val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$encodedAddress&key=$apiKey"

        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    Log.d("Geocode Response", response)  
                    val jsonObject = JSONObject(response)
                    val results = jsonObject.getJSONArray("results")

                    if (results.length() > 0) {
                        val location = results.getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")

                        val lat = location.getDouble("lat")
                        val lng = location.getDouble("lng")
                        val latLng = LatLng(lat, lng)

                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title("Wizyta: $address")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        )

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    } else {
                        Toast.makeText(this, "Nie znaleziono lokalizacji dla adresu", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Błąd w przetwarzaniu danych adresu", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("MapsActivity", "Błąd podczas geokodowania: ${error.message}")
                Toast.makeText(this, "Błąd połączenia z Geocoding API", Toast.LENGTH_SHORT).show()
            }
        ) {}

        Volley.newRequestQueue(this).add(request)
    }

}
