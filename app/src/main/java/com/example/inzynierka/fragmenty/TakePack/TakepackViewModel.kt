package com.example.inzynierka.fragmenty.TakePack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.aktywnosci.BaseViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User

class TakepackViewModel : BaseViewModel() {

    var cloudResult = MutableLiveData<Pack>()
    var mypacks = MutableLiveData<List<Pack>>()
    val idPacksToMe = repository.packsToMe()
    var userData = MutableLiveData<User>()

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