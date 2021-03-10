package com.example.hw1

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.RandomAccess
import kotlin.random.Random

const val LOCATION_REQUEST_CODE = 123
const val GEOFENCE_LOCATION_REQUEST_CODE = 12345
const val CAMERA_ZOOM_LEVEL = 13f
const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
const val GEOFENCE_EXPIRATION = 10 * 24 * 60 * 60 * 1000 // 10 DAYS
const val GEOFENCE_DWELL_DELAY = 10 * 1000 // 10 SECS
const val GEOFENCE_RADIUS = 300

private val TAG = MapsActivity::class.java.simpleName

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient

    var remindertime : String = ""
    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        if (!isLocationPermissionGranted()) {
            val permissions = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                LOCATION_REQUEST_CODE
            )
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap.isMyLocationEnabled = true
            // Get last known location data
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    with(mMap) {
                        val latlng = LatLng(it.latitude, it.longitude)
                        moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, CAMERA_ZOOM_LEVEL))
                    }
                } else {
                    with(mMap) {
                        moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(61.49911, 23.78712),
                                CAMERA_ZOOM_LEVEL
                            )
                        )
                    }
                }
            }
        }

        val tampere = LatLng(61.49911, 23.78712)

        mMap.addMarker(MarkerOptions().position(tampere).title("Marker in Tampere"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tampere, 13f))

        setLongClick(mMap)
        setPoiClick(mMap)
    }

    private fun setLongClick(googleMap: GoogleMap) {
        googleMap.setOnMapLongClickListener { latlng ->
            mMap.addMarker(
                MarkerOptions().position(latlng).title("Current position")
            )

            googleMap.addMarker(
                MarkerOptions().position(latlng)
                    .title("I was here")
                    .snippet(latlng.toString())
            )

            mMap.addCircle(
                CircleOptions()
                    .center(latlng)
                    .strokeColor(Color.argb(50, 70, 70, 70))
                    .fillColor(Color.argb(70, 150, 150, 150))
                    .radius(GEOFENCE_RADIUS.toDouble())
            )

            //insert latlng to database
            val db = Firebase.database(getString(R.string.firebase_db_url))
            val reference = db.getReference("reminders")
            var key = reference.push().key
            //reference.push().child("Location").setValue(it)
            if (key != null) {
                val reminder = Reminder(
                    0,
                    "MAPPIREMINDERI",
                    "aika",
                    "aika",
                    latlng.latitude.toString(),
                    latlng.longitude.toString(),
                    "dunno"
                )

                key = reminder.creator_id.toString()

                reference.child(key).setValue(reminder)

                val sdf = SimpleDateFormat("d/M/yyyy/H/m")
                val currentDate = sdf.format(Date())
                val time = currentDate.toString()
                createMapReminderDialog(
                        latlng.latitude.toString(),
                        latlng.longitude.toString(), latlng)
                /*
                val status = db.insertData(
                    Reminder(
                        0,
                        "MAPPIREMINDERI",
                        time,
                        "",
                        latlng.latitude.toString(),
                        latlng.longitude.toString(),
                        "placeholder",
                    )
                )
                */
            }


            //startActivity(Intent(applicationContext, AddReminder::class.java))

        }
    }

    private fun createGeofence(location: LatLng, key: String, geofencingClient: GeofencingClient, message: String) {
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS.toFloat())
            .setExpirationDuration(GEOFENCE_EXPIRATION.toLong())
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL
            ).setLoiteringDelay(GEOFENCE_DWELL_DELAY)
            .build()
        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()


        val intent = Intent(this, GeofenceReceiver::class.java)
            .putExtra("key", key)
            .putExtra("message", message)
            .putExtra("calendar", remindertime)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    GEOFENCE_LOCATION_REQUEST_CODE
                )
            } else {
                geofencingClient.addGeofences(geofenceRequest, pendingIntent)
            }
        } else {
            geofencingClient.addGeofences(geofenceRequest, pendingIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == GEOFENCE_LOCATION_REQUEST_CODE) {
            if (permissions.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "This app needs background location to be enabled",
                    Toast.LENGTH_LONG
                ).show()
                // 56:00 harkassa puhutaan mitä pitää tehdä
                // Request permissions again here
            }
        }

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && (
                        grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                                grantResults[1] == PackageManager.PERMISSION_GRANTED
                        )
            ) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mMap.isMyLocationEnabled = true
                onMapReady(mMap)
            } else {
                Toast.makeText(
                    this,
                    "This app needs background location to be enabled",
                    Toast.LENGTH_LONG
                ).show()
                // 56:00 harkassa puhutaan mitä pitää tehdä
                // Request permissions again here
            }
        }
    }

    private fun setPoiClick(googleMap: GoogleMap) {
        googleMap.setOnPoiClickListener { poi ->
            val poiMarker = googleMap.addMarker(
                MarkerOptions().position(poi.latLng)
                    .title(poi.name)
            )
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun createMapReminderDialog(latitude: String, longitude: String, latlng: LatLng) {

        val creationDialog = Dialog(this, R.style.Dialogi)
        creationDialog.setCancelable(false)
        creationDialog.setContentView(R.layout.dialog_mapreminder)
        creationDialog.show()
        creationDialog.findViewById<Button>(R.id.timePickerBtn).setOnClickListener{
            getDateTimeCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }
        creationDialog.findViewById<Button>(R.id.donebutton).setOnClickListener(){

            if(creationDialog.findViewById<EditText>(R.id.rmdrDesc).text.isNotEmpty()){
                val message = creationDialog.findViewById<EditText>(R.id.rmdrDesc).text.toString()
                val sdf = SimpleDateFormat("d/M/yyyy/H/m")
                val currentDate = sdf.format(Date())
                val time = currentDate.toString()
                creationDialog.findViewById<Button>(R.id.timePickerBtn).setOnClickListener{
                    getDateTimeCalendar()
                    DatePickerDialog(this, this, year, month, day).show()
                }
                val db: DataBaseHandler = DataBaseHandler(this)
                val status = db.insertData(Reminder(0, message, time, remindertime, latitude, longitude, "placeholder"))
                val reminder = Reminder(0, message, time, remindertime, latitude, longitude, "placeholder")


                if (status > -1){
                    creationDialog.findViewById<EditText>(R.id.rmdrDesc).text.clear()
                    createGeofence(latlng, reminder.creator_id.toString(), geofencingClient, message)
                    creationDialog.dismiss()
                    //startActivity(Intent(applicationContext, MenuActivity::class.java))
                }else{
                    Toast.makeText(this, "tooooobaad", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please fill the data correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getDateTimeCalendar(){

        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int){
        savedDay = day
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()
        TimePickerDialog(this, this, hour, minute, true).show()
    }
    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int){
        savedHour = hour
        savedMinute = minute
        cal.set(savedYear, savedMonth, savedDay, savedHour, savedMinute)
        savedMonth = savedMonth + 1
        remindertime = "$savedDay/$savedMonth/$savedYear/$savedHour/$savedMinute"
        /*
        findViewById<TextView>(R.id.testinaytto).text =  "$savedDay/$savedMonth/$savedYear\n$savedHour : $savedMinute"

         */

    }

}