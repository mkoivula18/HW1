package com.example.hw1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*


class MainActivity : AppCompatActivity() {

    val adminUsername = "mikko"
    val adminPassword = "123"
    private val notificationId = 101
    private val CHANNEL_ID = "channel_id_example_01"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {

            val un = findViewById<EditText>(R.id.mainUsername)
            val pw = findViewById<EditText>(R.id.mainPassword)

            if (un.getText().toString().equals(adminUsername) && pw.getText().toString().equals(adminPassword)) {

                var loginIntent = Intent(applicationContext, MenuActivity::class.java)
                startActivity(loginIntent)
            }
        }

        val settings = getSharedPreferences("my_shared_pref", 0)
        val password = settings.getString("password", "empty")
        if (password === "empty") {

            //store the value in your edittext as password
        } else {
            //if string in edittext matches with the password value.. let the user enter the activity.. else make a toast of wrong password..
        }
        createNotificationChannel()
    }
    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun sendNotification(context: Context) {
        //val context = this
        Toast.makeText(context, "ALSDLKHAKSJFGHLKAS", Toast.LENGTH_SHORT).show()

        val intent = Intent(context, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)
        val bitmapLargeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Example Title")
                .setContentText("Example Text")
                .setLargeIcon(bitmapLargeIcon)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)){
            notify(notificationId, builder.build())
        }
    }

}
