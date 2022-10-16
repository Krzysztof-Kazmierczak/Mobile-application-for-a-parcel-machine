package com.example.inzynierka.fragmenty.kurier

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfirmPickupPack : Fragment() {

    private var _binding:  ConfirmSendFragmentBinding? = null
    private val binding get() = _binding!!
    private val ConfirmPickupPackVm: ConfirmPickupPackViewModel by viewModels()

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
        var size = "box"
        var numerIdBox = String()
        ConfirmPickupPackVm.boxId()
        numerIdBox = ConfirmPickupPackVm.numerBoxuPUP

        //Ponowne otworzenie skrytki
        binding.CSFNie.setOnClickListener {
            ConfirmPickupPackVm.openBox(size, numerIdBox)
            Toast.makeText(requireContext(), "Otwarto ponownie box " + numerIdBox, Toast.LENGTH_SHORT).show()
        }

        //Aktualizacja informacji o skrytce w ktorej byla paczka
        binding.CSFTak.setOnClickListener {
            ConfirmPickupPackVm.closeBox(size, numerIdBox)
            findNavController()
                        .navigate(ConfirmPickupPackDirections.actionConfirmPickupPackToHomeFragment().actionId)
        }
    }
    //Wyslanie sms do uzytkownika ze jego paczka zostala wyciagnieta z box`u
    private fun sendSMS(numberPH:String,numberPack:String,numberBox:String,dataOdbioru:String) {
        val tresc1 = "Twoja paczka o numerze id "
        val tresc2 = " znajduje sie w skrytce: "
        val tresc3 = " Czas na odebranie paczki: "
        //Tresc sms do uzytkownika (BRAK POLSKI ZNAKÓW!)
        val SMS = tresc1 + numberPack + tresc2 + numberBox + tresc3 + dataOdbioru
        // Wysłanie SMS`a
        var smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(numberPH,null,SMS,null,null)
        //Potwierdzenie na ekranie ze wysłaliśmy pomyślnie SMS`a
        Toast.makeText(requireContext(),"SMS został wysłany",Toast.LENGTH_SHORT).show()
    }
    //Notyfikacja na telefonie uzytkownika że jego paczka zostala wyciagnieta z box`u
    private fun notyfiactionFunctionSend(numberPack:String,numberBox:String,tokenUser:String,dataOdbioru:String){
        val tresc1 = "Twoja paczka o numerze id "
        val tresc2 = " znajduje sie w skrytce: "
        val tresc3 = " Czas na odebranie paczki: "
        //Treść wiadomości na notyfikacji
        val mess = tresc1 + numberPack + tresc2 + numberBox + tresc3 + dataOdbioru
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC)
        val notification = PushNotification(
            data = NotificationData("Otrzymano Paczkę", mess, 10, false),
            to = tokenUser)
        //Publikacja notyfikacji
        sendNotification(notification)
    }
    //Wysłanie Notyfikacji
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
}
