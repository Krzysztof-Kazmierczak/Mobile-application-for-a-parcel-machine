package com.example.inzynierka.aktywnosci

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.inzynierka.R
import com.example.inzynierka.constants.Constants.Companion.TOPIC
import com.example.inzynierka.databinding.ActivityMainBinding
import com.example.inzynierka.databinding.HomeFragmentBinding
import com.example.inzynierka.firebase.NotificationData
import com.example.inzynierka.firebase.PushNotification
import com.example.inzynierka.firebase.RetrofitInstance
import com.example.inzynierka.fragmenty.Send.Send
import com.example.inzynierka.fragmenty.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fm = supportFragmentManager
        val fragmentSend = Send()
        val HomeFragment = HomeFragment()

        intent.extras?.getString("title")?.let{ title ->
            Log.i("MyTag", "FROM notification $title")
        }


        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        val notification = PushNotification(
            data = NotificationData("Otrzymano Paczkę", "Twoja paczka znajduje się w skrytce numer 3.", 10, false),
            to = TOPIC)
        sendNotification(notification)
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.i("MyTag", "Response ${Gson().toJson(response)}")
            } else{
                Log.i("MyTag", "Response ELSE ${response.message()}")
            }
        } catch (e: Exception){
            Log.i("MyTag", "Response Exception ${e.message}")
        }
    }


}

