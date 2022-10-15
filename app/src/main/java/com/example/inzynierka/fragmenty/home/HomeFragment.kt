package com.example.inzynierka.fragmenty.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inzynierka.R
import com.example.inzynierka.aktywnosci.RegistrationActivity
import com.example.inzynierka.databinding.HomeFragmentBinding
import com.example.inzynierka.fragmenty.Send.Send
import com.example.inzynierka.fragmenty.TakePack.TakepackFragment
import com.example.inzynierka.fragmenty.kurier.PickupPackFragment
import com.example.inzynierka.fragmenty.potwierdzenie.ConfirmSend
import com.example.inzynierka.fragmenty.repository.FirebaseRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class HomeFragment : Fragment() {

    private val homeVm by viewModels<HomeViewModel>()
    private val fbAuth = FirebaseAuth.getInstance()
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTakePackClick()
        setupSendClick()
        setupLogoutClick()
        networkConnectioCheck()
        homeFragmentScreen()
        setupPickUpPackClick()
        pushNewToken()
        //visibleDeliverButton()
        //token()
    }

    //Aktualizacja Tokena uzytkownika
    private fun token(){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(@NonNull task: Task<String?>) {
                    if (!task.isSuccessful()) {
                        println("Fetching FCM registration token failed")
                        return
                    }

                    // Get new FCM registration token
                    val token: String? = task.getResult()

                    // Log and toast
                    token?.let { Log.d("moj token ", it) }
                }
            })
    }

    //Wpisanie do bazy danych zaktualizowanego tokenu uzytkownika
    private fun pushNewToken()
    {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(@NonNull task: Task<String?>) {
                    if (!task.isSuccessful()) {
                        println("Fetching FCM registration token failed")
                        return
                    }

                    // Get new FCM registration token
                    val token: String? = task.getResult()

                    // Log and toast
                    token?.let { Log.d("moj token ", it) }
//TODO odkomentowac
                    //repository.pushToken(token.toString())
                }
            })
    }

    //Sprawdzanie polaczenia z internetem uzytkownika
    private fun networkConnectioCheck(){
        val connect =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connect.activeNetworkInfo
        if(networkInfo != null && networkInfo.isConnected)
        {
            binding.networkConnection.visibility = View.INVISIBLE
        }else
        {
            binding.networkConnection.visibility = View.VISIBLE
        }
    }

    //Sprawdzanie czy uzytkownik ma dostęp do opcji kuriera
    private fun visibleDeliverButton(){
        //Pobranie informacji o zalogowanym uzytkowniku
        homeVm.getUserData()
        homeVm.userData.observe(viewLifecycleOwner, { user ->
            val accesLVL = user.access
            if(accesLVL == 1)
            {
                binding.PickupPack.visibility = View.VISIBLE
            }else
            {
                binding.PickupPack.visibility = View.INVISIBLE
            }
        })
    }
    //Przejscie do fragmentu "moje paczki"
    private fun setupTakePackClick() {
        binding.TakePack.setOnClickListener {
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frame_layout, TakepackFragment())
            fragmentTransaction?.commit()
        }
    }
    //Przejście do fragmentu home fragment (aktualizacja tokenu)
    private fun homeFragmentScreen() {
        binding.homeFragmentScreen.setOnClickListener {
            networkConnectioCheck()
        }
    }
    //Przejście do fragmentu "wyślij paczkę"
    private fun setupSendClick() {
        binding.Send.setOnClickListener {
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frame_layout, Send())
            fragmentTransaction?.commit()
        }
    }
    //Przejście do fragmentu "wyjmij paczkę" (KURIER)
    private fun setupPickUpPackClick() {
        binding.PickupPack.setOnClickListener {
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frame_layout, PickupPackFragment())
            fragmentTransaction?.commit()
        }
    }
    //Wylogowanie uzytkownika
    private fun setupLogoutClick() {
        binding.LogOut.setOnClickListener {
            fbAuth.signOut()
            val AktywnoscPierwszeOkno: Intent = Intent(context, RegistrationActivity::class.java)
            startActivity(AktywnoscPierwszeOkno)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}