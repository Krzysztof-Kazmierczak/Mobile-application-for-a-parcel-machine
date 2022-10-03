package com.example.inzynierka.aktywnosci

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inzynierka.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern


class RegistrationActivity : AppCompatActivity() {

    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +  "(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                //"(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{5,}" +  //at least 4 characters
                "$"
    )

    private val fbAuth = FirebaseAuth.getInstance()

    private var textInputEmail: TextInputLayout? = null
    private var textInputPhoneNumber: TextInputLayout? = null
    private var textInputPassword: TextInputLayout? = null
    private var textInputPasswordRepeat: TextInputLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        textInputEmail = findViewById(R.id.RegMail)
        textInputPhoneNumber = findViewById(R.id.RegNumPhon)
        textInputPassword = findViewById(R.id.RegPass)
        textInputPasswordRepeat = findViewById(R.id.RegPassRep)
    }

    private fun validateEmail(): Boolean {
        val emailInput = textInputEmail!!.editText!!.text.toString().trim { it <= ' ' }
        return if (emailInput.isEmpty()) {
            textInputEmail!!.error = "Field can't be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail!!.error = "Please enter a valid email address"
            false
        } else {
            textInputEmail!!.error = null
            true
        }
    }

    private fun validatePhone(): Boolean {
        val phoneInput = textInputPhoneNumber!!.editText!!.text.toString().trim { it <= ' ' }
        return if (phoneInput.isEmpty()) {
            textInputPhoneNumber!!.error = "Field can't be empty"
            false
        } else if (phoneInput.length != 9) {
            textInputPhoneNumber!!.error = "Please enter a valid phone number"
            false
        } else {
            textInputPhoneNumber!!.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val passwordInput = textInputPassword!!.editText!!.text.toString().trim { it <= ' ' }
        return if (passwordInput.isEmpty()) {
            textInputPassword!!.error = "Field can't be empty"
            false
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textInputPassword!!.error = "Password too weak. Use: 1 lower letter, 1 upper letter, 1 digit, at least 5 characters, no spaces"
            false
        } else {
            textInputPassword!!.error = null
            true
        }
    }

    private fun validatePasswordRepeat(): Boolean {
        val passwordRepeatInput = textInputPasswordRepeat!!.editText!!.text.toString().trim { it <= ' ' }
        val passwordInput = textInputPassword!!.editText!!.text.toString().trim { it <= ' ' }

        return if (passwordInput != passwordRepeatInput) {
            textInputPasswordRepeat!!.error = "The passwords entered do not match"
            false
        } else {
            textInputPasswordRepeat!!.error = null
            true
        }
    }

    fun confirmInput(v: View?) {
        if (!validateEmail() or !validatePhone() or !validatePassword() or !validatePasswordRepeat()) {
            return
        }
        var input = "Email: " + textInputEmail?.getEditText()?.getText().toString()
        input += "\n"
        input += "Phone Number: " + textInputPhoneNumber?.getEditText()?.getText().toString()
       // input += "\n"
      //  input += "Password: " + textInputPassword?.getEditText()?.getText().toString()
       // input += "\n"
       // input += "Password Repeat: " + textInputPasswordRepeat?.getEditText()?.getText().toString()
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        isCurrentUser()
    }

    private fun isCurrentUser() {
        fbAuth.currentUser?.let {auth ->
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
    }
}