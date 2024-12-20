package com.example.project

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder


/**
 * `MapActivityPlaces` is an `AppCompatActivity` that provides a user interface for locating places
 * on a map using Google Maps and fetching place information using Google Places API.
 * It allows users to:
 * - Search for places by address or nearby places based on type and radius.
 * - Use a `SeekBar` to set the search radius.
 * - Use a `Spinner` to select place types.
 * - Use a `SearchView` to search for a location by address.
 *
 * The class integrates with Google Maps to display the user's current location, search for places,
 * and place markers on the map based on the user's input.
 */
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
//                    Toast.makeText(this@MapActivityPlaces, "Lokalizacja nie jest jeszcze dostępna", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)



    }
    @SuppressLint("MissingPermission")
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

    /**
     * Configures the map to show the user's current location.
     * If location permission is not granted, it requests the permission.
     * Once permission is granted and location is fetched, the map is centered on the user's location,
     * and a search is performed around that location.
     */
    @SuppressLint("MissingPermission")
    private fun setUpMap() {
        // Sprawdzenie, czy aplikacja ma uprawnienia do lokalizacji
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Prośba o przyznanie uprawnień
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }

        // Włączenie wyświetlania niebieskiej kropki
        mMap.isMyLocationEnabled = true

        // Pobranie ostatniej znanej lokalizacji użytkownika
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                // Zapisanie lokalizacji i przesunięcie kamery na aktualną lokalizację
                val currentLatLong = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15f))
                Log.d("MapActivityPlaces", "Lokalizacja ustawiona: $currentLatLong")
            } else {
                Log.e("MapActivityPlaces", "Nie udało się pobrać ostatniej lokalizacji")
                Toast.makeText(this, "Nie udało się pobrać lokalizacji", Toast.LENGTH_SHORT).show()
            }
        }
    }



    /**
     * Performs a search for places around a given location.
     * Results are filtered based on the selected place type and search radius.
     * Depending on the place type, it makes a network request to the Google Places API to fetch
     * nearby places and place markers on the map.
     *
     * @param location The central location around which to perform the search.
     */
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

    /**
     * Geocodes an address to get its latitude and longitude.
     * A network request is made to the Google Geocoding API to get the location details of the address.
     *
     * @param address The address to geocode.
     */
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

    /**
     * Clears all markers from the map.
     */
    private fun clearMap() {
        mMap.clear()
    }

    /**
     * Searches for nearby places using Google Places API with a specific keyword.
     *
     * @param locationString The central location for the search.
     * @param radius The search radius.
     * @param keyword The keyword to filter the search.
     * @param apiKey The API key for Google Places API.
     * @param markerColor The color of the marker to display on the map.
     */
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

    /**
     * Searches for nearby places using Google Places API.
     *
     * @param locationString The central location for the search.
     * @param radius The search radius.
     * @param type The type of place to search for.
     * @param apiKey The API key for Google Places API.
     * @param markerColor The color of the marker to display on the map.
     */
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
