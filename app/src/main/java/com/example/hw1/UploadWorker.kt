package com.example.hw1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*

class UploadWorker(context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams){

    private var notificationId = 1021
    private val CHANNEL_ID = "channel_id_example_01"

    override fun doWork(): Result {
        //MainActivity().sendNotification(applicationContext)
        for (i in 0..30){
            Log.i("MYTAG", "Downloading $i")
        }
        val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = time.format(Date())
        Log.i("MYTAG", "Completed $currentDate")

        //createNotificationChannel()
        val text = inputData.getString("message")
        //notificationId += 1
        sendNotification(applicationContext, text!!)
        return Result.success()
    }
    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = MainActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun sendNotification(context: Context, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)
        val bitmapLargeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reminder: " + message)
                .setContentText("Reminder " + message + " is happening now!")
                .setLargeIcon(bitmapLargeIcon)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)){
            notify(notificationId, builder.build())
        }
    }
}