package com.example.hw1


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import im.dino.dbinspector.helpers.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class AddReminder : AppCompatActivity() {
    val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        //Toast.makeText(context, currentDate, Toast.LENGTH_LONG).show()
        findViewById<Button>(R.id.donebutton).setOnClickListener(){
            if(findViewById<EditText>(R.id.rmdrDesc).text.isNotEmpty()){
                val message = findViewById<EditText>(R.id.rmdrDesc).text.toString()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                val time = currentDate.toString()
                val db: DataBaseHandler = DataBaseHandler(this)
                val status = db.insertData(Reminder(0, message, time, "placeholder", "placeholder", "placeholder", "placeholder", ))

                if (status > -1){
                    //Toast.makeText(context, currentDate, Toast.LENGTH_LONG).show()
                    findViewById<EditText>(R.id.rmdrDesc).text.clear()
                    startActivity(Intent(applicationContext, MenuActivity::class.java))
                }else{
                    Toast.makeText(context, "tooooobaad", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Please fill the data correctly", Toast.LENGTH_SHORT).show()
            }


        }

    }

    fun addRecord(view: View){

    }
}