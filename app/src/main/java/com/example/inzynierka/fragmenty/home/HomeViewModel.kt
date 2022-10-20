package com.example.inzynierka.fragmenty.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.aktywnosci.BaseViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User

class HomeViewModel : BaseViewModel() {
    var pack = MutableLiveData<Pack>()
    var userData = MutableLiveData<User>()

    //Pobranie informacji o zalogowanym uzytkowniku
    fun getUserData(): LiveData<User>
    {
        userData = repository.getUserData() as MutableLiveData<User>

        return userData
    }
}