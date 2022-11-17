package com.example.inzynierka.fragmenty.kurier

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.aktywnosci.BaseViewModel
import com.example.inzynierka.data.BoxS
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User

class PickupPackViewModel : BaseViewModel() {

    var cloudResult = MutableLiveData<Pack>()
    var boxInfo = MutableLiveData<BoxS>()
    var userInfo = MutableLiveData<User>()
    var packInfo = MutableLiveData<Pack>()

    //Pobranie informacji o paczce
    fun packData(id: String): LiveData<Pack> {
        cloudResult = repository.PutPack(id) as MutableLiveData<Pack>
        return cloudResult
    }
    //Zaktualizowanie w bazie danych nowej listy paczek uzytkownika
    fun upDataUser(nowaListaPaczekUzytkownika : ArrayList<String>)
    {
        repository.upDataUser(nowaListaPaczekUzytkownika)
    }
    //Pobranie informacji o danym boxie
    fun getBoxData(boxNumber : String) : LiveData<BoxS>
    {
        boxInfo = repository.getBoxData(boxNumber) as MutableLiveData<BoxS>
        return boxInfo
    }
    //Zaktualizowanie informacji o paczce
    fun upDataPack(numerIDPack: String)
    {
        repository.upDataPack(numerIDPack)
    }
    //"Zamknięcie" Boxu
    fun closeBox(size: String,id: String)
    {
        repository.closeBox(size, id)
    }
    //Ustawienie Boxu na pusty
    fun boxEmpty(size: String,id: String)
    {
        repository.editBoxEmptyData(size, id)
    }
    //Pobranie informacji o uzytkowniku
    fun infoUser(uid: String): LiveData<User>{
        userInfo = repository.infoUser(uid) as MutableLiveData<User>
        return userInfo
    }
    //Pobranie informacji o paczce
    fun infoPack(pack_id: String): LiveData<Pack>{
        packInfo = repository.getPackData(pack_id) as MutableLiveData<Pack>
        return packInfo
    }
    //Wysłanie informacji do bazy danych że należy otworzyć dany box
    fun openBox(size: String?, id: String?){
        size?.let{ it1 ->
            id?.let{ it2->
                repository.openBox(it1, it2)
            }
        }
    }
    //Funkcja zapisujaca informacje w paczce ze zostala wyjeta poniewaz minal czas na jej odebranie
    fun noteToPack(idPack:String){
        repository.notePack(idPack)
    }
}