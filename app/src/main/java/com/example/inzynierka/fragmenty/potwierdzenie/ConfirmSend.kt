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
import com.example.inzynierka.R
import com.example.inzynierka.constants.Constants
import com.example.inzynierka.databinding.ConfirmSendFragmentBinding
import com.example.inzynierka.firebase.NotificationData
import com.example.inzynierka.firebase.PushNotification
import com.example.inzynierka.firebase.RetrofitInstance
import com.example.inzynierka.fragmenty.Send.Send
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.getInstance

class ConfirmSend : Fragment() {

    private var _binding:  ConfirmSendFragmentBinding? = null
    private val binding get() = _binding!!
    private val ConfirmSendVm: ConfirmSendViewModel by viewModels()
    //Tutaj ustawiamy ile dni ma użytkownik na odebranie paczki
    val userTimeToTakePack = 3

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
        //Sprawdzanie połączenia z internetem
        observeInternetConnection()

        var size = String()
        var numerIdBox = String()
        var numerIdPack = String()
        //Pobranie i zapisanie do zmiennych informacji VM o id boxu i o numerze paczki
        ConfirmSendVm.boxId()
        ConfirmSendVm.numberPack()
        numerIdBox = ConfirmSendVm.numerBoxu
        numerIdPack = ConfirmSendVm.numerPaczki
        //Sprawdzenie wielkosci boxu(NIEUŻYWANE W TYM MOMENCIE)
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
        //Po naciśnieciu przycisku "Otwórz ponownie" wywołujemy funkacjię otwierającą box.
        binding.CSFNie.setOnClickListener {
            ConfirmSendVm.editBoxData(size, numerIdBox, numerIdPack)
            Toast.makeText(requireContext(), "Otwarto ponownie box " + numerIdBox, Toast.LENGTH_SHORT).show()
        }
        //Po wykryciu przycisku o zakończeniu procesu wysyłania paczki
        binding.CSFTak.setOnClickListener {
            //Ustawienie że box jest zajęty
            ConfirmSendVm.boxFull(size, numerIdBox)
            //Ustawienie że box jest zamknięty
            ConfirmSendVm.closeBox(size, numerIdBox)
            //Zaktualizowanie informacji paczki. (W jakim boxie się znajduje paczka)
            ConfirmSendVm.editPackData(numerIdPack ,numerIdBox)
            //Pobranie informacji o czasie
            val cal = getInstance()
            cal.time
            //Dodanie 3 dni do aktualnego dnia (CZAS NA ODBIÓR PACZKI)
            cal[Calendar.DAY_OF_YEAR] = cal[Calendar.DAY_OF_YEAR] + userTimeToTakePack
            val wyswietlanieDaty = SimpleDateFormat("dd-MM-yyyy",Locale.UK).format(cal.time)
            var day = cal.get(Calendar.DAY_OF_MONTH)
            var month = cal.get(Calendar.MONTH)
            var year = cal.get(Calendar.YEAR)
            //Dodanie do paczki i do boxu informacji do kiedy jest termin wyjęcia paczki
            //+1 bo miesiące są zapisywane od 0. żeby w bazie danych się nie myliło jaki to miesiąc
            ConfirmSendVm.addDatePack(day.toString(),(month+1).toString(),year.toString(),numerIdPack)
            ConfirmSendVm.addDateBox(day.toString(),(month+1).toString(),year.toString(),numerIdBox)
            //Pobranie informacji o paczce
            ConfirmSendVm.getPackData(numerIdPack.toString().trim())
            ConfirmSendVm.packSend.observe(viewLifecycleOwner, {packListData ->
                //Przypisanie do zmiennych najwazniejszych informacji paczki
                val numberToSendInfo = packListData.phoneNumber.toString().trim()
                val numberIDPack = packListData.packID.toString().trim()
                val numberIDBox = packListData.Id_box.toString().trim()
                val numerUID = packListData.uid.toString().trim()

                //Wysłanie SMS że paczka została do nas wysłana
                sendSMS(numberToSendInfo,numberIDPack,numberIDBox,wyswietlanieDaty)

                //Pobranie informacji o użytkowniku do którego wysłana jest paczka
                ConfirmSendVm.getUser(numerUID)
                ConfirmSendVm.infoUser.observe(viewLifecycleOwner, { user ->
                    //Dodanie do listy paczek użytkownika nowej paczki
                    var paczkiUser = user.paczki
                    paczkiUser?.add(numberIDPack.toString())
                    ConfirmSendVm.editUserData(numerUID, paczkiUser!!)
                    //Sprawdzenie czy użytkownik chce otrzymywać notyfikacje
                    if (user.permitNotyfication == 1){
                        //Wyświetlenie notyfikacji na tel użytkownika (odbioryc paczki)
                        notyfiactionFunctionSend(numberIDPack,numberIDBox,user.token.toString(),wyswietlanieDaty)
                    }
                    val fragmentTransaction = fragmentManager?.beginTransaction()
                    fragmentTransaction?.replace(R.id.frame_layout, Send())
                    fragmentTransaction?.commit()
                })
            })
        }
    }
    //Sprawdzanie połączenia z internetem
    override fun onResume() {
        super.onResume()
        ConfirmSendVm.checkInternetConnection(requireActivity().application)
    }
    //Sprawdzanie połączenia z internetem
    private fun observeInternetConnection(){
        ConfirmSendVm.isConnectedToTheInternet.observe(viewLifecycleOwner){
            it?.let{
                binding.networkConnection.visibility = if(it) View.GONE else View.VISIBLE
            }
        }
    }

    //Funkcja wysyłająca sms`a do użytkownika że odstał paczkę
    private fun sendSMS(numberPH:String,numberPack:String,numberBox:String,dataOdbioru:String) {
        val tresc1 = "Twoja paczka o numerze id "
        val tresc2 = " znajduje sie w skrytce: "
        val tresc3 = " Czas na odebranie paczkio: "
        //Treść SMS do użytkownika
        val SMS = tresc1 + numberPack + tresc2 + numberBox + tresc3 + dataOdbioru
        //Wysłanie SMS`a
        var smsManager = SmsManager.getDefault()

        //Wywołanie wysłania sms`a
        smsManager.sendTextMessage(numberPH,null,SMS,null,null)
       //Potwierdzenie na ekranie, że SMS został wysłany
        Toast.makeText(requireContext(),"SMS został wysłany",Toast.LENGTH_SHORT).show()
    }
    //Funkacja wyświetlająca notyfikacje na tel odbiorcy paczki że otrzymał paczkę
    private fun notyfiactionFunctionSend(numberPack:String,numberBox:String,tokenUser:String,dataOdbioru:String){
        val tresc1 = "Twoja paczka o numerze id "
        val tresc2 = " znajduje sie w skrytce: "
        val tresc3 = " Czas na odebranie paczki: "
        //Treść wiadomości notyfikacji (BEZ POLSKICH ZNAKÓW!)
        val mess = tresc1 + numberPack + tresc2 + numberBox + tresc3 + dataOdbioru
        //Wysyłanie notyfikacji
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC)
        val notification = PushNotification(
            data = NotificationData("Otrzymano Paczkę", mess, 10, false),
            to = tokenUser)
        sendNotification(notification)
    }
    //Funkacjia wysyłająca notyfikacje
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
