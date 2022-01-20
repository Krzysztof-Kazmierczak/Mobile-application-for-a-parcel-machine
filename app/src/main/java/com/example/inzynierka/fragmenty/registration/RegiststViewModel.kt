package com.example.inzynierka.fragmenty.registration

import androidx.lifecycle.ViewModel
import com.example.inzynierka.data.User
import com.example.inzynierka.fragmenty.repository.FirebaseRepository

class RegistrationViewModel: ViewModel() {
    private val repository = FirebaseRepository()

    fun createNewUser(user: User){
        repository.createNewUser(user)
    }

}