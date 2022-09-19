package com.example.inzynierka.fragmenty.kurier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.data.BoxS
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class PickupPackViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    var idPackToMe = MutableLiveData<String>()
    var cloudResult = MutableLiveData<Pack>()
    val idPacksToUser = repository.packsToMe()

    var userInfo = MutableLiveData<User>()
    var packInfo = MutableLiveData<Pack>()

    var endTimeBoxS = MutableLiveData<BoxS>()


    fun oneBoxInfo(name: String): LiveData<BoxS> {
        endTimeBoxS = repository.getOneBoxInfo(name) as MutableLiveData<BoxS>
        return endTimeBoxS
    }

    fun packData(id: String): LiveData<Pack> {
        cloudResult = repository.PutPack(id) as MutableLiveData<Pack>
        return cloudResult
    }

    fun upDataUser(nowaListaPaczekUzytkownika : ArrayList<String>)
    {
        repository.upDataUser(nowaListaPaczekUzytkownika)
    }

    fun upDataPack(numerIDPack: String)
    {
        repository.upDataPack(numerIDPack)
    }

    fun closeBox(size: String,id: String)
    {
        repository.closeBox(size, id)
    }

    fun boxEmpty(size: String,id: String)
    {
        repository.editBoxEmptyData(size, id)
    }

    fun infoUser(uid: String): LiveData<User>{
        userInfo = repository.infoUser(uid) as MutableLiveData<User>
        return userInfo
    }

    fun infoPack(pack_id: String): LiveData<Pack>{
        packInfo = repository.getPackData(pack_id) as MutableLiveData<Pack>
        return packInfo
    }

    fun openBox(size: String?, id: String?){
        size?.let{ it1 ->
            id?.let{ it2->
                repository.openBox(it1, it2)
            }
        }
    }

}