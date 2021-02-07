package com.example.hw1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    val adminUsername = "yes"
    val adminPassword = "123"

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

    }
}