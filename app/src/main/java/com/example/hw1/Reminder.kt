package com.example.hw1

data class Reminder(
    val creator_id:Int,
    val message:String,
    val creationtime:String,
    val remindertime:String,
    val location_x:String,
    val location_y:String,
    val reminderseen:String)


/*
class Reminder{
    var creator_id : Int = 0
    var creationtime : String = ""
    var reminder_time : String = ""
    var message : String = ""
    var reminder_seen : Boolean = false
    var location_x : Int = 0
    var location_y : Int = 0

        constructor(creator_id:Int, message:String, creationtime:String, reminder_time:String, location_x:Int, location_y:Int, reminder_seen:Boolean){
            this.creator_id = creator_id
            this.message = message
            this.creationtime = creationtime
            this.reminder_time = reminder_time
            this.location_x = location_x
            this.location_y = location_y
            this.reminder_seen = reminder_seen

        }

}

*/
