package com.example.inzynierka.fragmenty.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.aktywnosci.BaseViewModel
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User

class SettingsViewModel : BaseViewModel() {
    var pack = MutableLiveData<Pack>()
}