package com.example.hw1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class GeofenceReceiver: BroadcastReceiver() {

    lateinit var key: String
    lateinit var message: String
    lateinit var calendar: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        var continuer: Int = 0

        Log.i("AITATAG", "reminder != null!!")
        if (context != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            val geofencingTransition = geofencingEvent.geofenceTransition
            if (intent != null) {
                calendar = intent.getStringExtra("calendar").toString()
                Log.i("AITATAG", "calendar: $calendar")
                val reminderparts = calendar.split("/").toTypedArray()
                val reminderDay = reminderparts[0]
                val reminderMonth = reminderparts[1]
                val reminderYear = reminderparts[2]
                val reminderDate = SimpleDateFormat("dd-MM-yyyy").parse("$reminderDay-$reminderMonth-$reminderYear")
                Log.i("AITATAG", "noni: $reminderDate")

                var current = LocalDateTime.now()
                val currentFormatted = current.format(DateTimeFormatter.ISO_DATE)
                val dateparts = currentFormatted.split("-").toTypedArray()
                val thisYear = dateparts[0].toInt()
                val thisMonth = dateparts[1].toInt()
                val thisDay = dateparts[2].toInt()
                val newDate = SimpleDateFormat("dd-MM-yyyy").parse("$thisDay-$thisMonth-$thisYear")
                Log.i("AITATAG", "system: $newDate")

                if (reminderDate < newDate){
                    continuer = 1
                }
            }
            if (continuer==1){
                if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                        geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                    if (intent != null) {
                        key = intent.getStringExtra("key")!!
                        message = intent.getStringExtra("message")!!
                    }
                    //Retrieve from firebase
                    val firebase = Firebase.database
                    val db: DataBaseHandler = DataBaseHandler(context.applicationContext)
                    val reference = firebase.getReference("reminders")
                    val reminderLister = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            uploadToWorkManager(context.applicationContext, 5, 1000, message)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            println("Reminder: onCancelled: ${error.details}")
                        }

                    }
                    val child = reference.child(key)
                    child.addValueEventListener(reminderLister)
                }
            }
        }
    }

    fun uploadToWorkManager(context: Context, id: Int, time: Int, message: String){

        val reminderParameters = Data.Builder().putString("message", message).putInt("id", id).build()
        val reminderRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
                .setInputData(reminderParameters)
                .build()
        val workManager : WorkManager = WorkManager.getInstance(context.applicationContext)
        workManager.enqueue(reminderRequest)
    }

}