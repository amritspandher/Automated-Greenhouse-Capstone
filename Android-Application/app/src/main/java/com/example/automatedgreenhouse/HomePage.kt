package com.example.automatedgreenhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule
import android.preference.PreferenceManager

import android.content.SharedPreferences

import android.R.string.no
import org.json.JSONException

class HomePage : AppCompatActivity() {

    var running = true

    var userID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val queue = MySingleton.getInstance(this.applicationContext).requestQueue

        var system1 = findViewById<TableLayout>(R.id.system1)
        system1.isVisible = false
        var system2 = findViewById<TableLayout>(R.id.system2)
        system2.isVisible = false
        var system3 = findViewById<TableLayout>(R.id.system3)
        system3.isVisible = false

        userID = User.userId

        val timer = Timer("sensorUpdateTimer")
        val timer2 = Timer("sensorUpdateTimer2")

        val bt_addSystem = findViewById<AppCompatButton>(R.id.addSystem)
        bt_addSystem?.setOnClickListener {
            val newIntent = Intent(this, AddNewSystem::class.java)
            newIntent.putExtra("userID", userID)
            startActivity(newIntent)
        }

        //get greenhouses for this user
        val greenhouseArray = arrayOfNulls<String>(4)
        val jsonArrayGetRequest = object : JsonArrayRequest(
            Request.Method.GET, User.rdsUrl, null,
            { response ->
                Log.d(
                    "DEEEBUG",
                    "HomePage greenhouseArray jsonArrayGET Response: %s".format(response.toString())
                )

                if (!response.isNull(0)) {
                    val greenhouses: JSONObject = response.getJSONObject(0)
                    greenhouseArray[0] = greenhouses.getString("name")
                    println("greenhouseArray[0]: " + greenhouseArray[0])

                    try {
                        val greenhouses: JSONObject = response.getJSONObject(1)
                        greenhouseArray[1] = greenhouses.getString("name")
                        println("greenhouseArray[1]: " + greenhouseArray[1])

                    }
                    catch (e: JSONException)
                    {

                    }

                    try {
                        val greenhouses: JSONObject = response.getJSONObject(2)
                        greenhouseArray[2] = greenhouses.getString("name")
                        println("greenhouseArray[2]: " + greenhouseArray[2])

                    }
                    catch (e: JSONException)
                    {

                    }

                    if (greenhouseArray.isNotEmpty()) {

                        if (greenhouseArray[2] != null)
                        {
                            var bt_system3 = findViewById<AppCompatButton>(R.id.system3_name)
                            bt_system3.text = greenhouseArray[2]
                            system3.isVisible = true

                            bt_system3?.setOnClickListener {
                                var intent = Intent(this, MainDisplay::class.java)
                                intent.putExtra("systemName", bt_system3.text)
                                timer.cancel()
                                startActivity(intent)
                            }
                        }
                        if (greenhouseArray[1] != null)
                        {
                            var bt_system2 = findViewById<AppCompatButton>(R.id.system2_name)
                            bt_system2.text = greenhouseArray[1]
                            system2.isVisible = true

                            bt_system2?.setOnClickListener {
                                var intent = Intent(this, MainDisplay::class.java)
                                intent.putExtra("systemName", bt_system2.text)
                                User.greenhouseId_2 = "2"
                                User.greenhouseId = "2"
                                timer.cancel()
                                timer2.cancel()
                                startActivity(intent)
                            }
                        }
                        if (greenhouseArray[0] != null)
                        {
                            User.greenhouseName_1 = greenhouseArray[0].toString()
                            var bt_system1 = findViewById<AppCompatButton>(R.id.system1_name)
                            bt_system1.text = User.greenhouseName_1
                            system1.isVisible = true

                            bt_system1?.setOnClickListener {
                                var intent = Intent(this, MainDisplay::class.java)
                                intent.putExtra("systemName", bt_system1.text)
                                User.greenhouseId_1 = "1"
                                User.greenhouseId = "1"
                                timer.cancel()
                                timer2.cancel()
                                startActivity(intent)
                            }
                        }
                    }
                }

            },
            { error ->
                Log.d("DEEEBUG", "${error}")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                //GET greenhouse info
                headers["table"] = "Greenhouses"
                headers["userid"] = userID

                return headers
            }
        }
        queue.add(jsonArrayGetRequest)

        var sys1_temp = findViewById<TextView>(R.id.system1_tempNum)
        var sys1_humid = findViewById<TextView>(R.id.system1_humidityNum)

        timer.schedule(1000, 5000)
        {
            //get temperature
            val timestreamArray = arrayOfNulls<String>(4)
            val jsonTimestreamObjectGetRequest = object : JsonObjectRequest(
                Request.Method.GET, User.timestreamUrl, null,
                { response ->
                    Log.d(
                        "DEEEBUG tempGet",
                        "HomePage timestreamArray jsonArrayGET Response: %s".format(response.toString())
                    )

                    if (response.has("measure_value"))
                    {
                        var temperatures = response.getString("measure_value")
                        temperatures = temperatures.take(4)

                        if (timestreamArray.isNotEmpty()) {
                            sys1_temp.text = temperatures
                        }
                    }
                    else
                    {
                        Log.d("DEEBUG", "NOTHIN IN THE TIMESTREAM")
                    }

                },
                { error ->
                    Log.d("DEEEBUG tempGet", "${error}")
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    //GET temperature
                    headers["moduleid"] = "1002"
                    headers["measurename"] = "temperature"

                    return headers
                }
            }

            queue.add(jsonTimestreamObjectGetRequest)

            //get humidity
            val jsonTimestreamObjectGetHumidityRequest = object : JsonObjectRequest(
                Request.Method.GET, User.timestreamUrl, null,
                { response ->
                    Log.d(
                        "DEEEBUG humidGet",
                        "HomePage Response: %s".format(response.toString())
                    )

                    if (response.has("measure_value"))
                    {
                        var humidity = response.getString("measure_value")
                        humidity = humidity.take(4)

                        if (timestreamArray.isNotEmpty()) {
                            sys1_humid.text = humidity
                        }
                    }

                },
                { error ->
                    Log.d("DEEEBUG humidGet", "${error}")
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    //GET humidity
                    headers["moduleid"] = "1002"
                    headers["measurename"] = "humidity"

                    return headers
                }
            }

            queue.add(jsonTimestreamObjectGetHumidityRequest)
        }

        var sys2_humid = findViewById<TextView>(R.id.system2_humidityNum)

        timer2.schedule(1000, 5000)
        {
            //get temperature
            val timestreamArray = arrayOfNulls<String>(4)

            //get soil moisture
            val jsonTimestreamObjectGetHumidityRequest = object : JsonObjectRequest(
                Request.Method.GET, User.timestreamUrl, null,
                { response ->
                    Log.d(
                        "DEEEBUG soilMoistGet",
                        "HomePage Response: %s".format(response.toString())
                    )

                    if (response.has("measure_value"))
                    {
                        var humidity = response.getString("measure_value")
                        humidity = humidity.take(4)

                        if (timestreamArray.isNotEmpty()) {
                            sys2_humid.text = humidity
                        }
                    }

                },
                { error ->
                    Log.d("DEEEBUG humidGet", "${error}")
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    //GET humidity
                    headers["moduleid"] = "2001"
                    headers["measurename"] = "soilMoisture"

                    return headers
                }
            }

            queue.add(jsonTimestreamObjectGetHumidityRequest)
        }

    }
}