 package com.example.inzynierka.fragmenty.registration

import androidx.lifecycle.ViewModel
import com.example.inzynierka.aktywnosci.BaseViewModel
import com.example.inzynierka.data.User
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class RegistrationViewModel : BaseViewModel() {
    //Tworzenie w bazie dancyh nowego dokumentu dla użytkownika
    fun createNewUser(user: User){
        repository.createNewUser(user)
    }

}