package com.example.inzynierka.fragmenty.registration

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.inzynierka.R
import com.example.inzynierka.databinding.FragmentLoginBinding
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
//        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSignUpClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSignUpClick() {
        binding.CreateAccButton.setOnClickListener {
            val email = binding.RegMail.text?.trim().toString()
            val pass = binding.RegPass.text?.trim().toString()
            val repeatPass = binding.RegPassRep.text?.trim().toString()
            val phoneNumber = binding.RegNumPhon.text?.trim().toString()
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
}