package com.example.hw1


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AddReminder : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    val context = this
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
        setContentView(R.layout.activity_add_reminder)
        pickDate()
        //MainActivity().sendNotification(this)
        findViewById<Button>(R.id.donebutton).setOnClickListener(){
            if(findViewById<EditText>(R.id.rmdrDesc).text.isNotEmpty()){
                val message = findViewById<EditText>(R.id.rmdrDesc).text.toString()
                val sdf = SimpleDateFormat("d/M/yyyy/H/m")
                val currentDate = sdf.format(Date())
                val time = currentDate.toString()
                val db: DataBaseHandler = DataBaseHandler(this)
                val status = db.insertData(Reminder(0, message, time, remindertime, "placeholder", "placeholder", "placeholder", ))

                if (status > -1){
                    findViewById<EditText>(R.id.rmdrDesc).text.clear()

                    uploadToWorkManager(this, 5, 5000, message)

                    startActivity(Intent(applicationContext, MenuActivity::class.java))
                }else{
                    Toast.makeText(context, "tooooobaad", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Please fill the data correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDateTimeCalendar(){

        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun pickDate(){
        findViewById<Button>(R.id.timePickerBtn).setOnClickListener{
            getDateTimeCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }
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
    fun uploadToWorkManager(context: Context, id: Int, time: Int, message: String){
        val reminderParameters = Data.Builder().putString("message", message).putInt("id", id).build()
        val minutesFromNow = cal.timeInMillis - System.currentTimeMillis()
        Log.d("Lab", "Time in millis: ${cal.timeInMillis}")
        Log.d("Lab", "Systemtime in millis: ${System.currentTimeMillis()}")
        Log.d("Lab", "Minutes from now: ${minutesFromNow}")

        val reminderRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
                .setInputData(reminderParameters)
                .setInitialDelay(minutesFromNow, TimeUnit.MILLISECONDS)
                .build()
        val workManager : WorkManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(reminderRequest)
        workManager.getWorkInfoByIdLiveData(reminderRequest.id).observe(this, androidx.lifecycle.Observer { Toast.makeText(this, it.state.name, Toast.LENGTH_SHORT).show() })


    }
}