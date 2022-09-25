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
import com.example.inzynierka.aktywnosci.RegistrationActivity
import com.example.inzynierka.databinding.HomeFragmentBinding
import com.example.inzynierka.fragmenty.repository.FirebaseRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class HomeFragment : Fragment() {

    private val homeVm by viewModels<HomeViewModel>()
    private val fbAuth = FirebaseAuth.getInstance()
    private val repository = FirebaseRepository()
    private val fbCloud = FirebaseMessaging.getInstance()
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
       // networkConnectioCheck()
        Log.i("Powtorzenie", "1")
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

       // havePack()
      //  token()
    }

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

                    /*Toast.makeText(
                        this@MainActivity,
                        "Your device registration token is$token",
                        Toast.LENGTH_SHORT
                    ).show()*/

                }
            })
    }

    private fun havePack(){

            homeVm.getPackData("0001") //to 0001 wiem ze jest 0001 bo to wpisuje kurier i przychodzi z powiadomieniem
            homeVm.pack.observe(viewLifecycleOwner, { pack ->
               if (pack.packInBox.toString() == 1.toString()) {

                    val uidUser = pack.uid.toString()
                    homeVm.sendInfotoUser("0001",uidUser)
               }
            })
    }

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

                    /*Toast.makeText(
                        this@MainActivity,
                        "Your device registration token is$token",
                        Toast.LENGTH_SHORT
                    ).show()*/
                    repository.pushToken(token.toString())
                }
            })
    }

    private fun networkConnectioCheck(){

        Log.i("Powtorzenie", "2")
        val connect =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connect.activeNetworkInfo
        // if(.isNotEmpty()){
        if(networkInfo != null && networkInfo.isConnected)
        {
            binding.networkConnection.visibility = View.INVISIBLE
        }else
        {
            binding.networkConnection.visibility = View.VISIBLE
        }
       // networkConnectioCheck()
    }

    private fun setupTakePackClick() {
        Log.i("Powtorzenie", "3")
        binding.TakePack.setOnClickListener {
            findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToTakepackFragment().actionId)

        }
    }

    private fun homeFragmentScreen() {
        binding.homeFragmentScreen.setOnClickListener {
            networkConnectioCheck()
        }
    }

    private fun setupSendClick() {
        binding.Send.setOnClickListener {
            findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToSend().actionId)
        }
    }

    private fun setupPickUpPackClick() {
        binding.PickupPack.setOnClickListener {
            findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToPickupPackFragment().actionId)
        }
    }

    private fun setupLogoutClick() {
        Log.i("Powtorzenie", "4")
        binding.LogOut.setOnClickListener {
                fbAuth.signOut()
                val AktywnoscPierwszeOkno: Intent = Intent(context, RegistrationActivity::class.java)
                startActivity(AktywnoscPierwszeOkno)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("Powtorzenie", "5")
        _binding = null
    }

}