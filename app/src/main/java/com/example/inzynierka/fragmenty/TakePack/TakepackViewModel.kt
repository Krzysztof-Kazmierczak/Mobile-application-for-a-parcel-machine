package com.example.inzynierka.fragmenty.TakePack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class TakepackViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    var idPackToMe = MutableLiveData<String>()
    var cloudResult = MutableLiveData<Pack>()
    //val user = repository.getUserData()

    fun packToMe(): LiveData<String>
    {
        idPackToMe = repository.packToMe() as MutableLiveData<String>

        return idPackToMe
    }

    fun packData(id: String): LiveData<Pack> {
        cloudResult = repository.PutPack(id) as MutableLiveData<Pack>
        return cloudResult
    }

    fun openBox(size: String, id: String){
        repository.openBox(size, id)

    }

}