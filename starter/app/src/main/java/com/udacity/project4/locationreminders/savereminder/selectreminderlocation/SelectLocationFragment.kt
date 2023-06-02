package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.locationreminders.savereminder.TAG
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(),OnMapReadyCallback {

    //  to check if device is running Q or later
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private var selctedPoi: PointOfInterest? = null
    private lateinit var map: GoogleMap

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        TODO: add the map setup implementation
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        TODO: call this function after the user confirms on the selected location
        binding.saveBtn.setOnClickListener {
            onLocationSelected(it)
        }



        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap



        //        TODO: put a marker to location that the user selected
        onMapLongClick(map)
        //TODO: zoom to the user location after taking his permission
        enableMyLocation()
        moveCameraToDeviceLocation(map)
//        TODO: add style to the map
        setMapStyle(map)
// poi
        setPoiClick(map)
    }


    private fun onLocationSelected(view: View) {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        ////////////////////  // for real app
        if (selctedPoi != null) {
            _viewModel.selctedPoiSave(selctedPoi!!)
            view.findNavController().popBackStack()
        } else
            Toast.makeText(
                context, "No POI selected please select ", Toast.LENGTH_SHORT
            ).show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.

        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setMapStyle(map: GoogleMap) { // first create Json obj style"https://mapstyle.withgoogle.com/" and put it map_style.json in raw
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }


    }
    /**
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    private fun moveCameraToDeviceLocation(map: GoogleMap) {

        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { it ->
                if (it.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = it.result
                    if (lastKnownLocation != null) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude), 12F))
                    }
                } else {
                    Log.i(TAG, "Current location is null. Using defaults.")
                    Log.i(TAG, "Exception: ${it.exception}")
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(30.236895, 31.370806), 15.toFloat()))
                }
            }
        } catch (e: SecurityException) {
            Log.i(TAG, "Exception: ${e.message}")
        }
    }

    fun onMapLongClick(map: GoogleMap)// to add pins when long clicks // invoked in ui thread
    {

        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title. // when click
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            val marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            ) //change pin color //optional step
            //marker?.showInfoWindow() //to show title without click pin
            val newLocation=PointOfInterest(latLng,marker.id.toString()," selected location ")
            selctedPoi=newLocation
        }

    }

    private fun setPoiClick(map: GoogleMap) {
        selctedPoi = null
        map.setOnPoiClickListener { poi ->
            selctedPoi = poi
            val poiMarker = map.addMarker(
                MarkerOptions().position(poi.latLng).title(poi.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )//change pin color //optional step
            poiMarker?.showInfoWindow() //to show title without click pin
        }
    }


    override fun onResume() {
        super.onResume()

        Log.i(TAG, "onstart selectfr")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onstart selctfr")
    }


    private fun enableMyLocation() { //enable your current location
        if (foregroundLocationPermissionApproved())
            map.setMyLocationEnabled(true)
        else
            requestForegroundLocationPermissions()
        //set zoom and move map camera position to device's current location
        //moveCameraToDeviceLocation(map)
        map?.moveCamera(CameraUpdateFactory.zoomIn())
    }
    //check if the user has granted the permission.

////////////////////////location handling
///////////////// location permission
    /*
 *  Uses the Location Client to check the current state of location settings, and gives the user
 *  the opportunity to turn on location services within our app.
     */

    //    //1 Determines whether the app has the permissions across Android 10+ and all other or no permission exist
    @TargetApi(29) // for android 10 to above
    fun foregroundLocationPermissionApproved(): Boolean {

        val foregroundLocationApproved =
            (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        return foregroundLocationApproved
    }

    //2 Requests ACCESS_FINE_LOCATION
    @TargetApi(29) // for android 10 to above
    private fun requestForegroundLocationPermissions() {
        //check if permission granted
        if (foregroundLocationPermissionApproved())
            return
        var permissionArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)


        val resultCode = REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE // foreground permission only needed here

        //request foreground and background permissions
        requestPermissions(
            permissionArray,
            resultCode
        )//for fragment //on so onRequestPermissionsResult will run in fragment
        //ActivityCompat.requestPermissions(requireActivity(),permissionArray,resultCode)// for activity //on so onRequestPermissionsResult will run in Activity


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // TODO: Step 5 add code to handle the result of the user's permission
        Log.d(TAG, "onRequestPermissionResult")
        if (grantResults.isEmpty()|| // check if permission denied or empty after user granted
            grantResults[LOCATION_PERMISSION_INDEX]==PackageManager.PERMISSION_DENIED ||
            (requestCode== REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE && grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX]==PackageManager.PERMISSION_DENIED))
        {
            Snackbar.make(binding.root,
                R.string.permission_denied_explanation,Snackbar.LENGTH_INDEFINITE).setAction(
                R.string.settings)// this to show setting action below screen to add permission manually
            {
                startActivity(Intent().apply {
                    action=Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data=Uri.fromParts("package",BuildConfig.APPLICATION_ID,null)
                    flags=Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
        }else
            enableMyLocation()

    }





}


private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
public const val TAG = "xdx"
private const val LOCATION_PERMISSION_INDEX = 0
private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1



