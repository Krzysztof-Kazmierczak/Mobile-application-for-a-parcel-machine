 package com.example.inzynierka.fragmenty.registration

import com.example.inzynierka.aktywnosci.BaseViewModel
import com.example.inzynierka.data.User

class RegistrationViewModel : BaseViewModel() {
    //Tworzenie w bazie dancyh nowego dokumentu dla użytkownika
    fun createNewUser(user: User){
        repository.createNewUser(user)
    }

}