package com.example.inzynierka.fragmenty.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.inzynierka.databinding.FragmentLoginBinding
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
    //Przejście do fragmentu z tworzeniem nowego użytkownika
    private fun setupRegistrationClick() {
        binding.RegisterButton2.setOnClickListener {
            findNavController()
                .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment().actionId)
        }
    }
    //Próba zalogowania do aplikacji
    private fun setupLoginClick() {
        binding.LoginButton.setOnClickListener {
            //Pobranie informacji z pól tekstowych
            val email = binding.LogMail.text?.trim().toString()
            val pass = binding.LogPass.text?.trim().toString()
            //Wysłanie do bazy danych czy login i hasło są prawidłowe
            fbAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { authRes ->
                    //Jeżeli wszystko się zgadza logujemy się do aplikacji
                    if(authRes.user != null) startApp()
                }
                .addOnFailureListener{ exc ->
                    //Jeżeli występuje błąd wyświetlamy komunikat o błędzie użytkownikowi
                    Snackbar.make(requireView(), "Błędny login lub hasło", Snackbar.LENGTH_SHORT)
                        .show()
                    Log.d(LOG_DEUBG, exc.message.toString())
                }
        }
    }
}