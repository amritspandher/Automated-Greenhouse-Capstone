package com.example.automatedgreenhouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.google.android.material.textfield.TextInputEditText

class AddNewSystem : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnewsystem)

        val queue = MySingleton.getInstance(this.applicationContext).requestQueue
        var userID = ""

        if (intent.hasExtra("userID"))
        {
            userID = intent.extras?.getCharSequence("userID").toString()

        }

        val bt_addSystem = findViewById<AppCompatButton>(R.id.addSystem)
        val systemName = findViewById<TextInputEditText>(R.id.systemName_text).text
        bt_addSystem?.setOnClickListener {

            //upload to database
            val jsonArrayPostRequest = object: JsonArrayRequest(
                Request.Method.POST, User.rdsUrl, null,
                { response ->
                    Log.d("DEEEBUG","jsonArrayPOST Response: %s".format(response.toString()))
                },
                { error ->
                    Log.d("DEEEBUG","${error}")
                })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    // POST new greenhouse
                    headers["table"] = "Greenhouses"
                    headers["name"] = systemName.toString()
                    headers["userid"] = userID

                    return headers
                }
            }
            queue.add(jsonArrayPostRequest)

            val intent = Intent (this, HomePage::class.java)
            intent.putExtra("systemName", systemName)
            startActivity(intent)
        }
    }
}