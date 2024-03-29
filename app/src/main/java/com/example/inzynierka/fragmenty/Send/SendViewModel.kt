package com.example.inzynierka.fragmenty.Send

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.aktywnosci.BaseViewModel
import com.example.inzynierka.data.Pack

class SendViewModel : BaseViewModel() {

    var cloudResult = MutableLiveData<Pack>()
    var cloudResultBoxS = MutableLiveData<String>()

    //Funkcja wywołująca funkcję z FirebaseReository która zwraca informacje o danej przesyłce
    fun putPack(id: String): LiveData<Pack> {
        cloudResult = repository.PutPack(id) as MutableLiveData<Pack>
        return cloudResult
    }
    //Funkacja szukająca wolnego boxu dla naszej przesyłki
    fun findEmptyBoxS(size: String): LiveData<String> {
        cloudResultBoxS = repository.findEmptyBoxS(size) as MutableLiveData<String>
        return cloudResultBoxS
    }
    //Funkacja edytująca informacje boxu.
    fun editBoxData(size: String,id: String,packID:String)
    {
        repository.editBoxData(size, id, packID)
    }
}