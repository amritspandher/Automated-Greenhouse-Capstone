package com.example.automatedgreenhouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject

class SchedulePageCreate : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedulecreate)

        val queue = MySingleton.getInstance(this.applicationContext).requestQueue

        var moduleId = intent.extras?.getCharSequence("moduleid").toString()
        var greenhouseId = "1"

        var bt_back = findViewById<AppCompatImageButton>(R.id.bt_back)
        bt_back?.setOnClickListener {
            finish()
        }

        val bt_addTime = findViewById<TextInputLayout>(R.id.timeAdd)
        val time = findViewById<TextInputEditText>(R.id.timeAdd_text).text

        val bt_addAction = findViewById<TextInputLayout>(R.id.actionAdd)
        val action = findViewById<TextInputEditText>(R.id.actionAdd_text).text

        val bt_addSchedule = findViewById<AppCompatImageButton>(R.id.addSchedule)
        bt_addSchedule?.setOnClickListener {

            //upload to database
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
                    headers["greenhouseID"] = greenhouseId
                    headers["moduleID"] = moduleId
                    headers["time"] = time.toString()
                    headers["day"] = "na"
                    headers["repeatStatus"] = "na"
                    headers["action"] = action.toString()

                    return headers
                }
            }
            queue.add(jsonArrayPostRequest)

            val intent = Intent (this, SchedulePage::class.java)
            startActivity(intent)
        }
    }
}