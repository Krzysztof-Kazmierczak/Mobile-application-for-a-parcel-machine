package com.example.inzynierka.fragmenty.potwierdzenie

import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inzynierka.R
import com.example.inzynierka.constants.Constants
import com.example.inzynierka.databinding.ConfirmSendFragmentBinding
import com.example.inzynierka.firebase.NotificationData
import com.example.inzynierka.firebase.PushNotification
import com.example.inzynierka.firebase.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.type.Date
import com.google.type.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.Calendar.getInstance


class ConfirmSend : Fragment() {

    private val Send_DEBUG = "Send_DEBUG"
    private var _binding:  ConfirmSendFragmentBinding? = null
    private val binding get() = _binding!!
    private val ConfirmSendVm: ConfirmSendViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ConfirmSendFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
        return inflater.inflate(R.layout.confirm_send_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var size = String()
        var numerIdBox = String()
        var numerIdPack = String()
        ConfirmSendVm.boxId()
        ConfirmSendVm.numberPack()

        numerIdBox = ConfirmSendVm.numerBoxu
        numerIdPack = ConfirmSendVm.numerPaczki
        if(numerIdBox.toInt()<6)
        {
            size = "box"
        }
        else
        {
            if(numerIdBox.toInt()<11)
            {
                size = "boxsM"
            }
            else
            {
                size = "boxsL"
            }
        }
        binding.CSFNie.setOnClickListener {
            ConfirmSendVm.editBoxData(size, numerIdBox, numerIdPack)
            Toast.makeText(requireContext(), "Otwarto ponownie box " + numerIdBox, Toast.LENGTH_SHORT).show()
        }

        binding.CSFTak.setOnClickListener {
            ConfirmSendVm.boxFull(size, numerIdBox)
            ConfirmSendVm.closeBox(size, numerIdBox)
            ConfirmSendVm.editPackData(numerIdPack ,numerIdBox)

            /*DateTime.getDefaultInstance()

            val time = Date(156)//.time + (1000*3600*24*3)
            //val time =  Date().time. times( + 1000*3600*24*3)//(3dni w milisekundach)

            val costam = Date(156)
            costam.before(Date())

            var calendar = Calendar.getInstance()
            var day = calendar.get(Calendar.DAY_OF_MONTH)
            var month = calendar.get(Calendar.MONTH)
            var year = calendar.get(Calendar.YEAR)

            */

            val cal = getInstance()
            cal.time
            cal[Calendar.DAY_OF_YEAR] = cal[Calendar.DAY_OF_YEAR] + 3
            val wyswietlanieDaty = SimpleDateFormat("dd-MM-yyyy",Locale.UK).format(cal.time)
            var day = cal.get(Calendar.DAY_OF_MONTH)
            var month = cal.get(Calendar.MONTH)
            var year = cal.get(Calendar.YEAR)

            ConfirmSendVm.addDatePack(day.toString(),(month+1).toString(),year.toString(),numerIdPack)
            ConfirmSendVm.addDateBox(day.toString(),(month+1).toString(),year.toString(),numerIdBox)

            ConfirmSendVm.getPackData(numerIdPack.toString().trim())
            ConfirmSendVm.packSend.observe(viewLifecycleOwner, {packListData ->
                val numberToSendInfo = packListData.phoneNumber.toString().trim()
                val numberIDPack = packListData.packID.toString().trim()
                val numberIDBox = packListData.Id_box.toString().trim()
                val numerUID = packListData.uid.toString().trim()

                sendSMS(numberToSendInfo,numberIDPack,numberIDBox,wyswietlanieDaty)

                ConfirmSendVm.getUser(numerUID)
                ConfirmSendVm.infoUser.observe(viewLifecycleOwner, { user ->

                    var paczkiUser = user.paczki
                    paczkiUser?.add(numberIDPack.toString())

                    ConfirmSendVm.editUserData(numerUID, paczkiUser!!)

                    notyfiactionFunctionSend(numberIDPack,numberIDBox,user.token.toString(),wyswietlanieDaty)



                    findNavController()
                        .navigate(ConfirmSendDirections.actionConfirmSendToHomeFragment().actionId)
                })
            })
        }
    }

    private fun sendSMS(numberPH:String,numberPack:String,numberBox:String,dataOdbioru:String) {
        val tresc1 = "Twoja paczka o numerze id "
        val tresc2 = " znajduje sie w skrytce: "
        val tresc3 = " Czas na odebranie paczki: "

        val SMS = tresc1 + numberPack + tresc2 + numberBox + tresc3 + dataOdbioru
        //val SMS = numberPack.toString() + numberBox.toString()
        var smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(numberPH,null,SMS,null,null)
        Log.d("To jest sms",SMS)
        Toast.makeText(requireContext(),"SMS został wysłany",Toast.LENGTH_SHORT).show()
    }

    private fun notyfiactionFunctionSend(numberPack:String,numberBox:String,tokenUser:String,dataOdbioru:String){
        val tresc1 = "Twoja paczka o numerze id "
        val tresc2 = " znajduje sie w skrytce: "
        val tresc3 = " Czas na odebranie paczki: "

        val mess = tresc1 + numberPack + tresc2 + numberBox + tresc3 + dataOdbioru
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC)
        val notification = PushNotification(
            data = NotificationData("Otrzymano Paczkę", mess, 10, false),
            to = tokenUser)
            //to = "eH3xSnpRR1qbBN2G1mDbo_:APA91bEWhrCAxRdOBuQAUr6_2fgdjuNe_NIYziPCBt8dqfFQ4zbQiv_dpbwlYEmib9fqg-Rjb7NBDKbxjVZavmU_B8Kj8wDBtoQfLi-MPu2v5sW5udZRuLXcvwOP0xyPz723HRZk7CxR")//TOPIC)
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
    // private fun sendSMS() {
     //   var phoneNo = 782054003.toString().trim()
       // var SMS = "no elo".trim()
  // var smsManager = SmsManager.getDefault()
   // smsManager.sendTextMessage(phoneNo,null,SMS,null,null)

   // Toast.makeText(requireContext(),"wyslano sms",Toast.LENGTH_SHORT).show()

    //}
}
