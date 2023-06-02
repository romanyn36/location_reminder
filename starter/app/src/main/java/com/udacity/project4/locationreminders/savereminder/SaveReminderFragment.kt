package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private var reminder: ReminderDataItem? =null
    //  to check if device is running Q or later
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q

    /////////geofencing
    private lateinit var geofencingClient: GeofencingClient //1
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)

        intent.action = SaveReminderFragment.ACTION_GEOFENCE_EVENT
        //FLAG_UPDATE_CURRENT to use same pending intent when return again
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

////////////
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel
//        ///
//            val poiBundle=SaveReminderFragmentArgs.fromBundle(requireArguments()) // method 1
//             val poi =poiBundle.selctedPoi // method 1
//        or
//        val args: SaveReminderFragmentArgs by navArgs() // for safe args
//        val poi=args.selctedPoi
//        ////////
//        _viewModel.selctedPoiSave(poi)// initialize poi in viewModel

        //instantiate the geofencing client
        //A GeofencingClient is the main entry point for interacting with the geofencing APIs.
        geofencingClient = LocationServices.getGeofencingClient(requireActivity()) //2

        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value
             reminder = ReminderDataItem(title,description ,location,latitude,longitude)
//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db
                if (_viewModel.validateEnteredData(reminder!!) ) {
                    checkPermissionsForGeofencing()
                }
        }
    }


    ////////////////////////////////////////////////////////////////////////geofencing

    private fun saveReminderAndAddGeofence() {
        if(reminder!=null)
        {
            //Build the geofence using the geofence builder
            val geofence = geofence()
            //Build the geofence request. Set the initial trigger to INITIAL_TRIGGER_ENTER,
            // add the geofence you just built and then build.
            val geofencingRequest = geofencingRequest(geofence)
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
                .addOnSuccessListener {
                    _viewModel.validateAndSaveReminder(reminder!!)
                    Log.d(AuthenticationActivity.TAG, "Geofence Added")
                }
                .addOnFailureListener {
                    if ((it.message != null)) {
                        ("Failed to add geofenc")
                       // checkPermissionsForGeofencing()
                    }
                }

        }
    }
    private fun geofence(): Geofence? {
        val geofence = Geofence.Builder()
            .setRequestId(reminder?.id)
            .setCircularRegion(
                reminder?.latitude!!,
                reminder?.longitude!!,
                GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(NEVER_EXPIRE) //GEOFENCE_EXPIRATION_IN_MILLISECONDS// not expire until it reached
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()
        return if ( reminder?.latitude!=null && reminder?.longitude!=null) geofence else null

    }

    private fun geofencingRequest(geofence: Geofence?): GeofencingRequest? {
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
        return geofencingRequest
    }


////////////////////////location handling
///////////////// location permission
    /*
 *  Uses the Location Client to check the current state of location settings, and gives the user
 *  the opportunity to turn on location services within our app.
     */

    //1 Determines whether the app has the permissions across Android 10+ and all other or no permission exist
    @TargetApi(29) // for android 10 to above
     fun foregroundAndBackgroundLocationPermissionApproved():Boolean
    {
        Log.i(TAG,"location  permission check")
        val foregroundLocationApproved=(PackageManager.PERMISSION_GRANTED== ActivityCompat.checkSelfPermission(context!!,
            Manifest.permission.ACCESS_FINE_LOCATION))
        val backgroundLocationApproved=if(runningQOrLater)
            (PackageManager.PERMISSION_GRANTED==
                    ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION))// true if accepted //false if no
        else
            true //  needn't background location in lower android version
        return foregroundLocationApproved&&backgroundLocationApproved
    }

    //2 Requests ACCESS_FINE_LOCATION and (on Android 10+ (Q) ACCESS_BACKGROUND_LOCATION from user if not exist
    @RequiresApi(Build.VERSION_CODES.Q)//@TargetApi(29) // for android 10 to above
    private fun requestForegroundAndBackgroundLocationPermissions()
    {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return
        var permissionArray= arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode=when
        {
            //The code will be different depending on if the device is running Q or later /
            // / will inform us if you need to check for one permission (fine location) or
            // multiple permissions (fine and background location) when the user returns from the permission request screen.
            runningQOrLater->
            {
                permissionArray+= Manifest.permission.ACCESS_BACKGROUND_LOCATION ////add background location permission if higher sdk

                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE

            }
            else->REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE

        }
        //request foreground and background permissions
       requestPermissions(permissionArray,resultCode)//for fragment //on so onRequestPermissionsResult will run in fragment
        //ActivityCompat.requestPermissions(requireActivity(),permissionArray,resultCode)// for activity //on so onRequestPermissionsResult will run in Activity

    }


    /*  Uses the Location Client to check the current state of location settings, and gives the user
    *  the opportunity to turn on location services within our app.
    */
    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true) {
        // TODO: Step 6 add code to check that the device's location is on // after permission accepted
        val locationRequest= LocationRequest.create().apply {
            priority= LocationRequest.PRIORITY_LOW_POWER
        }
        val builder= LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient=LocationServices.getSettingsClient(requireActivity())  ///to get the Settings Client
        val locationSettingsResponseTask =settingsClient.checkLocationSettings(builder.build()) //check if location enabled
        locationSettingsResponseTask.addOnFailureListener{
                exception ->
            Log.i(TAG,"location faield")
            if(exception is ResolvableApiException && resolve)
            {
                try { ///Check if the exception is of type ResolvableApiException method in order to prompt the user to turn on device location
                   // exception.startResolutionForResult(requireActivity(), REQUEST_TURN_DEVICE_LOCATION_ON) // 1 in activity


                    startIntentSenderForResult(exception.resolution.intentSender, REQUEST_TURN_DEVICE_LOCATION_ON, null,// 2 in fragment
                        0,
                        0,
                        0,
                        null)
                    Log.i(TAG,"location field ,,try,")
                }catch (sendEx: IntentSender.SendIntentException)
                {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)

                }
            }else // If the exception is not of type ResolvableApiException and you refused to ebable location, present a snackbar that alerts the user that location needs to be enabled to use app

            {
                Log.d(TAG, "else sss")

                Snackbar.make(binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok)
                    {
                        checkDeviceLocationSettingsAndStartGeofence() // ask user again
                        Log.i(TAG,"location setting")
                    }.show()
            }
        }
        //If the locationSettingsResponseTask does complete, check that it is successful, if so you will want to add the geofence.
        locationSettingsResponseTask.addOnCompleteListener {
            if(it.isSuccessful)
            saveReminderAndAddGeofence()
            Log.i(TAG,"location succes")
        }
    }
    /**
     * Starts the permission check and Geofence process only if the Geofence associated with the
     * current hint isn't yet active.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionsForGeofencing() {
        if (foregroundAndBackgroundLocationPermissionApproved()) { //check location permission
            checkDeviceLocationSettingsAndStartGeofence()//check location setting enabled
        } else {
            requestForegroundAndBackgroundLocationPermissions()// ask permission if not exist
        }
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
            checkDeviceLocationSettingsAndStartGeofence()
    }

    /////////////////////////////////////////////////////////////////////////////////

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()

    }
    companion object {
        internal const val ACTION_GEOFENCE_EVENT =
            "EVENT.ACTION_GEOFENCE"
    }

    /*
    *  When we get the result from asking the user to turn on device location, we call
    *  checkDeviceLocationSettingsAndStartGeofence again to make sure it's actually on, but
    *  we don't resolve the check to keep the user from seeing an endless loop.
    */
    // After the user chooses whether to accept or deny device location permissions, this checks if the user has chosen to accept the permissions. If not, it will ask again.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
super.onActivityResult(requestCode, resultCode, data)
        // check that the user turned on their device location and ask
        //again if they did not
        if(requestCode== REQUEST_TURN_DEVICE_LOCATION_ON){
            checkDeviceLocationSettingsAndStartGeofence(false)
            Log.i(TAG,"onActivity res inside if")
        }
        Log.i(TAG,"onActivity res")
    }
}
//for geofencing
const val GEOFENCE_RADIUS_IN_METERS = 1000F
private const val  NEVER_EXPIRE = -1L
val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)
// for location
private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
public const val TAG = "xdx"
private const val LOCATION_PERMISSION_INDEX = 0
private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
