package com.example.hw1

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelStore

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        findViewById<Button>(R.id.btnLogout).setOnClickListener(){
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        val listView = findViewById<ListView>(R.id.listView)
        val names = arrayOf("Reminder_1: ", "Reminder_2: ", "Reminder_3:", "Reminder_4:")

        val arrayAdapter:ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, names
        )
        listView.adapter = arrayAdapter
//        listView.setOnClickListener { adapterView, view, i, l->
//            Toast.makeText(this, "Item selected" + names[i], Toast.LENGTH_LONG)
//                .show()
//        }

        findViewById<Button>(R.id.newReminder).setOnClickListener {
            startActivity(Intent(applicationContext, AddReminder::class.java))
        }

    }
}
