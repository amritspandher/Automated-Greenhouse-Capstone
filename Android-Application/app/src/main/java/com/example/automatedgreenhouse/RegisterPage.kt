package com.example.automatedgreenhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputLayout

class RegisterPage : AppCompatActivity() {

    private val usernameLiveData = MutableLiveData<String>()
    private val passwordLiveData = MutableLiveData<String>()
    private val passwordConfirmLiveData = MutableLiveData<String>()
    private val isValidLiveData = MediatorLiveData<Boolean>().apply {
        this.value = false

        addSource(usernameLiveData){ email ->
            val password = passwordLiveData.value
            val passwordConfirm = passwordConfirmLiveData.value
            this.value = validateForm(email, password, passwordConfirm)
        }

        addSource(passwordLiveData){ password ->
            val username = usernameLiveData.value
            val passwordConfirm = passwordConfirmLiveData.value
            this.value = validateForm(username, password, passwordConfirm)
        }

        addSource(passwordConfirmLiveData){ passwordConfirm ->
            val username = usernameLiveData.value
            val password = passwordLiveData.value
            this.value = validateForm(username, password,passwordConfirm)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val username = findViewById<TextInputLayout>(R.id.username)
        val password = findViewById<TextInputLayout>(R.id.password)
        val passwordConfirm = findViewById<TextInputLayout>(R.id.password_confirm)
        val btRegister = findViewById<AppCompatButton>(R.id.button_register)

        username.editText?.doOnTextChanged { text, _, _, _ ->
            usernameLiveData.value = text?.toString()
        }
        password.editText?.doOnTextChanged { text, _, _, _ ->
            passwordLiveData.value = text?.toString()
        }
        passwordConfirm.editText?.doOnTextChanged { text, _, _, _ ->
            passwordConfirmLiveData.value = text?.toString()
        }

        isValidLiveData.observe(this) { isValid ->
            btRegister.isEnabled = isValid
        }

        btRegister?.setOnClickListener{
            //pass username and password to LoginPage to use it
            Toast.makeText(this,"Account Created",Toast.LENGTH_SHORT).show()

            val newIntent = Intent(this, LoginPage::class.java)

            val regUser = username.editText?.text.toString()
            val regPass = password.editText?.text.toString()

            newIntent.putExtra("user", regUser)
            newIntent.putExtra("pass", regPass)
            startActivity(newIntent)
        }

        val btCancel = findViewById<AppCompatButton>(R.id.button_cancel)
        btCancel?.setOnClickListener{
            startActivity(Intent(this, LoginPage::class.java))
        }
    }

    private fun validateForm(username: String?, password: String?, passwordConfirm: String?) : Boolean {
        val isValidUsername = username != null && username.isNotBlank() && username.contains("@")
        val isValidPassword = password != null && password.isNotBlank()  && password == passwordConfirm && password.length >= 6

        return isValidUsername && isValidPassword
    }
}