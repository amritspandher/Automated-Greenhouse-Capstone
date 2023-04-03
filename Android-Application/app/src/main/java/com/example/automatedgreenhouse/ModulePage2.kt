package com.example.automatedgreenhouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest

class ModulePage2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulepage2)

        val queue = MySingleton.getInstance(this.applicationContext).requestQueue

        var bt_home = findViewById<AppCompatImageButton>(R.id.bt_home)
        bt_home?.setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }

        var bt_back = findViewById<AppCompatImageButton>(R.id.bt_back)
        bt_back?.setOnClickListener {
            finish()
        }

        var bt_addModule = findViewById<AppCompatButton>(R.id.addSystem)
        bt_addModule?.setOnClickListener {
            startActivity(Intent(this, AddNewModule::class.java))
        }

        //turn irrigation on/off
        var bt_module6_power = findViewById<AppCompatButton>(R.id.module6_power)
        var powerState6 = "off"

        bt_module6_power?.setOnClickListener {

            //POST iot/control
            val jsonArrayPostRequest = object: JsonObjectRequest(
                Request.Method.POST, User.iotControlUrl, null,
                { response ->
                    Log.d("DEEEBUG postRequest","jsonArrayPOST iot/control Response: %s".format(response.toString()))
                },
                { error ->
                    Log.d("DEEEBUG postRequest","${error}")
                })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    // POST powerState to module
                    headers["moduleid"] = "2002"
                    headers["action"] = powerState6

                    return headers
                }
            }
            queue.add(jsonArrayPostRequest)

            powerState6 = if (powerState6 == "on")
                "off"
            else
                "on"

            bt_module6_power.text = powerState6
        }

        var mod5_soilMoisture = findViewById<TextView>(R.id.module5_percentNum)
        var bt_module5_get = findViewById<AppCompatButton>(R.id.module5_get)
        bt_module5_get.setOnClickListener {
            //get humidity
            val jsonTimestreamObjectGetHumidityRequest = object : com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET, User.timestreamUrl, null,
                { response ->
                    android.util.Log.d(
                        "DEEEBUG SoilMoistureGet",
                        "Response: %s".format(response.toString())
                    )

                    if (response.has("measure_value"))
                    {
                        var level = response.getString("measure_value")
                        level = level.take(4)

                        if (level != null) {
                            mod5_soilMoisture.text = level
                        }
                    }

                },
                { error ->
                    android.util.Log.d("DEEEBUG SoilMoistureGet", "${error}")
                }) {
                override fun getHeaders(): kotlin.collections.MutableMap<kotlin.String, kotlin.String> {
                    val headers = HashMap<kotlin.String, kotlin.String>()

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