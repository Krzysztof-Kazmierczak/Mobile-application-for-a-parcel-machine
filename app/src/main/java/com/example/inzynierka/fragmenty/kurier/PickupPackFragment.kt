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
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzynierka.R
import com.example.inzynierka.constants.Constants
import com.example.inzynierka.data.BoxS
import com.example.inzynierka.databinding.PickupPackFragmentBinding
import com.example.inzynierka.firebase.NotificationData
import com.example.inzynierka.firebase.PushNotification
import com.example.inzynierka.firebase.RetrofitInstance
import com.example.inzynierka.fragmenty.settings.SettingsFragment
import com.example.inzynierka.fragmenty.settings.PickupPacksAdapter
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

var PUP_boxId = String()
// Tworzenie listy box`ów które są po terminie
var boxAfterTime = ArrayList<BoxS>()

class PickupPackFragment : Fragment() {

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
        //Sprawdzanie który "kafelek" wybraliśmy
        val connect =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connect.activeNetworkInfo
        binding.recyclerViewPickuppack.layoutManager = LinearLayoutManager(requireContext())
        adapter = PickupPacksAdapter { position ->
            //Pobieramy informacje z wybranego "kafelka"
            val boxID = boxAfterTime[position].ID_Box.toString() //PickupPackVm.endTimeBoxS.value?.get(position)?.ID_Box.toString()
            val packID = boxAfterTime[position].ID.toString() //PickupPackVm.endTimeBoxS.value?.get(position)?.ID.toString()
            val size = boxAfterTime[position].Size.toString()
            //Adnotacja w bazie danych że paczka została wyjęta przez opóźnienie w odebraniu
            PickupPackVm.noteToPack(packID)
            if(networkInfo != null && networkInfo.isConnected) {
                //Otwarcie wybranego boxu z paczka po terminie
               PickupPackVm.openBox(size,boxID)
                //Pobranie informacji o paczce
                PickupPackVm.infoPack(packID)
                PickupPackVm.packInfo.observe(viewLifecycleOwner, { packDataInfo ->
                    //Pobranie informacji o uzytkowniku (odbiorcy paczki)
                    PickupPackVm.infoUser(packDataInfo.uid.toString())
                    PickupPackVm.userInfo.observe(viewLifecycleOwner, { userDataInfo ->
                        //PickupPackVm.infoUserPacks(userDataInfo.uid.toString())
                       // PickupPackVm.idPacksToUser.observe(viewLifecycleOwner, { listUserPacks ->
                        //Przypisanie do zmiennej listy paczek uzytkownika
                        val listUserPacks = userDataInfo.paczki

                        //Stworzenie i umieszczenie w bazie danych nowej listy paczek uzytkownika (usunięcie tej którą wyjęliśmy)
                        var nowaListaPaczekUzytkownika = ArrayList<String>()
                        val liczbaPaczek = (listUserPacks?.size)?.minus(1)
                        var idPaczek = listUserPacks?.get(0)
                        for (i in 0..liczbaPaczek!!) {
                            idPaczek = listUserPacks?.get(i)
                            if(packID != idPaczek)
                            {
                                nowaListaPaczekUzytkownika.add(idPaczek)
                            }
                        }
                        //Przypisanie w bazie danych nowej listy paczek użytkownika
                        PickupPackVm.upDataUser(nowaListaPaczekUzytkownika)
                        //Ustawienie w bazie danych że box jest już dostępny/pusty
                        PickupPackVm.boxEmpty("box", boxID)
                        PUP_boxId = boxID
                        //Zaktualizowanie informacji paczki.
                        PickupPackVm.upDataPack(packID)
                        //"Zamkniecie" box
                        PickupPackVm.closeBox("box", boxID)
                        //Sprawdzenie czy użytkownik chce otrzymywać notyfikacje
                        if (userDataInfo.permitNotyfication == 1){
                            //Wywołanie Funkcji wyświetlającą notyfikację na tel odbiorcy paczki
                            notyfiactionFunctionSend(packID,boxID,userDataInfo.token.toString())
                        }
                        //Sprawdzenie czy użytkownik chce otrzymywać SMS
                        if (userDataInfo.permitSMS == 1){
                            //Wywołanie funkcji wysyłającej SMS`a
                            sendSMS(userDataInfo.phone.toString(),packID,boxID)
                        }
                        val fragmentTransaction = fragmentManager?.beginTransaction()
                        fragmentTransaction?.replace(R.id.frame_layout, PickupPackFragment())
                        fragmentTransaction?.commit()
                    })
                })
            }
        }
        binding.recyclerViewPickuppack.adapter = adapter
    }
    //Sprawdzanie połączenia z internetem
    override fun onResume() {
        super.onResume()
        PickupPackVm.checkInternetConnection(requireActivity().application)
    }
    //Sprawdzanie połączenia z internetem
    private fun observeInternetConnection(){
        PickupPackVm.isConnectedToTheInternet.observe(viewLifecycleOwner){
            it?.let{
                binding.networkConnection.visibility = if(it) View.GONE else View.VISIBLE

                //wyswietlamy komunikat ze brak paczek jezeli nie ma dostepu do internetu
                binding.PUPBrakPaczek.visibility = if(it) View.GONE else View.VISIBLE
            }
        }
    }
    //Wyslanie SMS do użytkownika że paczka została wyjęta
    private fun sendSMS(numberPH:String,numberPack:String,numberBox:String) {
        val tresc1 = context?.getResources()?.getString(R.string.SMSyourPackNumberID) + " "
        val tresc2 = " " + context?.getResources()?.getString(R.string.SMSwasTakenFromBox) + " "
        val tresc3 = context?.getResources()?.getString(R.string.SMStimeEnd) + " "
        //Tresc SMS`a
        val SMS = tresc1 + numberPack + tresc2 + numberBox + tresc3
        var smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(numberPH,null,SMS,null,null)
        Toast.makeText(requireContext(),context?.getResources()?.getString(R.string.LOGsmsWasSend), Toast.LENGTH_SHORT).show()
    }
    //Publikacja notyfikacji że wyciągamy paczkę
    private fun notyfiactionFunctionSend(numberPack:String,numberBox:String,tokenUser:String){
        val tresc1 = context?.getResources()?.getString(R.string.SMSyourPackNumberID) + " "
        val tresc2 = " " + context?.getResources()?.getString(R.string.SMSwasTakenFromBox) + " "
        val tresc3 = context?.getResources()?.getString(R.string.SMStimeEnd) + " "

        val mess = tresc1 + numberPack + tresc2 + numberBox + tresc3
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC)
        val notification = PushNotification(
            data = NotificationData(context?.getResources()?.getString(R.string.LOGPackWasTaken).toString(), mess, 10, false),
            to = tokenUser)
        sendNotification(notification)
    }
    //Wysłanie notyfikacji że wyciągamy paczkę
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
    //Wyświetlanie paczek po terminie //todo N
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Sprawdzanie połączenia z internetem
        observeInternetConnection()
        //Czyszczenie listy
        boxAfterTime.clear()
        var answerFunction = "0"
        PickupPackVm.getBoxData(1.toString())
        PickupPackVm.boxInfo.observe(viewLifecycleOwner, { boxInfo1 ->
            answerFunction = boxCheckTime(boxInfo1)
            if (answerFunction != "0") {
                boxAfterTime.add(boxInfo1)
            }
            PickupPackVm.getBoxData(2.toString())
            PickupPackVm.boxInfo.observe(viewLifecycleOwner, { boxInfo2 ->
                answerFunction = boxCheckTime(boxInfo2)
                if (answerFunction != "0") {
                    boxAfterTime.add(boxInfo2)
                }
                PickupPackVm.getBoxData(3.toString())
                PickupPackVm.boxInfo.observe(viewLifecycleOwner, { boxInfo3 ->
                    answerFunction = boxCheckTime(boxInfo3)
                    if (answerFunction != "0") {
                        boxAfterTime.add(boxInfo3)
                    }
                    PickupPackVm.getBoxData(4.toString())
                    PickupPackVm.boxInfo.observe(viewLifecycleOwner, { boxInfo4 ->
                        answerFunction = boxCheckTime(boxInfo4)
                        if (answerFunction != "0") {
                            boxAfterTime.add(boxInfo4)
                        }
                        PickupPackVm.getBoxData(5.toString())
                        PickupPackVm.boxInfo.observe(viewLifecycleOwner, { boxInfo5 ->
                            answerFunction = boxCheckTime(boxInfo5)
                            if (answerFunction != "0") {
                                boxAfterTime.add(boxInfo5)
                            }
                            if (boxAfterTime.isNotEmpty()) {
                                //wyswietlamy komunikat ze brak paczek
                                binding.PUPBrakPaczek.visibility = View.INVISIBLE
                                adapter.setEndTimePacks(boxAfterTime)
                            } else {
                                //wyswietlamy komunikat ze brak paczek
                                binding.PUPBrakPaczek.visibility = View.VISIBLE
                            }
                        })
                    })
                })
            })
        })
    }
    //Funkcja sprawdza czy paczka jest po terminie. Jeżeli jest po terminie funkcja zwraca w stringu
    //numer boxu w którym jest paczka po terminie, jeżeli jest termin funkcja zwraca 0 - stringowe
    fun boxCheckTime(box: BoxS): String {
        var backInfo = "0"
        val boxID = box.ID_Box.toString()
        val cal = Calendar.getInstance()
        cal.time
        cal[Calendar.DAY_OF_MONTH]
        val day = cal[Calendar.DAY_OF_MONTH].toString()
        val month = (cal[Calendar.MONTH] + 1).toString()
        val year = cal[Calendar.YEAR].toString()
        if (box != null) {
            if (box.day!! != "") {
                if (box.year!! < year) {
                    Log.d(
                        boxID + " skrytka jest po terminie",
                        box.day.toString()
                    )
                    backInfo = boxID
                } else if (box.year!! == year && box.month!!.toInt() < month.toInt()) {
                    Log.d(
                        boxID + " skrytka jest po terminie",
                        box.day.toString()
                    )
                    backInfo = boxID
                } else if (box.year!! == year && box.month!! == month && box.day!! <= day) {
                    Log.d(
                        boxID + " skrytka jest po terminie",
                        box.day.toString()
                    )
                    backInfo = boxID
                } else {
                    Log.d(boxID + " jest jeszcze termin ", "TERMIN")

                }
                Log.d(boxID + " nie ma żadnej paczki ", "TERMIN")

            }
            Log.d(boxID + " nie ma żadnej paczki ", "TERMIN")
        }
        return backInfo
    }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }