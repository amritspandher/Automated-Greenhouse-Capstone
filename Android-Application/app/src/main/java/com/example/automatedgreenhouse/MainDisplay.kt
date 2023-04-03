package com.example.automatedgreenhouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

class MainDisplay : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val queue = MySingleton.getInstance(this.applicationContext).requestQueue

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maindisplay)

        val timer = Timer("sensorUpdateTimer")
        val timer2 = Timer("sensorUpdateTimer2")

        val scheduleButton = findViewById<AppCompatImageButton>(R.id.schedules)
        scheduleButton.setOnClickListener {
            timer.cancel()
            startActivity(Intent(this, SchedulePage::class.java))
        }

        //will grab the system Name when it is received from HomePage, otherwise will just use what is stored in greenhouseName_1
        var systemName = intent.extras?.getCharSequence("systemName").toString()
        if (systemName != "null")
            User.greenhouseName_1 = systemName

        val systemNameTitle = findViewById<TextView>(R.id.systemName)
        systemNameTitle.text = User.greenhouseName_1

        var bt_module = findViewById<AppCompatImageButton>(R.id.modules)
        bt_module?.setOnClickListener {
            timer.cancel()
            if (User.greenhouseId == "1")
                startActivity(Intent(this, ModulePage::class.java))
            else if (User.greenhouseId == "2")
                startActivity(Intent(this, ModulePage2::class.java))
        }

        var bt_stats = findViewById<AppCompatImageButton>(R.id.stats)
        bt_stats?.setOnClickListener {
            timer.cancel()
            startActivity(Intent(this, StatsPage::class.java))
        }


        var tempDisplayRow = findViewById<TableRow>(R.id.tempRow)

        if (User.greenhouseId == "1")
        {
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
                            "MainDisplay timestreamArray jsonArrayGET Response: %s".format(response.toString())
                        )

                        if (response.has("measure_value"))
                        {
                            var temperatures = response.getString("measure_value")
                            temperatures = temperatures.take(4)

                            if (timestreamArray.isNotEmpty()) {
                                sys1_temp.text = temperatures
                            }
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
                            "MainDisplay Response: %s".format(response.toString())
                        )

                        if (response.has("measure_value"))
                        {
                            var humidity = response.getString("measure_value")

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

        }
        else if (User.greenhouseId == "2")
        {
            tempDisplayRow.isVisible = false

            var sys2_humid = findViewById<TextView>(R.id.system1_humidityNum)
            var sys2_soilMoisure_text = findViewById<TextView>(R.id.system1_humidity)
            sys2_soilMoisure_text.text = "Soil Moisture:"

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



    override fun onStop()
    {
        Timer().cancel()
        super.onStop()
    }
}
