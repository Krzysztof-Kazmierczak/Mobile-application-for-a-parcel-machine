package com.example.inzynierka.fragmenty.kurier

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User
import com.example.inzynierka.fragmenty.Send.Send
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class ConfirmPickupPackViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    //val user = repository.getUserData()
    var cloudResult = MutableLiveData<Pack>()
    //var cloudResultBox = MutableLiveData<String>()
    private val pickupPackFragment = PickupPackFragment()
    var cloudResultBoxS = MutableLiveData<String>()
    var numerPaczki = String()
    var numerBoxuPUP = String()
    var packSend = MutableLiveData<Pack>()
    var infoUser = MutableLiveData<User>()

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

    fun editUserData(uid: String, paczki: ArrayList<String>)
    {
        repository.editUserData(uid, paczki)
    }


    fun boxId(): String? {
        numerBoxuPUP = pickupPackFragment.getPUPIdBox()
        return numerBoxuPUP
    }

    fun getPackData(Id_pack: String): LiveData<Pack>
    {
        packSend = repository.getPackData(Id_pack) as MutableLiveData<Pack>

        return packSend
    }

    fun editPackData(numerIdPack: String, numerIdBox: String)
    {
        repository.editPackData(numerIdPack,numerIdBox)
    }

    fun closeBox(size: String,id: String)
    {
        repository.closeBox(size, id)
    }

    fun openBox(size: String,id: String)
    {
        repository.openBox(size, id)
    }

    fun boxFull(size: String,id: String)
    {
        repository.editBoxFullData(size, id)
    }
}