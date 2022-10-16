package com.example.inzynierka.fragmenty.kurier

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class ConfirmPickupPackViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    var cloudResult = MutableLiveData<Pack>()
    private val pickupPackFragment = PickupPackFragment()
    var numerPaczki = String()
    var numerBoxuPUP = String()
    var packSend = MutableLiveData<Pack>()
    var infoUser = MutableLiveData<User>()

    //Pobranie ID box`u
    fun boxId(): String? {
        numerBoxuPUP = pickupPackFragment.getPUPIdBox()
        return numerBoxuPUP
    }
    //Pobranie informacji o paczce
    fun getPackData(Id_pack: String): LiveData<Pack>
    {
        packSend = repository.getPackData(Id_pack) as MutableLiveData<Pack>
        return packSend
    }
    //Zamknięcie Boxu (Zmiana stanu w bazie danych "OC" na "0"
    fun closeBox(size: String,id: String)
    {
        repository.closeBox(size, id)
    }
    //Wyslanie informacji do bazy dancyh że chcemy dany box otworzyć
    fun openBox(size: String,id: String)
    {
        repository.openBox(size, id)
    }
}