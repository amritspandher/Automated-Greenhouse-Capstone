package com.example.automatedgreenhouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.google.android.material.textfield.TextInputEditText

class AddNewModule : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnewmodule)

        val queue = MySingleton.getInstance(this.applicationContext).requestQueue

        val bt_addModule = findViewById<AppCompatButton>(R.id.addModule)
        val moduleName = findViewById<TextInputEditText>(R.id.moduleName_text).text
        bt_addModule?.setOnClickListener {

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

                    // POST new module
                    //headers["table"] = "Greenhouses"
                    //headers["name"] = systemName.toString()
                    //headers["userid"] = userID

                    return headers
                }
            }
            queue.add(jsonArrayPostRequest)

            val intent = Intent (this, ModulePage::class.java)
            intent.putExtra("moduleName", moduleName)
            startActivity(intent)
        }
    }
}