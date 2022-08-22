package com.example.inzynierka.data

data class User(val uid:String? = null,
                val name: String? = null,
                val surname: String? = null,
                val paczki: ArrayList<String>? = null,
                val email: String? = null,
                val toMePackID: String? = null,
                val phone: String? = null,
                val check: Int? = null,
                val token: String? = null)
