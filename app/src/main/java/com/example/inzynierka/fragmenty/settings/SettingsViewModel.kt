package com.example.inzynierka.fragmenty.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.aktywnosci.BaseViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User

class SettingsViewModel : BaseViewModel() {
    var pack = MutableLiveData<Pack>()
    var userData = repository.getUserData() as MutableLiveData<User>

    //Funkcja zmieniajaca zezwolenie uzytkownika o wysyłanie do niego notyfikacji
    fun notyficationPermit(permit: Int){
        repository.notyficationPermit(permit)
    }
    //Funkcja zmieniajaca zezwolenie uzytkownika o wysyłanie do niego notyfikacji
    fun smsPermit(permit: Int){
        repository.smsPermit(permit)
    }
}