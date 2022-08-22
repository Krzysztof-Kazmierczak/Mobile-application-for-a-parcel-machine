package com.example.inzynierka.fragmenty.Send

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inzynierka.data.Box
import com.example.inzynierka.data.Pack
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class SendViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    //val user = repository.getUserData()
    var cloudResult = MutableLiveData<Pack>()
    //var cloudResultBox = MutableLiveData<String>()
    var cloudResultBoxS = MutableLiveData<String>()

    //Funkcja wywołująca funkcję z FirebaseReository która zwraca informacje o danej przesyłce
    fun putPack(id: String): LiveData<Pack> {
        cloudResult = repository.PutPack(id) as MutableLiveData<Pack>
        return cloudResult
    }

    fun findEmptyBoxS(size: String): LiveData<String> {

        cloudResultBoxS = repository.findEmptyBoxS(size) as MutableLiveData<String>

        return cloudResultBoxS
    }

    fun editBoxData(size: String,id: String, packID: String)
    {
        repository.editBoxData(size, id, packID)
    }

   // fun editUserData(Uid: String, packID: String)
   // {
        //repository.editUserData(Uid, packID)
    //}

}