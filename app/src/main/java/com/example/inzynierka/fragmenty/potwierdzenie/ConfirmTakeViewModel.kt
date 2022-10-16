package com.example.inzynierka.fragmenty.potwierdzenie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.fragmenty.TakePack.TakepackFragment
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class ConfirmTakeViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val takePack = TakepackFragment()
    var cloudResult = MutableLiveData<Pack>()
    var cloudResultBoxS = MutableLiveData<String>()
    var numerPaczki = String()
    var numerBoxu = String()
    val idPacksToMe = repository.packsToMe()
    //Pobranie informacji o numerze boxu w której jest paczka użytkownika
    fun boxId(): String {
        numerBoxu = takePack.getIdBox()
        return numerBoxu
    }
    //Zaktualizowanie informacji o liście paczek użytkownika
    fun upDataUser(nowaListaPaczekUzytkownika : ArrayList<String>)
    {
        repository.upDataUser(nowaListaPaczekUzytkownika)
    }
    //Zaktualizowanie informacji o paczce
    fun upDataPack(numerIDPack: String)
    {
        repository.upDataPack(numerIDPack)
    }
    //Pobranie informacji o numerze paczki którą użytkownik wyjął z boxu
    fun packId(): String {
        numerPaczki = takePack.getIdPack()
        return numerPaczki
    }
    //"Otwarcie" boxu
    fun openBoxCT(size: String,id: String)
    {
        repository.openBoxCT(size, id)
    }
    //"Zamknięcie" boxu
    fun closeBox(size: String,id: String)
    {
        repository.closeBox(size, id)
    }
    //Ustawienie boxu na pusty/wolny
    fun boxEmpty(size: String,id: String)
    {
        repository.editBoxEmptyData(size, id)
    }
}