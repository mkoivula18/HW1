package com.example.hw1

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        var menurecycler = findViewById<RecyclerView>(R.id.menurecycler)

        menurecycler.layoutManager = LinearLayoutManager(this)
        val itemAdapter = ItemAdapter(this, getItemsList())
        menurecycler.adapter = itemAdapter

        findViewById<Button>(R.id.btnLogout).setOnClickListener() {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
        findViewById<Button>(R.id.newReminder).setOnClickListener {
            startActivity(Intent(applicationContext, AddReminder::class.java))
        }
    }

    private fun setuplistofdata() {
        var menurecycler = findViewById<RecyclerView>(R.id.menurecycler)
        if (getItemsList().size > 0) {
            menurecycler.visibility = View.VISIBLE
            menurecycler.layoutManager = LinearLayoutManager(this)
            val itemAdapter = ItemAdapter(this, getItemsList())
            menurecycler.adapter = itemAdapter
        }else{
            menurecycler.visibility = View.GONE
        }

    }

    fun getItemsList(): ArrayList<Reminder> {
        val db: DataBaseHandler = DataBaseHandler(this)
        return db.readdata()
    }

    fun informationDialog(reminder: Reminder) {
        val infoDialog = Dialog(this, R.style.Dialogi)
        infoDialog.setCancelable(false)
        infoDialog.setContentView(R.layout.dialog_info)

        infoDialog.findViewById<TextView>(R.id.remindertitle).setText(reminder.message)
        infoDialog.findViewById<TextView>(R.id.dialog_description).setText(reminder.message)
        infoDialog.findViewById<TextView>(R.id.dialog_id).setText(reminder.creator_id.toString())
        infoDialog.findViewById<TextView>(R.id.dialog_creationtime).setText(reminder.creationtime)
        infoDialog.findViewById<TextView>(R.id.dialog_remindertime).setText(reminder.remindertime)
        infoDialog.findViewById<TextView>(R.id.dialog_locationx).setText(reminder.location_x)
        infoDialog.findViewById<TextView>(R.id.dialog_locationy).setText(reminder.location_y)
        infoDialog.findViewById<TextView>(R.id.dialog_reminderseen).setText(reminder.reminderseen)
        infoDialog.show()

        infoDialog.findViewById<Button>(R.id.cancelbutton).setOnClickListener{
            infoDialog.dismiss()
        }

    }

    fun deleteReminder(reminder: Reminder) {
        val db: DataBaseHandler = DataBaseHandler(this)
        val status = db.deletedata(Reminder(reminder.creator_id, "", "", "", "", "", ""))
        if (status > -1) {
            Toast.makeText(this, "Deleted Succesfully", Toast.LENGTH_SHORT).show()
            setuplistofdata()
        }

    }
    fun editReminderDialog(reminder: Reminder){

        val updateDialog = Dialog(this, R.style.Dialogi)
        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_edit)
        //updateDialog.findViewById<TextView>(R.id.dialog_description).setText(reminder.message)
        updateDialog.findViewById<TextView>(R.id.dialog_id).setText(reminder.creator_id.toString())
        updateDialog.findViewById<TextView>(R.id.dialog_creationtime).setText(reminder.creationtime)
        updateDialog.findViewById<TextView>(R.id.dialog_remindertime).setText(reminder.remindertime)
        //updateDialog.findViewById<TextView>(R.id.dialog_locationx).setText(reminder.location_x)
        //updateDialog.findViewById<TextView>(R.id.dialog_locationy).setText(reminder.location_y)
        updateDialog.findViewById<TextView>(R.id.dialog_reminderseen).setText(reminder.reminderseen)




        updateDialog.show()

        updateDialog.findViewById<Button>(R.id.cancelbutton).setOnClickListener{
            updateDialog.dismiss()
        }
        val db: DataBaseHandler = DataBaseHandler(this)

        updateDialog.findViewById<Button>(R.id.editbutton).setOnClickListener{
            var newdesc = updateDialog.findViewById<EditText>(R.id.edited_description).text.toString()
            var newlocx = updateDialog.findViewById<EditText>(R.id.edited_locationx).text.toString()
            var newlocy = updateDialog.findViewById<EditText>(R.id.edited_locationy).text.toString()
            if (newdesc.isNotEmpty() && newlocx.isNotEmpty() && newlocy.isNotEmpty()) {
                val status = db.updatedata(Reminder(reminder.creator_id, newdesc, "", "", newlocx, newlocy, ""))
                if (status > -1) {
                    Toast.makeText(this, "Reminder edited succesfully", Toast.LENGTH_SHORT).show()
                    setuplistofdata()
                    updateDialog.dismiss()
                } else {
                    Toast.makeText(this, "Error occured", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this, "Please fill all boxes", Toast.LENGTH_SHORT).show()
            }

        }

    }
}
