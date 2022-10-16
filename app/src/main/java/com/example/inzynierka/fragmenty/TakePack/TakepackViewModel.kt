package com.example.inzynierka.fragmenty.TakePack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class TakepackViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    var cloudResult = MutableLiveData<Pack>()
    var mypacks = MutableLiveData<List<Pack>>()
    val idPacksToMe = repository.packsToMe()

    //Zaktualizowanie listy paczek użytkownika
    fun packData(mojePaczki: List<String>){
        mypacks = repository.getmyPacks(mojePaczki) as MutableLiveData<List<Pack>>
    }
    //Pobranie z bazy dancyh informacji o paczkach
    fun packData(id: String): LiveData<Pack> {
        cloudResult = repository.PutPack(id) as MutableLiveData<Pack>
        return cloudResult
    }
    //"Otwieramy" box
    fun openBox(size: String?, id: String?){
        size?.let{ it1 ->
            id?.let{ it2->
                repository.openBox(it1, it2)
            }
        }
    }
}