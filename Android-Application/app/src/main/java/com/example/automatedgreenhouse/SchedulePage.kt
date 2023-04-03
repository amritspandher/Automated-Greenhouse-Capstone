package com.example.automatedgreenhouse

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class SchedulePage : AppCompatActivity() {

    var scheduleIDs = arrayListOf<Int>()
    var missionAchomplished = false
    var maxLength = 10

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedulepage)

        //get existing entries
        AllEntriesGetRequest()

        var bt_home = findViewById<AppCompatImageButton>(R.id.bt_home)
        bt_home?.setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }

        var bt_back = findViewById<AppCompatImageButton>(R.id.bt_back)
        bt_back?.setOnClickListener {
            finish()
        }

        var bt_clear = findViewById<AppCompatImageView>(R.id.clearSchedule)
        bt_clear?.setOnClickListener {
            deleteScheduleEntries("1003", false)
            deleteScheduleEntries("1004", false)
            deleteScheduleEntries("1005", false)
            deleteScheduleEntries("1003", true)
            deleteScheduleEntries("1004", true)
            deleteScheduleEntries("1005", true)
        }

        //addSchedule buttons
        val addScheduleEntry = findViewById<AppCompatImageView>(R.id.addScheduleEntry)
        addScheduleEntry.setOnClickListener {
            val intent = Intent(this, SchedulePageCreate::class.java)
            intent.putExtra("moduleid", "1004")
            startActivity(intent)
        }
        val addScheduleEntry2 = findViewById<AppCompatImageView>(R.id.addScheduleEntry2)
        addScheduleEntry2.setOnClickListener {
            val intent = Intent(this, SchedulePageCreate::class.java)
            intent.putExtra("moduleid", "1003")
            startActivity(intent)
        }
        val addScheduleEntry3 = findViewById<AppCompatImageView>(R.id.addScheduleEntry3)
        addScheduleEntry3.setOnClickListener {
            val intent = Intent(this, SchedulePageCreate::class.java)
            intent.putExtra("moduleid", "1005")
            startActivity(intent)
        }

        //auto buttons
        val autoSchedule = findViewById<AppCompatButton>(R.id.module1_auto)
        autoSchedule.setOnClickListener {
            //go to default light settings, bold text
            if (autoSchedule.typeface.isBold)
            {
                autoSchedule.setTypeface(autoSchedule.typeface, Typeface.NORMAL)
                //deleteScheduleEntries("1004", true)
            }
            else
            {
                //deleteScheduleEntries("1004", false)
                autoScheduleUpdate("1004")
                autoSchedule.setTypeface(autoSchedule.typeface, Typeface.BOLD)
            }
            AllEntriesGetRequest()
            //7am-7pm light schedule
        }
        val autoSchedule2 = findViewById<AppCompatButton>(R.id.module2_auto)
        autoSchedule2.setOnClickListener {
            //go to default fan settings
            if (autoSchedule2.typeface.isBold)
            {
                autoSchedule2.setTypeface(autoSchedule2.typeface, Typeface.NORMAL)
                //deleteScheduleEntries("1003", true)
            }
            else
            {
                //deleteScheduleEntries("1003", false)
                autoScheduleUpdate("1003")
                autoSchedule2.setTypeface(autoSchedule2.typeface, Typeface.BOLD)
            }
            AllEntriesGetRequest()
            //auto based on sensor readings in NodeRed
        }
        val autoSchedule3 = findViewById<AppCompatButton>(R.id.module3_auto)
        autoSchedule3.setOnClickListener {
            //go to default irrigation settings
            if (autoSchedule3.typeface.isBold)
            {
                autoSchedule3.setTypeface(autoSchedule3.typeface, Typeface.NORMAL)
                //deleteScheduleEntries("1005", true)
            }
            else
            {
                //deleteScheduleEntries("1005", false)
                autoScheduleUpdate("1005")
                autoSchedule3.setTypeface(autoSchedule3.typeface, Typeface.BOLD)
            }
            AllEntriesGetRequest()
            //auto based on sensor readings in NodeRed
        }

        //press auto and THEN turn all of that module's time values to --:--

    }



    private fun AllEntriesGetRequest() {
        val queue = MySingleton.getInstance(this.applicationContext).requestQueue
        var scheduleUrl = "https://eknqepv86f.execute-api.us-east-1.amazonaws.com/database/rds"

        val scheduleArray = arrayOfNulls<String>(7)
        val jsonArrayGetRequest = object : JsonArrayRequest(
            Request.Method.GET, scheduleUrl, null,
            { response ->
                Log.d(
                    "debugResponse",
                    "schedulePageCreate scheduleArray jsonArrayGET Response: %s".format(response.toString())
                )

                if (!response.isNull(0)) {
                    var lengthToTraverse = response.length()-1
                    scheduleIDs.clear()
                    for (i in 0..lengthToTraverse) {
                        val schedules: JSONObject = response.getJSONObject(i)

                        //check moduleId, time, action
                        var schedModId = schedules.getString("moduleID")
                        var schedTime = schedules.getString("time")
                        var schedAction= schedules.getString("action")
                        //if there is moduleId/action overlap, delete old one

                        var lights_on = findViewById<TextView>(R.id.module1_on_text)
                        var lights_off = findViewById<TextView>(R.id.module1_off_text)
                        var fans_on = findViewById<TextView>(R.id.module2_on_text)
                        var fans_off = findViewById<TextView>(R.id.module2_off_text)
                        var irrigation_on = findViewById<TextView>(R.id.module3_on_text)
                        var irrigation_off = findViewById<TextView>(R.id.module3_off_text)

                        //moduleId 1004 = lights, 1003 = fans, 1005 = irrigation
                        if (schedModId == "1004")
                        {
                            when (schedAction)
                            {
                                "on" -> lights_on.text = schedTime
                                "off" -> lights_off.text = schedTime

                                else -> Log.d("ERROR", "schedule action is incorrect")

                            }

                        }
                        else if (schedModId == "1003")
                        {
                            when (schedAction)
                            {
                                "on" -> fans_on.text = schedTime
                                "off" -> fans_off.text = schedTime

                                else -> Log.d("ERROR", "schedule action is incorrect")

                            }

                        }
                        else if (schedModId == "1005")
                        {
                            when (schedAction)
                            {
                                "on" -> irrigation_on.text = schedTime
                                "off" -> irrigation_off.text = schedTime

                                else -> Log.d("ERROR", "schedule action is incorrect or auto")

                            }

                        }


                        scheduleIDs.add(schedules.getString("scheduleID").toInt())
                    }
                    println("**************")
                    println(scheduleIDs)
                    println("**************")

                    missionAchomplished = true
                    maxLength = scheduleIDs.size

                    if (scheduleArray.isNotEmpty()) {
                    }
                }
            },
            { error ->
                Log.d("debugError", "$error")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                //GET schedule info
                headers["table"] = "Schedules"
                headers["greenhouseID"] = "1"

                return headers
            }
        }
        queue.add(jsonArrayGetRequest)
    }

    //input moduleId and whether AUTO entry needs to be deleted (true) or MANUAL entries need to be deleted (false)
    private fun deleteScheduleEntries(moduleIdDelete: String, autoDelete: Boolean){

        val queue = MySingleton.getInstance(this.applicationContext).requestQueue
        var scheduleUrl = "https://eknqepv86f.execute-api.us-east-1.amazonaws.com/database/rds"

        val scheduleArray = arrayOfNulls<String>(7)
        val jsonArrayGetRequest = object : JsonArrayRequest(
            Request.Method.GET, scheduleUrl, null,
            { response ->
                Log.d(
                    "debugResponse",
                    "schedulePageCreate scheduleArray jsonArrayGET Response: %s".format(response.toString())
                )

                if (!response.isNull(0)) {
                    var lengthToTraverse = response.length()-1
                    scheduleIDs.clear()
                    for (i in 0..lengthToTraverse) {
                        val schedules: JSONObject = response.getJSONObject(i)

                        //check moduleId, time, action
                        var schedModId = schedules.getString("moduleID")
                        var schedAction= schedules.getString("action")

                        if (moduleIdDelete == schedModId)
                        {
                            if (schedAction == "auto" && autoDelete)
                                scheduleIDs.add(schedules.getString("scheduleID").toInt())
                            else if (schedAction != "auto" && !autoDelete)
                                scheduleIDs.add(schedules.getString("scheduleID").toInt())
                        }

                        scheduleIDs.forEach()
                        {
                            deleteScheduleEntry(it)
                            scheduleIDs.remove(it)
                        }
                    }
                }
            },
            { error ->
                Log.d("debugError", "$error")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                //GET schedule info
                headers["table"] = "Schedules"
                headers["greenhouseID"] = "1"

                return headers
            }


        }
        queue.add(jsonArrayGetRequest)

        //delete each tagged schedule id
        scheduleIDs.forEach()
        {
            deleteScheduleEntry(it)
        }

        AllEntriesGetRequest()
    }

    private fun deleteScheduleEntry(entry: Int) {

        val queue = MySingleton.getInstance(this.applicationContext).requestQueue

        val jsonArrayPostRequest = object: JsonObjectRequest(
            Request.Method.DELETE, User.rdsUrl, null,
            { response ->
                Log.d("DEEEBUG deleteEntry"," Response: %s".format(response.toString()))
            },
            { error ->
                Log.d("DEEEBUG deleteEntry","${error}")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                // POST powerState to module
                headers["table"] = "Schedules"
                headers["scheduleid"] = entry.toString()

                return headers
            }
        }
        queue.add(jsonArrayPostRequest)

    }

    private fun autoScheduleUpdate(moduleIdAuto: String) {
        val queue = MySingleton.getInstance(this.applicationContext).requestQueue

        val jsonArrayPostRequest = object: JsonArrayRequest(
            Request.Method.POST, User.rdsUrl, null,
            { response ->
                Log.d("debug","jsonArrayPOST Response: %s".format(response.toString()))
            },
            { error ->
                Log.d("debug","$error")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                // POST new schedule entry
                headers["table"] = "Schedules"
                headers["greenhouseID"] = "1"
                headers["moduleID"] = moduleIdAuto
                headers["time"] = ""
                headers["day"] = ""
                headers["repeatStatus"] = ""
                headers["action"] = "auto"

                return headers
            }
        }
        queue.add(jsonArrayPostRequest)
    }

}