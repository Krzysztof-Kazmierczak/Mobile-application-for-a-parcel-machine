package com.example.inzynierka.fragmenty.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.inzynierka.R
import com.example.inzynierka.databinding.ActivityMainBinding
import com.example.inzynierka.databinding.ActivityMainBinding.inflate
import com.example.inzynierka.databinding.ActivityRegistrationBinding.inflate
import com.example.inzynierka.databinding.FragmentLoginBinding
import com.example.inzynierka.databinding.FragmentLoginBinding.inflate
import com.example.inzynierka.fragmenty.repository.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : BaseFragment() {

    private val fbAuth = FirebaseAuth.getInstance()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val LOG_DEUBG = "LOG_DEBUG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoginClick()
        setupRegistrationClick()


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRegistrationClick() {
        binding.RegisterButton2.setOnClickListener {
            findNavController()
                .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment().actionId)
        }
    }

    private fun setupLoginClick() {
        binding.LoginButton.setOnClickListener {
            val email = binding.LogMail.text?.trim().toString()
            val pass = binding.LogPass.text?.trim().toString()

            fbAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { authRes ->
                    if(authRes.user != null) startApp()
                }
                .addOnFailureListener{ exc ->
                    Snackbar.make(requireView(), "Błędny login lub hasło", Snackbar.LENGTH_SHORT)
                        .show()
                    Log.d(LOG_DEUBG, exc.message.toString())
                }
        }
    }
}