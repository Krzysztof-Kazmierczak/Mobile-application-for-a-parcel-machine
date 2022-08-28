package com.example.inzynierka.fragmenty.potwierdzenie

import android.util.Log
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

    //val user = repository.getUserData()
    var cloudResult = MutableLiveData<Pack>()
    //var cloudResultBox = MutableLiveData<String>()
    var cloudResultBoxS = MutableLiveData<String>()
    var numerPaczki = String()
    var numerBoxu = String()
    var packSend = MutableLiveData<Pack>()
    var infoUser = MutableLiveData<User>()
   // val idPacksToMe =
    //var isPack = MutableLiveData<Int>()

    //fun ConfirmButton(): LiveData<Pack> {

      //  cloudResult = repository.PutPack() as MutableLiveData<Pack>
     //   return cloudResult
   // }


//    private var _numerBoxu = MutableLiveData<String>()
//    val numerBoxu: LiveData<String> = _numerBoxu
//
//    fun setNumberId(id: String){
//        _numerBoxu.postValue(id)
//    }
//

    fun addDatePack(day:String,month:String,year:String,packID:String)
    {
        repository.addDatePack(day,month,year,packID)
    }

    fun addDateBox(day:String,month:String,year:String,boxID:String)
    {
        repository.addDateBox(day,month,year,boxID)
    }

    fun getUser(uid: String): LiveData<User> {
    infoUser = repository.infoUser(uid) as MutableLiveData<User>
    return infoUser
    }


    fun numberPack(): String {
        numerPaczki = send.getIdPack()

        return numerPaczki
    }

    fun editUserData(uid: String, paczki: ArrayList<String>)
    {
        repository.editUserData(uid, paczki)
    }


    fun boxId(): String? {
        numerBoxu = send.getIdBox()
//        _numerBoxu.value = send.getIdBox()
        return numerBoxu
//        return _numerBoxu.value
    }



    fun getPackData(Id_pack: String): LiveData<Pack>
    {
        packSend = repository.getPackData(Id_pack) as MutableLiveData<Pack>

        return packSend
    }

    fun editBoxData(size: String, id: String, idPack: String)
    {
        repository.editBoxData(size, id, idPack)
    }

    fun editPackData(numerIdPack: String, numerIdBox: String)
    {
        repository.editPackData(numerIdPack,numerIdBox)
    }

    fun closeBox(size: String,id: String)
    {
        repository.closeBox(size, id)
    }

    fun boxFull(size: String,id: String)
    {
        repository.editBoxFullData(size, id)
    }

    fun findEmptyBoxS(size: String): LiveData<String> {

        cloudResultBoxS = repository.findEmptyBoxS(size) as MutableLiveData<String>

        return cloudResultBoxS
    }


}