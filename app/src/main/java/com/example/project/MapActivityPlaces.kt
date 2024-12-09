package com.example.project

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.project.BuildConfig
import com.example.project.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder

class MapActivityPlaces : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private var lastLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var typePlaceChoice: Spinner
    private lateinit var radiusChoice: SeekBar
    private lateinit var radiusText: TextView
    private lateinit var searchByAddress: SearchView

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_places)

        typePlaceChoice = findViewById(R.id.typePlaceChoice)
        radiusChoice = findViewById(R.id.radiusChoice)
        radiusText = findViewById(R.id.radiusText)
        searchByAddress = findViewById(R.id.searchByAddress)

        radiusChoice.max = 10000
        radiusChoice.progress = 5000
        radiusText.text = "Promień: ${radiusChoice.progress} m"

        searchByAddress.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    clearMap()
                    geocodeAddress(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                     lastLocation?.let {
                        val currentLatLong = LatLng(it.latitude, it.longitude)
                        performSearch(currentLatLong)
                    }
                }
                return false
            }
        })



        radiusChoice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                radiusText.text = "Promień: $progress m"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                lastLocation?.let {
                    val currentLatLong = LatLng(it.latitude, it.longitude)
                    performSearch(currentLatLong)
                } ?: run {
                    Toast.makeText(this@MapActivityPlaces, "Lokalizacja nie jest jeszcze dostępna", Toast.LENGTH_SHORT).show()
                }
            }

        })


        val typePlaceChoiceAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.place_types,
            R.layout.spinner_item
        )
        typePlaceChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typePlaceChoice.adapter = typePlaceChoiceAdapter

        typePlaceChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                lastLocation?.let {
                    val currentLatLong = LatLng(it.latitude, it.longitude)
                    performSearch(currentLatLong)
                } ?: run {
                    Toast.makeText(this@MapActivityPlaces, "Lokalizacja nie jest jeszcze dostępna", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val mapView: MapView = findViewById(R.id.mapView2)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.d("MapActivityPlaces", "Kliknięto marker: ${marker.title}")
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 11f))
                performSearch(currentLatLong)
            } else {
                Toast.makeText(this, "Nie udało się pobrać lokalizacji", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performSearch(location: LatLng) {
        clearMap()
        val apiKey = BuildConfig.MAPS_API_KEY
        val locationString = "${location.latitude},${location.longitude}"
        val radius = radiusChoice.progress
        val selectedType = typePlaceChoice.selectedItem.toString()

        when (selectedType) {
            "Apteki" -> searchNearbyPlaces(locationString, radius, "pharmacy", apiKey, BitmapDescriptorFactory.HUE_RED)
            "Drogerie" -> {
                searchNearbyPlacesWithKeyword(locationString, radius, "Rossmann", apiKey, BitmapDescriptorFactory.HUE_BLUE)
                searchNearbyPlacesWithKeyword(locationString, radius, "Hebe", apiKey, BitmapDescriptorFactory.HUE_BLUE)
            }
        }
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

    private fun clearMap() {
        mMap.clear()
    }

    private fun searchNearbyPlacesWithKeyword(
        locationString: String,
        radius: Int,
        keyword: String,
        apiKey: String,
        markerColor: Float
    ) {
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$radius&keyword=$keyword&key=$apiKey"

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

                        val markerOptions = MarkerOptions()
                            .position(LatLng(lat, lng))
                            .title(placeName)
                            .icon(BitmapDescriptorFactory.defaultMarker(markerColor))

                        mMap.addMarker(markerOptions)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("MapActivityPlaces", "Błąd podczas wyszukiwania: ${error.message}")
            }) {}

        Volley.newRequestQueue(this).add(request)
    }

    private fun searchNearbyPlaces(locationString: String, radius: Int, type: String, apiKey: String, markerColor: Float) {
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$radius&type=$type&key=$apiKey"

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

                        val markerOptions = MarkerOptions()
                            .position(LatLng(lat, lng))
                            .title(placeName)
                            .icon(BitmapDescriptorFactory.defaultMarker(markerColor))

                        mMap.addMarker(markerOptions)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("MapActivityPlaces", "Błąd podczas wyszukiwania miejsc: ${error.message}")
            }) {}

        Volley.newRequestQueue(this).add(request)
    }
}
