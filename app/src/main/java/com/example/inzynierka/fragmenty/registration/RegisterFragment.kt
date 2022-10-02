package com.example.inzynierka.fragmenty.registration

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import com.example.inzynierka.R
import com.example.inzynierka.databinding.FragmentRegistrationBinding
import com.example.inzynierka.fragmenty.registration.RegistrationViewModel
import com.example.inzynierka.fragmenty.repository.BaseFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern


@Suppress("UNREACHABLE_CODE")
class RegisterFragment : BaseFragment() {

    private var textInputEmail: TextInputLayout? = null
    private var textInputPhoneNumber: TextInputLayout? = null
    private var textInputPassword: TextInputLayout? = null
    private var textInputPasswordRepeat: TextInputLayout? = null

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val fbAuth = FirebaseAuth.getInstance()
    private val regVm by viewModels<RegistrationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)
        return binding.root

        val RegistrationMail = view?.findViewById<View>(R.id.RegMail) as TextInputLayout?
        val RegistrationPhoneNumber = view?.findViewById<View>(R.id.RegNumPhon)as TextInputLayout?
        val RegistrationPassword = view?.findViewById<View>(R.id.RegPass) as TextInputLayout?
        //val RegistrationPasswordRepeat = //view?.findViewById<View>(R.id.//RegPassRep) as TextInputLayout?
        textInputEmail = RegistrationMail
        textInputPhoneNumber = RegistrationPhoneNumber
        textInputPassword = RegistrationPassword
       // textInputPasswordRepeat = RegistrationPasswordRepeat
    }

    private fun validateEmail(): Boolean {
        val emailInput = textInputEmail!!.editText!!.text.toString().trim { it <= ' ' }
        if (emailInput.isEmpty()) {
            textInputEmail!!.error = "Field can't be empty"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail!!.error = "Please enter a valid email address"
            return false
        } else {
            textInputEmail!!.error = null
            return true
        }
    }





    companion object {
        private val PASSWORD_PATTERN = Pattern.compile(
            "^" +  //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +  //any letter
                    "(?=.*[@#$%^&+=])" +  //at least 1 special character
                    "(?=\\S+$)" +  //no white spaces
                    ".{4,}" +  //at least 4 characters
                    "$"
        )
    }
}