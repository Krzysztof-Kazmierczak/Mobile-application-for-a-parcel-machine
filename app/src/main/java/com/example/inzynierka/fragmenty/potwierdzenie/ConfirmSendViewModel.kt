package com.example.inzynierka.fragmenty.potwierdzenie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User
import com.example.inzynierka.fragmenty.Send.Send
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class ConfirmSendViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    private val send = Send()
    var cloudResult = MutableLiveData<Pack>()
    var cloudResultBoxS = MutableLiveData<String>()
    var numerPaczki = String()
    var numerBoxu = String()
    var packSend = MutableLiveData<Pack>()
    var infoUser = MutableLiveData<User>()

    //Dodanie paczki do listy w bazie danych użytkownika
    fun addDatePack(day:String,month:String,year:String,packID:String)
    {
        repository.addDatePack(day,month,year,packID)
    }
    //Zaktualiowanie danych boxu
    fun addDateBox(day:String,month:String,year:String,boxID:String)
    {
        repository.addDateBox(day,month,year,boxID)
    }
    //Pobranie informacji o użytkowniku
    fun getUser(uid: String): LiveData<User> {
    infoUser = repository.infoUser(uid) as MutableLiveData<User>
    return infoUser
    }
    //Zaktualizowanie danych użytkownika
    fun editUserData(uid: String, paczki: ArrayList<String>)
    {
        repository.editUserData(uid, paczki)
    }
    //Pobranie informacji o ID paczki
    fun numberPack(): String {
        numerPaczki = send.getIdPack()
        return numerPaczki
    }
    //Pobranie informacji o numerze boxu
    fun boxId(): String? {
        numerBoxu = send.getIdBox()
        return numerBoxu
    }
    //Pobranie informacji o paczce
    fun getPackData(Id_pack: String): LiveData<Pack>
    {
        packSend = repository.getPackData(Id_pack) as MutableLiveData<Pack>
        return packSend
    }
    //Zaktualizowanie informacji o boxie
    fun editBoxData(size: String, id: String, idPack: String)
    {
        repository.editBoxData(size, id, idPack)
    }
    //Zaktualizowanie informacji o paczce
    fun editPackData(numerIdPack: String, numerIdBox: String)
    {
        repository.editPackData(numerIdPack,numerIdBox)
    }
    //"Zamknięcie" boxu
    fun closeBox(size: String,id: String)
    {
        repository.closeBox(size, id)
    }
    //Zaktualizowanie informacji boxu że jest zapełniony
    fun boxFull(size: String,id: String)
    {
        repository.editBoxFullData(size, id)
    }
}