package com.example.inzynierka.fragmenty.kurier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.data.BoxS
import com.example.inzynierka.data.Pack
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class PickupPackViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    var idPackToMe = MutableLiveData<String>()
    var cloudResult = MutableLiveData<Pack>()
    val idPacksToMe = repository.packsToMe()
    var endTimeBoxS = MutableLiveData<List<BoxS>>()

    fun packData(id: String): LiveData<Pack> {
        cloudResult = repository.PutPack(id) as MutableLiveData<Pack>
        return cloudResult
    }

    fun getUserData(){
        repository.getUserData()
    }

    fun endTimeBoxs(){//}: MutableLiveData<List<Pack>> {
        endTimeBoxS = repository.getEndTimePacks() as MutableLiveData<List<BoxS>>
        // return mypacks
    }
}