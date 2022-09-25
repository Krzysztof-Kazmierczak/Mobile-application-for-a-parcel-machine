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

var PUP_boxId = String()

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

            PickupPackVm.noteToPack(packID)

            if(networkInfo != null && networkInfo.isConnected) {
                PickupPackVm.openBox(PickupPackVm.endTimeBoxS.value?.get(position)?.Size.toString(),boxID)
                PickupPackVm.infoPack(packID)
                PickupPackVm.packInfo.observe(viewLifecycleOwner, { packDataInfo ->
                    PickupPackVm.infoUser(packDataInfo.uid.toString())
                    PickupPackVm.userInfo.observe(viewLifecycleOwner, { userDataInfo ->
                        PickupPackVm.infoUserPacks(userDataInfo.uid.toString())
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


                        PickupPackVm.boxEmpty("box", boxID)
                        PUP_boxId = boxID
                        PickupPackVm.upDataPack(packID)

                        PickupPackVm.closeBox("box", boxID)

                        sendSMS(userDataInfo.phone.toString(),packID,boxID)
                        notyfiactionFunctionSend(packID,boxID,userDataInfo.token.toString())

                        findNavController()
                            .navigate(PickupPackFragmentDirections.actionPickupPackFragmentToConfirmPickupPack())
                        })
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

        var boxAfterTime = ArrayList<BoxS>()
        var ileSkrytek = 0

        PickupPackVm.oneBoxInfo(1.toString())
        PickupPackVm.endTimeBox.observe(viewLifecycleOwner, { boxInfo01 ->
            if (boxInfo01 != null)
            {
                boxAfterTime.add(boxInfo01)
               // boxInfo01 = null
            }
            PickupPackVm.oneBoxInfo(2.toString())
            PickupPackVm.endTimeBox.observe(viewLifecycleOwner, { boxInfo02 ->
                if (boxInfo02 != null)
                {
                    boxAfterTime.add(boxInfo02)
                }
            PickupPackVm.oneBoxInfo(3.toString())
                PickupPackVm.endTimeBox.observe(viewLifecycleOwner, { boxInfo03 ->
                    if (boxInfo03 != null)
                    {
                        boxAfterTime.add(boxInfo03)
                    }
            PickupPackVm.oneBoxInfo(4.toString())
                    PickupPackVm.endTimeBox.observe(viewLifecycleOwner, { boxInfo04 ->
                        if (boxInfo04 != null)
                        {
                            boxAfterTime.add(boxInfo04)
                        }

            PickupPackVm.oneBoxInfo(5.toString())
                        PickupPackVm.endTimeBox.observe(viewLifecycleOwner, { boxInfo05 ->
                            if (boxInfo05 != null)
                            {
                                boxAfterTime.add(boxInfo05)
                                if (boxAfterTime.isNotEmpty()) {
                                    networkConnectioCheck()
                                    binding.PUPBrakPaczek.visibility = View.INVISIBLE

                                    adapter.setEndTimePacks(boxAfterTime)
                                } else {
                                    networkConnectioCheck()
                                }
                            }else {
                                if (boxAfterTime.isNotEmpty()) {
                                    networkConnectioCheck()
                                    binding.PUPBrakPaczek.visibility = View.INVISIBLE

                                    adapter.setEndTimePacks(boxAfterTime)
                                } else {
                                    networkConnectioCheck()
                                }
                            }
        })})})})})

      /*  if(boxAfterTime.isNotEmpty()){
            networkConnectioCheck()
            binding.PUPBrakPaczek.visibility = View.INVISIBLE

            adapter.setEndTimePacks(boxAfterTime)
        }
        else{
            networkConnectioCheck()
        }*/

       // PickupPackVm.endTimeBoxS.observe(viewLifecycleOwner, { listEndTimePack ->

            /*if(boxAfterTime.isNotEmpty()){
                networkConnectioCheck()
                binding.PUPBrakPaczek.visibility = View.INVISIBLE

                    adapter.setEndTimePacks(listEndTimePack as ArrayList<BoxS>)
            }
            else{
                networkConnectioCheck()
            }*/
        //})
    }


    fun getPUPIdBox(): String{
        Log.d("To zwracam", PUP_boxId)
        return PUP_boxId
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}