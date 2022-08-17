package com.example.inzynierka.fragmenty.TakePack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class TakepackViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    var idPackToMe = MutableLiveData<String>()
    //var idPacksToMe = MutableLiveData<ArrayList<String>>()
    var cloudResult = MutableLiveData<Pack>()
    var mypacks = MutableLiveData<List<Pack>>()
    //val user = repository.getUserData()

    val idPacksToMe = repository.packsToMe()


    fun packData(mojePaczki: List<String>){//}: MutableLiveData<List<Pack>> {
        mypacks = repository.getmyPacks(mojePaczki) as MutableLiveData<List<Pack>>
       // return mypacks
    }

   // fun packsToMe(): LiveData<ArrayList<String>>
    //{
      //  idPacksToMe = repository.packsToMe() as MutableLiveData<ArrayList<String>>

        //return idPacksToMe
    //}


    fun packData(id: String): LiveData<Pack> {
        cloudResult = repository.PutPack(id) as MutableLiveData<Pack>
        return cloudResult
    }

    fun openBox(size: String?, id: String?){
        size?.let{ it1 ->
            id?.let{ it2->
                repository.openBox(it1, it2)
            }
        }
    }
    fun getUserData(){
        repository.getUserData()
    }

}