package com.example.hw1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.contentValuesOf
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class DataBaseHandler(var context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    var showAll = 0
    companion object{
        const val DATABASE_VERSION = 5
        const val DATABASE_NAME = "ReminderDatabase"
        const val TABLE_NAME = "Reminders"
        const val COL_DESCRIPTION = "Description"
        const val COL_ID = "Id"
        const val COL_CREATIONTIME = "CreationTime"
        const val COL_REMINDERTIME = "ReminderTime"
        const val COL_LOCATION_X = "Locationx"
        const val COL_LOCATION_Y = "Locationy"
        const val COL_REMINDER_SEEN = "Reminderseen"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY, " + COL_DESCRIPTION + " TEXT, "
                + COL_CREATIONTIME + " TEXT, "
                + COL_REMINDERTIME + " TEXT, "
                + COL_LOCATION_X + " TEXT, "
                + COL_LOCATION_Y + " TEXT, "
                + COL_REMINDER_SEEN + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
    fun insertData(reminder: Reminder): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_DESCRIPTION, reminder.message)
        cv.put(COL_CREATIONTIME, reminder.creationtime)
        cv.put(COL_REMINDERTIME, reminder.remindertime)
        cv.put(COL_LOCATION_X, reminder.location_x)
        cv.put(COL_LOCATION_Y, reminder.location_y)
        cv.put(COL_REMINDER_SEEN, reminder.reminderseen)
        val result = db.insert(TABLE_NAME, null, cv)

        if (result == (-1).toLong())
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

        }
        db.close()
        return result
    }

    fun readdata() : ArrayList<Reminder>{
        val reminderlist: ArrayList<Reminder> = ArrayList<Reminder>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var message: String
        var creation_time: String
        var remindertime: String
        var location_x: String
        var location_y: String
        var reminderseen: String

        if (cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                message = cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION))
                creation_time = cursor.getString(cursor.getColumnIndex(COL_CREATIONTIME))
                remindertime = cursor.getString(cursor.getColumnIndex(COL_REMINDERTIME))
                location_x = cursor.getString(cursor.getColumnIndex(COL_LOCATION_X))
                location_y = cursor.getString(cursor.getColumnIndex(COL_LOCATION_Y))
                reminderseen = cursor.getString(cursor.getColumnIndex(COL_REMINDER_SEEN))

                val rem = Reminder(id, message, creation_time, remindertime, location_x, location_y, reminderseen)
                reminderlist.add(rem)
            }while (cursor.moveToNext())
        }
        return reminderlist
    }
    fun updatedata(reminder: Reminder): Int {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_DESCRIPTION, reminder.message)
        //cv.put(COL_CREATIONTIME, reminder.creationtime)
        // cv.put(COL_REMINDERTIME, reminder.remindertime)
        cv.put(COL_LOCATION_X, reminder.location_x)
        cv.put(COL_LOCATION_Y, reminder.location_y)
        //cv.put(COL_REMINDER_SEEN, reminder.reminderseen)

        val success = db.update(TABLE_NAME, cv, COL_ID + "=" + reminder.creator_id, null)

        db.close()
        return success
    }
    fun deletedata(reminder: Reminder): Int {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_ID, reminder.creator_id)
        val success = db.delete(TABLE_NAME, COL_ID + "=" + reminder.creator_id, null)

        db.close()
        return success
    }
}

