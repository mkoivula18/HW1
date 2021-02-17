package com.example.hw1


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AddReminder : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        findViewById<Button>(R.id.finishReminder).setOnClickListener(){
            var finishintent = Intent(applicationContext, MenuActivity::class.java)
            startActivity(finishintent)

        }

    }
}