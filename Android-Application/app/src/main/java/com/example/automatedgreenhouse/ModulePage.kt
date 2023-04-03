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

class ModulePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulepage)

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

        //turn lights on/off
        var bt_module1_power = findViewById<AppCompatButton>(R.id.module1_power)
        var powerState = "off"

        bt_module1_power?.setOnClickListener {

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
                    headers["moduleid"] = "1004"
                    headers["action"] = powerState

                    return headers
                }
            }
            queue.add(jsonArrayPostRequest)

            powerState = if (powerState == "on")
                "off"
            else
                "on"

            bt_module1_power.text = powerState
        }

        //turn fans on/off
        var bt_module2_power = findViewById<AppCompatButton>(R.id.module2_power)
        var powerStateModuleTwo = "off"

        bt_module2_power?.setOnClickListener {

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
                    headers["moduleid"] = "1003"
                    headers["action"] = powerStateModuleTwo

                    return headers
                }
            }
            queue.add(jsonArrayPostRequest)

            powerStateModuleTwo = if (powerStateModuleTwo == "on")
                "off"
            else
                "on"

            bt_module2_power.text = powerStateModuleTwo
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
                    headers["moduleid"] = "1005"
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

        var mod4_temp = findViewById<TextView>(R.id.module4_tempNum)
        var bt_module4_get = findViewById<AppCompatButton>(R.id.module4_get)
            bt_module4_get.setOnClickListener {
                //get temperature
                val timestreamArray = arrayOfNulls<String>(4)
                val jsonTimestreamObjectGetRequest = object : JsonObjectRequest(
                    Request.Method.GET, User.timestreamUrl, null,
                    { response ->
                        Log.d(
                            "DEEEBUG tempGet",
                            "Response: %s".format(response.toString())
                        )

                        if (response.has("measure_value"))
                        {
                            var temperatures = response.getString("measure_value")
                            temperatures = temperatures.take(4)

                            if (timestreamArray.isNotEmpty()) {
                                mod4_temp.text = temperatures
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

            }

            var mod3_humid = findViewById<TextView>(R.id.module3_humidNum)
            var bt_module3_get = findViewById<AppCompatButton>(R.id.module3_get)
            bt_module3_get.setOnClickListener {
                //get humidity
                val jsonTimestreamObjectGetHumidityRequest = object : com.android.volley.toolbox.JsonObjectRequest(
                    com.android.volley.Request.Method.GET, User.timestreamUrl, null,
                    { response ->
                        android.util.Log.d(
                            "DEEEBUG humidGet",
                            "Response: %s".format(response.toString())
                        )

                        if (response.has("measure_value"))
                        {
                            var humidity = response.getString("measure_value")
                            humidity = humidity.take(4)

                            if (humidity != null) {
                                mod3_humid.text = humidity
                            }
                        }

                    },
                    { error ->
                        android.util.Log.d("DEEEBUG humidGet", "${error}")
                    }) {
                    override fun getHeaders(): kotlin.collections.MutableMap<kotlin.String, kotlin.String> {
                        val headers = HashMap<kotlin.String, kotlin.String>()

                        //GET humidity
                        headers["moduleid"] = "1002"
                        headers["measurename"] = "humidity"

                        return headers
                    }
                }

                queue.add(jsonTimestreamObjectGetHumidityRequest)
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
                    headers["moduleid"] = "1009"
                    headers["measurename"] = "soilMoisture"

                    return headers
                }
            }

            queue.add(jsonTimestreamObjectGetHumidityRequest)
        }

        var mod7_waterLevel = findViewById<TextView>(R.id.module7_percentNum)
        var bt_module7_get = findViewById<AppCompatButton>(R.id.module7_get)
        bt_module7_get.setOnClickListener {
            //get humidity
            val jsonTimestreamObjectGetHumidityRequest = object : com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET, User.timestreamUrl, null,
                { response ->
                    android.util.Log.d(
                        "DEEEBUG WaterLevelGet",
                        "Response: %s".format(response.toString())
                    )

                    if (response.has("measure_value"))
                    {
                        var level = response.getString("measure_value")
                        level = level.take(4)

                        if (level != null) {
                            mod7_waterLevel.text = level
                        }
                    }

                },
                { error ->
                    android.util.Log.d("DEEEBUG WaterLevelGet", "${error}")
                }) {
                override fun getHeaders(): kotlin.collections.MutableMap<kotlin.String, kotlin.String> {
                    val headers = HashMap<kotlin.String, kotlin.String>()

                    //GET humidity
                    headers["moduleid"] = "1006"
                    headers["measurename"] = "waterLevel"

                    return headers
                }
            }

            queue.add(jsonTimestreamObjectGetHumidityRequest)
        }

        var bt_module8_camera = findViewById<AppCompatButton>(R.id.module8_capture)
        bt_module8_camera.setOnClickListener {
            // jump to camera page
            startActivity(Intent(this, CameraPage::class.java))
        }
    }
}