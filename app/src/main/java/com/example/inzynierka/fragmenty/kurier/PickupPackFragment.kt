package com.example.inzynierka.fragmenty.kurier

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzynierka.R
import com.example.inzynierka.constants.Constants
import com.example.inzynierka.data.BoxS
import com.example.inzynierka.databinding.PickupPackFragmentBinding
import com.example.inzynierka.firebase.NotificationData
import com.example.inzynierka.firebase.PushNotification
import com.example.inzynierka.firebase.RetrofitInstance
import com.example.inzynierka.fragmenty.home.PickupPacksAdapter
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PickupPackFragment : Fragment() {
    private val PROFILE_DEBUG = "PROFILE_DEBUG"
    private var _binding: PickupPackFragmentBinding? = null
    private val binding get() = _binding!!
    private val PickupPackVm by viewModels<PickupPackViewModel>()

    private lateinit var adapter: PickupPacksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PickupPackFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
        return inflater.inflate(R.layout.pickup_pack_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val connect =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connect.activeNetworkInfo
        binding.recyclerViewPickuppack.layoutManager = LinearLayoutManager(requireContext())

        adapter = PickupPacksAdapter { position ->

            val boxID = PickupPackVm.endTimeBoxS.value?.get(position)?.ID_Box.toString()
            val packID = PickupPackVm.endTimeBoxS.value?.get(position)?.ID.toString()
            val size = PickupPackVm.endTimeBoxS.value?.get(position)?.Size.toString()

            if(networkInfo != null && networkInfo.isConnected) {
                PickupPackVm.openBox(PickupPackVm.endTimeBoxS.value?.get(position)?.Size.toString(),boxID)
                PickupPackVm.infoPack(packID)
                PickupPackVm.packInfo.observe(viewLifecycleOwner, { packDataInfo ->
                    PickupPackVm.infoUser(packDataInfo.uid.toString())
                    PickupPackVm.userInfo.observe(viewLifecycleOwner, { userDataInfo ->
                        PickupPackVm.idPacksToUser.observe(viewLifecycleOwner, { listUserPacks ->
                            var nowaListaPaczekUzytkownika = ArrayList<String>()

                            val liczbaPaczek = (listUserPacks.size) - 1
                            var idPaczek = listUserPacks.get(0)
                            for (i in 0..liczbaPaczek) {
                                idPaczek = listUserPacks.get(i)
                                if(packID != idPaczek)
                                {
                                    nowaListaPaczekUzytkownika.add(idPaczek)
                                }
                            }
                            PickupPackVm.upDataUser(nowaListaPaczekUzytkownika)
                        })

                        PickupPackVm.boxEmpty("box", boxID)

                        PickupPackVm.upDataPack(packID)

                        PickupPackVm.closeBox("box", boxID)

                        sendSMS(userDataInfo.phone.toString(),packID,boxID)
                        notyfiactionFunctionSend(packID,boxID,userDataInfo.token.toString())

                        findNavController()
                            .navigate(PickupPackFragmentDirections.actionPickupPackFragmentToHomeFragment())

                    })
                })
            }
        }

        binding.recyclerViewPickuppack.adapter = adapter
    }

    private fun sendSMS(numberPH:String,numberPack:String,numberBox:String) {
        val tresc1 = "Twoja paczka o numerze id "
        val tresc2 = " zostala wyjeta ze skrytki: "
        val tresc3 = " Minal Twoj czas na odbioru paczki."

        val SMS = tresc1 + numberPack + tresc2 + numberBox + tresc3
        //val SMS = numberPack.toString() + numberBox.toString()
        var smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(numberPH,null,SMS,null,null)
        Log.d("To jest sms",SMS)
        Toast.makeText(requireContext(),"SMS został wysłany", Toast.LENGTH_SHORT).show()
    }

    private fun notyfiactionFunctionSend(numberPack:String,numberBox:String,tokenUser:String){
        val tresc1 = "Twoja paczka o numerze id "
        val tresc2 = " zostala wyjeta ze skrytki: "
        val tresc3 = " Minal Twoj czas na odbioru paczki."

        val mess = tresc1 + numberPack + tresc2 + numberBox + tresc3
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC)
        val notification = PushNotification(
            data = NotificationData("Wyciagnieto paczke", mess, 10, false),
            to = tokenUser)
        sendNotification(notification)
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.i("MyTag", "Response ${Gson().toJson(response)}")
            } else{
                Log.i("MyTag", "Response ELSE ${response.message()}")
            }
        } catch (e: Exception){
            Log.i("MyTag", "Response Exception ${e.message}")
        }
    }

    private fun networkConnectioCheck(){
        val connect =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connect.activeNetworkInfo
        // if(.isNotEmpty()){
        if(networkInfo != null && networkInfo.isConnected)
        {
            binding.networkConnection.visibility = View.INVISIBLE
            binding.PUPBrakPaczek.visibility = View.VISIBLE
        }else
        {
            binding.networkConnection.visibility = View.VISIBLE
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        for (i in 1..5){
            val boxInfo = PickupPackVm.oneBoxInfo(i.toString())

        }


        PickupPackVm.endTimeBoxS.observe(viewLifecycleOwner, { listEndTimePack ->

            if(listEndTimePack.isNotEmpty()){
                networkConnectioCheck()
                binding.PUPBrakPaczek.visibility = View.INVISIBLE

                    adapter.setEndTimePacks(listEndTimePack as ArrayList<BoxS>)
            }
            else{
                networkConnectioCheck()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}