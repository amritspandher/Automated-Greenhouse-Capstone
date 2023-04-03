package com.example.automatedgreenhouse

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class LoginPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var usernameLiveData = MutableLiveData<String>()
    private var passwordLiveData = MutableLiveData<String>()
    private val isValidLiveData = MediatorLiveData<Boolean>().apply {
        this.value = false

        addSource(usernameLiveData){ email ->
            val password = passwordLiveData.value
            this.value = validateForm(email, password)
        }

        addSource(passwordLiveData){ password ->
            val username = usernameLiveData.value
            this.value = validateForm(username, password)
        }
    }

    override fun onStart() {
        super.onStart()
        // logs out when returning to this page
        auth.signOut()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth

        val queue = MySingleton.getInstance(this.applicationContext).requestQueue

        var username = findViewById<TextInputLayout>(R.id.username)
        var password = findViewById<TextInputLayout>(R.id.password)
        val btLogin = findViewById<Button>(R.id.button_login)

        username.editText?.doOnTextChanged { text, _, _, _ ->
            usernameLiveData.value = text?.toString()
        }
        password.editText?.doOnTextChanged { text, _, _, _ ->
            passwordLiveData.value = text?.toString()
        }

        isValidLiveData.observe(this) { isValid ->
            btLogin.isEnabled = isValid
        }

        btLogin?.setOnClickListener {
            var username = findViewById<TextInputEditText>(R.id.username_text).text.toString()
            var password = findViewById<TextInputEditText>(R.id.password_text).text.toString()
            var userID = ""

            signIn(username,password)

            Thread.sleep(1000)

            if (auth.currentUser != null)
            {
                Toast.makeText(this,"Login Success",Toast.LENGTH_SHORT).show()

                //get current users
                val jsonArrayGetRequest = object: JsonArrayRequest(Request.Method.GET, User.rdsUrl, null,
                    { response ->
                        Log.d("DEEEBUG userGet","Response: %s".format(response.toString()))
                        val users : JSONObject = response.getJSONObject(0)
                        userID = users.get("userID").toString()

                        //sets global userId
                        User.userId = userID

                        startActivity(Intent (this, HomePage::class.java))
                    },
                    { error ->
                        Log.d("DEEEBUG userGet","${error}")
                    })
                {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        //GET user info
                        headers["table"] = "Users"
                        headers["username"] = username

                        return headers
                    }
                }

                queue.add(jsonArrayGetRequest)
            }
            else
            {
                Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show()
            }
        }

        val btRegister = findViewById<Button>(R.id.button_register)

        btRegister?.setOnClickListener{
            val intent = Intent (this, RegisterPage::class.java)
            startActivity(intent)
        }

        //create Account with entered username/password
        if (intent.hasExtra("user"))
        {
            var newUser = intent.extras?.getCharSequence("user").toString()
            var newPass = intent.extras?.getCharSequence("pass").toString()

            //create Account for authentication, adds to database if successful
            createAccount(newUser, newPass, queue)
        }
    }

    private fun validateForm(username: String?, password: String?) : Boolean {
        val isValidUsername = username != null && username.isNotBlank() && username.contains("@")
        val isValidPassword = password != null && password.isNotBlank() && password.length >= 6

        return isValidUsername && isValidPassword
    }

    private fun createAccount(email: String, password: String, queue: RequestQueue) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")

                    //upload to database
                    val jsonArrayPutRequest = object: JsonArrayRequest(Request.Method.POST, User.rdsUrl, null,
                        { response ->
                            Log.d("DEEEBUG createUser","Response: %s".format(response.toString()))
                        },
                        { error ->
                            Log.d("DEEEBUG createUser","${error}")
                        })
                    {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()

                            // PUT users
                            headers["table"] = "Users"
                            headers["username"] = email
                            headers["password"] = password

                            return headers
                        }
                    }

                    queue.add(jsonArrayPutRequest)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed." + task.exception,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed." + task.exception,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun isProbablyAnEmulator() = Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.BOARD == "QC_Reference_Phone" //bluestacks
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.HOST.startsWith("Build") //MSI App Player
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || "google_sdk" == Build.PRODUCT

    companion object {
        private const val TAG = "EmailPassword"
    }
}