package com.example.automatedgreenhouse

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.automatedgreenhouse.databinding.ActivityCamerapageBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class CameraPage : AppCompatActivity() {

    lateinit var binding : ActivityCamerapageBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityCamerapageBinding.inflate(layoutInflater)
            setContentView(binding.root)
            //setContentView(R.layout.activity_camerapage)

            val queue = MySingleton.getInstance(this.applicationContext).requestQueue

            var bt_home = findViewById<AppCompatImageButton>(R.id.bt_home)
            bt_home?.setOnClickListener {
                startActivity(Intent(this, HomePage::class.java))
            }

            var bt_back = findViewById<AppCompatImageButton>(R.id.bt_back)
            bt_back?.setOnClickListener {
                finish()
            }

            var bt_get_image = findViewById<AppCompatButton>(R.id.bt_get_image)

            bt_get_image?.setOnClickListener {

                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Fetching image...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val imageName = "image"
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("pictures/$imageName.jpg")

                val localfile = File.createTempFile("tempImage", "jpg")
                storageRef.getFile(localfile).addOnSuccessListener {

                    if (progressDialog.isShowing)
                        progressDialog.dismiss()

                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    binding.viewImage.setImageBitmap(bitmap)

                }.addOnFailureListener {

                    if (progressDialog.isShowing)
                        progressDialog.dismiss()

                    Toast.makeText(this, "Failed to retrieve the image", Toast.LENGTH_SHORT).show()

                }
            }

            var bt_new_image = findViewById<AppCompatButton>(R.id.bt_new_image)

            bt_new_image?.setOnClickListener {

                //POST iot/control
                val jsonArrayPostRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    "https://eknqepv86f.execute-api.us-east-1.amazonaws.com/iot/control",
                    null,
                    { response ->
                        Log.d("DEEEBUG postRequest", "sjonArrayPOST iot/control Response: on")
                    },
                    { error ->
                        Log.d("DEEEBUG postRequest", "${error}")
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        //POST powerState to module
                        headers["moduleid"] = "1012"
                        headers["action"] = "on"

                        return headers
                    }
                }
                queue.add(jsonArrayPostRequest)

                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Fetching image...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({

                    val imageName = "image"
                    val storageRef =
                        FirebaseStorage.getInstance().reference.child("pictures/$imageName.jpg")

                    val localfile = File.createTempFile("tempImage", "jpg")
                    storageRef.getFile(localfile).addOnSuccessListener {

                        if (progressDialog.isShowing)
                            progressDialog.dismiss()

                        val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                        binding.viewImage.setImageBitmap(bitmap)

                    }.addOnFailureListener {

                        if (progressDialog.isShowing)
                            progressDialog.dismiss()

                        Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show()

                    }
                }, 7000)

                val handler2 = Handler(Looper.getMainLooper())
                handler2.postDelayed({

                    bt_new_image.setEnabled(true);
                }, 20000)
                bt_new_image.setEnabled(false);
            }
        }
    }
