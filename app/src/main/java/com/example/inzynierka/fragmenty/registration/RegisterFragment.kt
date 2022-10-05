package com.example.inzynierka.fragmenty.registration
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.fragment.app.viewModels
import com.example.inzynierka.databinding.FragmentRegistrationBinding
import com.example.inzynierka.fragmenty.repository.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : BaseFragment() {
    private val REG_DEBUG = "REG_DEBUG"
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // setupSignUpClick()
        confirmInput()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSignUpClick() {

        binding.buttonCreate.setOnClickListener {

            val email = binding.RegMail.toString()
            val pass = binding.RegPass.toString()
            val repeatPass = binding.RegPassRep.toString()
            val phoneNumber = binding.RegNumPhon.toString()
            if (phoneNumber != "")
            {
                if(pass==repeatPass) {
                    fbAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnSuccessListener { authRes ->
                            if (authRes.user != null) {
                                val user = com.example.inzynierka.data.User(
                                    authRes.user!!.uid,
                                    "",
                                    "",
                                    arrayListOf(""),
                                    authRes.user!!.email,
                                    "",
                                    phoneNumber,
                                    0
                                )
                                regVm.createNewUser(user)
                                startApp()
                            }
                        }
                        .addOnFailureListener { exc ->
                            Snackbar.make(
                                requireView(),
                                "Upss...Something went wrong...",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                            Log.d(REG_DEBUG, exc.message.toString())
                        }
                }
            }else{
                Snackbar.make(
                    requireView(),
                    "Upss...Must pass phone number...",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun validateEmail(): Boolean {

       return if (binding.RegMail.editText.toString() != 1.toString()) {
           binding.RegMail.helperText = "Field can't be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.RegMail.toString()).matches()) {
           binding.RegMail.helperText = "Please enter a valid email address"
            false
        } else {
           binding.RegMail.helperText = null
            true
        }
    }

    fun confirmInput() {
        binding.buttonCreate.setOnClickListener {
            if (!validateEmail() /*or !validatePhone() or !validatePassword() or !validatePasswordRepeat()*/) {
                return@setOnClickListener
            }
            var input = "Email: " + binding.RegMail?.getEditText()?.getText().toString()
            input += "\n"
            input += "Phone Number: " + binding.RegNumPhon?.getEditText()?.getText().toString()
            // input += "\n"
            //  input += "Password: " + textInputPassword?.getEditText()?.getText().toString()
            // input += "\n"
            // input += "Password Repeat: " + textInputPasswordRepeat?.getEditText()?.getText().toString()
            Toast.makeText(requireContext(), input, Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun validatePhone(): Boolean {
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
    }*/
}