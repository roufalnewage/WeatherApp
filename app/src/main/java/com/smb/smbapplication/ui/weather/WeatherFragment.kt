package com.smb.smbapplication.ui.weather

/**
 * Created by Shijil Kadambath on 03/08/2018
 * for NewAgeSMB
 * Email : shijil@newagesmb.com
 */
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.GravityCompat
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import com.smb.smbapplication.AppExecutors

import com.smb.smbapplication.R
import com.smb.smbapplication.common.autoCleared
import com.smb.smbapplication.data.api.*
import com.smb.smbapplication.ui.BaseFragment
import com.smb.smbapplication.ui.RetryCallback
import com.smb.smbapplication.ui.login.ListAdapter
import com.smb.smbapplication.ui.login.LoginFragmentDirections
import com.smb.smbapplication.ui.login.LoginViewModel
import org.json.JSONObject
import retrofit2.Response
import java.lang.Exception
import java.security.Permission
import javax.inject.Inject

private const val TAG: String = "LoginFragment"

/**
 * A simple [Fragment] subclass.
 *
 */
class WeatherFragment : BaseFragment<com.smb.smbapplication.databinding.FragmentWeatherBinding>(), NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onConnectionSuspended(p0: Int) {

    }


    @Inject
    lateinit var appExecutors: AppExecutors

    lateinit var mViewModel: WeatherViewModel

    lateinit var mNavigationView: NavigationView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var weatherResult: ApiResponse<JsonObject>
    override fun getLayoutId(): Int {
        return R.layout.fragment_weather;
    }


    lateinit var googleApiClient: GoogleApiClient
    var PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    lateinit var locationRequest: LocationRequest;
    var UPDATE_INTERVAL = 5000;
    var FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    lateinit var permissionsToRequest: ArrayList<String>

    var permissionsRejected = arrayListOf<String>()
    var permissions = arrayListOf<String>()
    // integer for permissions results request
    var ALL_PERMISSIONS_RESULT = 1011;


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel = getViewModel(WeatherViewModel::class.java)

        mNavigationView = mBinding.navigationView;

        mNavigationView.setNavigationItemSelectedListener(this@WeatherFragment)

        setPermission();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this!!.activity as Activity)
        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            //Log.v("RESULT", fusedLocationClient.lastLocation.result.toString())


        }

        mBinding.image = "https://cdn.freebiesupply.com/logos/large/2x/android-logo-png-transparent.png"

        mBinding.callback = object : RetryCallback {
            override fun retry() {
                mViewModel.retry()
            }
        }
        mViewModel.repositories.observe(this, Observer { result ->

            when (result) {
                is ApiSuccessResponse -> {
                    val rootObject = JSONObject((result as ApiSuccessResponse).body.toString())
                    val rootArray = rootObject.getJSONArray("data")
                    val subObject = JSONObject(rootArray[0].toString())
                    mBinding.txCity.setText(subObject.getString("city_name"))
                    mBinding.txTemp.setText(subObject.getString("temp") + "\u2103")
                }
                is ApiEmptyResponse -> {
                }
                is ApiErrorResponse -> {

                }
            }


        })

        mViewModel.loadData()
        mBinding.btnCurrent.setOnClickListener {
            mViewModel.loadData1()
        }
        mBinding.btnDayalert.setOnClickListener {
            mViewModel.loadAlert()
        }
        mViewModel.repositories1.observe(this, Observer { result ->

            when (result) {
                is ApiSuccessResponse -> {
                    try {
                            val rootObject = JSONObject((result as ApiSuccessResponse).body.toString())
                            val rootArray = rootObject.getJSONArray("data")
                            val subObject = JSONObject(rootArray[0].toString())
                            val weather = subObject.getJSONObject("weather")

                            val builder = AlertDialog.Builder(activity as Activity)
                            // Set the alert dialog title
                            builder.setTitle("Current Weather Condition")
                            // Display a message on alert dialog
                            builder.setMessage("Climate :" + weather.getString("description"))
                            // Set a positive button and its click listener on alert dialog
                            builder.setPositiveButton("Ok") { dialog, which ->
                                // Do something when user press the positive button
                            }
                            // Finally, make the alert dialog using builder
                            val dialog: AlertDialog = builder.create()
                            // Display the alert dialog on app interface
                            dialog.show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                is ApiEmptyResponse -> {
                }
                is ApiErrorResponse -> {

                }

            }


        })
        mViewModel.repositoriesalert.observe(this, Observer {
            result ->
            when (result) {
                is ApiSuccessResponse -> {

                    val rootObject = JSONObject((result as ApiSuccessResponse).body.toString())
                    val rootArray = rootObject.getJSONArray("alerts")
                    val subObject = JSONObject(rootArray[0].toString())
                    val builder = AlertDialog.Builder(activity as Activity)
                    // Set the alert dialog title
                    builder.setTitle("Alert")
                    // Display a message on alert dialog
                    builder.setMessage(subObject.getString("description"))
                    // Set a positive button and its click listener on alert dialog
                    builder.setPositiveButton("Ok") { dialog, which ->
                        // Do something when user press the positive button
                    }
                    // Finally, make the alert dialog using builder
                    val dialog: AlertDialog = builder.create()
                    // Display the alert dialog on app interface
                    dialog.show()

                }
                is ApiEmptyResponse -> {
                }
                is ApiErrorResponse -> {

                }
            }


        })
        mViewModel.repositoriesforecast.observe(this, Observer { result ->
            when (result) {
                is ApiSuccessResponse -> {
                        val rootObject = JSONObject((result as ApiSuccessResponse).body.toString())
                        val rootArray = rootObject.getJSONArray("data")
                        val subObject = JSONObject(rootArray[0].toString())
                        val builder = AlertDialog.Builder(activity as Activity)
                        // Set the alert dialog title
                        builder.setTitle("Forecast ")
                        // Display a message on alert dialog
                        builder.setMessage(
                                JSONObject(rootArray[0].toString()).getString("valid_date") + "   " + subObject.getString("app_max_temp") + "\u2103" + " \n"
                                        + JSONObject(rootArray[1].toString()).getString("valid_date") + "   " + subObject.getString("app_max_temp") + "\u2103" + " \n"
                                        + JSONObject(rootArray[2].toString()).getString("valid_date") + "   " + subObject.getString("app_max_temp") + "\u2103" + " \n"
                                        + JSONObject(rootArray[3].toString()).getString("valid_date") + "   " + subObject.getString("app_max_temp") + "\u2103" + " \n"
                                        + JSONObject(rootArray[4].toString()).getString("valid_date") + "   " + subObject.getString("app_max_temp") + "\u2103" + " \n"


                        )
                        // Set a positive button and its click listener on alert dialog
                        builder.setPositiveButton("Ok") { dialog, which ->
                            // Do something when user press the positive button
                        }
                        // Finally, make the alert dialog using builder
                        val dialog: AlertDialog = builder.create()
                        // Display the alert dialog on app interface
                        dialog.show()
                }
                is ApiEmptyResponse -> {
                }
                is ApiErrorResponse -> {

                }
            }


        })
        mBinding.btnDayWeather.setOnClickListener {
            mViewModel.loadForcast()
        }

    }

    private fun setPermission() {

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        arrayOf()), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = GoogleApiClient.Builder(activity as Activity).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    fun navController() = findNavController()

    fun permissionsToRequest(wantedPermissions: ArrayList<String>): ArrayList<String> {
        var result = arrayListOf<String>();

        for (perm in wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.navItemAbout -> {
                Toast.makeText(context, "Hi", Toast.LENGTH_LONG).show()
            }
            R.id.navItemRateUs -> {

            }
            R.id.navItemFeed -> {

            }
            R.id.navItemLogout -> {

            }

        }
        mBinding.drawerView.closeDrawer(GravityCompat.START)
        return true
    }

    fun hasPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(context as Context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    override
    fun onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }


    override fun onResume() {
        super.onResume();

        if (!checkPlayServices()) {

            //locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    override fun onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    fun checkPlayServices(): Boolean {
        var apiAvailability = GoogleApiAvailability.getInstance();
        var resultCode = apiAvailability.isGooglePlayServicesAvailable(activity as Activity);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity as Activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                activity!!.finish();
            }

            return false;
        }

        return true;
    }

    override fun onConnected(p0: Bundle?) {

        if (ActivityCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        var location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            Log.v("RESULT", "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude())
            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }

        startLocationUpdates();
    }

    fun startLocationUpdates() {
        locationRequest = LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL.toLong());
        locationRequest.setFastestInterval(FASTEST_INTERVAL.toLong());

        if (ActivityCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 7)


         //   Toast.makeText(context, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }else
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }


    override fun onLocationChanged(location: Location) {
        if (location != null) {
            // locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            ALL_PERMISSIONS_RESULT ->
                for (perm in permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

        }
    }
}
