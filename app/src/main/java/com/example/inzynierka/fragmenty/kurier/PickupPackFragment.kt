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
            //todo pozycje srpawdzic z paczka!
            val boxID = boxAfterTime[position].ID_Box.toString() //PickupPackVm.endTimeBoxS.value?.get(position)?.ID_Box.toString()
            val packID = boxAfterTime[position].ID.toString() //PickupPackVm.endTimeBoxS.value?.get(position)?.ID.toString()
            //Adnotacja w bazie danych że paczka została wyjęta przez opóźnienie w odebraniu
            PickupPackVm.noteToPack(packID)
            if(networkInfo != null && networkInfo.isConnected) {
                //Otwarcie wybranego boxu z paczka po terminie
               //todo PickupPackVm.openBox(PickupPackVm.endTimeBoxS.value?.get(position)?.Size.toString(),boxID)
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
                        fragmentTransaction?.replace(R.id.frame_layout, SettingsFragment())
                        fragmentTransaction?.commit()
                    })
                })
               // })
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
        val tresc1 = "Twoja paczka o numerze id "
        val tresc2 = " zostala wyjeta ze skrytki: "
        val tresc3 = " Minal Twoj czas na odbioru paczki."
        //Tresc SMS`a
        val SMS = tresc1 + numberPack + tresc2 + numberBox + tresc3
        var smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(numberPH,null,SMS,null,null)
        Toast.makeText(requireContext(),"SMS został wysłany", Toast.LENGTH_SHORT).show()
    }
    //Publikacja notyfikacji że wyciągamy paczkę
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Sprawdzanie połączenia z internetem
        observeInternetConnection()
        //Czyszczenie listy
        boxAfterTime.clear()
        var liczbaPaczek = 0
        var listaPaczek = ArrayList<Int>()
        listaPaczek.clear()
        listaPaczek.add(0)
        listaPaczek.add(0)
        listaPaczek.add(0)
        listaPaczek.add(0)
        listaPaczek.add(0)
        listaPaczek.add(0)
        for (i in 1..5) {
            PickupPackVm.oneBoxInfo(i.toString())
            PickupPackVm.endTimeBox.observe(viewLifecycleOwner, { boxInfo ->
                if(boxInfo!=null){
                    if(listaPaczek[boxInfo.ID_Box!!.toInt()] != boxInfo.ID_Box?.toInt()){
                        listaPaczek[boxInfo.ID_Box?.toInt()!!] = boxInfo.ID_Box?.toInt()

                        if (boxInfo != null) {
                            liczbaPaczek = liczbaPaczek + 1
                            if (boxInfo.day!! != "") {
                            boxAfterTime.add(boxInfo)
                            }
                        }
                    }
                }
                if (liczbaPaczek == 5) {
                    if (boxAfterTime.isNotEmpty()) {
                        //wyswietlamy komunikat ze brak paczek
                        binding.PUPBrakPaczek.visibility = View.INVISIBLE
                        adapter.setEndTimePacks(boxAfterTime)
                    } else {
                        //wyswietlamy komunikat ze brak paczek
                        binding.PUPBrakPaczek.visibility = View.VISIBLE
                    }
                }
            })
        }
        /*
        // Pobranie informacji o tym czy skrytka nie jest juz po terminie... TODO poprawic to! N
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
                                    //wyswietlamy komunikat ze brak paczek
                                    binding.PUPBrakPaczek.visibility = View.INVISIBLE

                                    adapter.setEndTimePacks(boxAfterTime)
                                } else {
                                    //wyswietlamy komunikat ze brak paczek
                                    binding.PUPBrakPaczek.visibility = View.VISIBLE
                                }
                            }else {
                                if (boxAfterTime.isNotEmpty()) {
                                    //wyswietlamy komunikat ze brak paczek
                                    binding.PUPBrakPaczek.visibility = View.INVISIBLE
                                    adapter.setEndTimePacks(boxAfterTime)
                                } else {
                                    //wyswietlamy komunikat ze brak paczek
                                    binding.PUPBrakPaczek.visibility = View.VISIBLE
                                }
                            }
        })})})})})
    }*/
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}