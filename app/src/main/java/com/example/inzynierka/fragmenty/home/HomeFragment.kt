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
                    //TODO odkomentowac
                    //repository.pushToken(token.toString())
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

    private fun visibleDeliverButton(){
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

    private fun setupTakePackClick() {
        Log.i("Powtorzenie", "3")
        binding.TakePack.setOnClickListener {
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frame_layout, TakepackFragment())
            fragmentTransaction?.commit()

            // findNavController()
            //    .navigate(HomeFragmentDirections.actionHomeFragmentToTakepackFragment().actionId)

        }
    }

    private fun homeFragmentScreen() {
        binding.homeFragmentScreen.setOnClickListener {
            networkConnectioCheck()
        }
    }

    private fun setupSendClick() {
        binding.Send.setOnClickListener {
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frame_layout, Send())
            fragmentTransaction?.commit()
            // findNavController()
            //   .navigate(HomeFragmentDirections.actionHomeFragmentToSend().actionId)
        }
    }

    private fun setupPickUpPackClick() {
        binding.PickupPack.setOnClickListener {
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frame_layout, PickupPackFragment())
            fragmentTransaction?.commit()
            //findNavController()
            //  .navigate(HomeFragmentDirections.actionHomeFragmentToPickupPackFragment().actionId)
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
        _binding = null
    }
}