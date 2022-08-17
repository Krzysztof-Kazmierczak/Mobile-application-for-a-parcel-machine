package com.example.inzynierka.fragmenty.potwierdzenie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.fragmenty.Send.Send
import com.example.inzynierka.fragmenty.TakePack.TakepackFragment
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class ConfirmTakeViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val takePack = TakepackFragment()
    //val user = repository.getUserData()
    var cloudResult = MutableLiveData<Pack>()
    //var cloudResultBox = MutableLiveData<String>()
    var cloudResultBoxS = MutableLiveData<String>()
    var numerPaczki = String()
    var numerBoxu = String()
    var isPack = MutableLiveData<Int>()

    val idPacksToMe = repository.packsToMe()


    fun boxId(): String {
        numerBoxu = takePack.getIdBox()
        return numerBoxu
    }

    fun upDataUser(nowaListaPaczekUzytkownika : ArrayList<String>)
    {
        repository.upDataUser(nowaListaPaczekUzytkownika)
    }

    fun upDataPack(numerIDPack: String)
    {
        repository.upDataPack(numerIDPack)
    }

    fun packId(): String {
        numerPaczki = takePack.getIdPack()
        return numerPaczki
    }

    fun openBoxCT(size: String,id: String)
    {
        repository.openBoxCT(size, id)
    }

    fun closeBox(size: String,id: String)
    {
        repository.closeBox(size, id)
    }

    fun boxEmpty(size: String,id: String)
    {
        repository.editBoxEmptyData(size, id)
    }
}