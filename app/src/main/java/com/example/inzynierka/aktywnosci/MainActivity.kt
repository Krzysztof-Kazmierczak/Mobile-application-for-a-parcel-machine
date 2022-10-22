package com.example.inzynierka.aktywnosci

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.R
import com.example.inzynierka.data.User
import com.example.inzynierka.databinding.ActivityMainBinding
import com.example.inzynierka.fragmenty.Send.Send
import com.example.inzynierka.fragmenty.TakePack.TakepackFragment
import com.example.inzynierka.fragmenty.settings.SettingsFragment
import com.example.inzynierka.fragmenty.kurier.PickupPackFragment
import com.example.inzynierka.fragmenty.repository.FirebaseRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val fbAuth = FirebaseAuth.getInstance()
    private val repository = FirebaseRepository()
    var userDataMainActivity = MutableLiveData<User>()
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Aktualizajca tokenu uzytkownika w bazie danych
        pushNewToken()

        intent.extras?.getString("title")?.let { title ->
            Log.i("MyTag", "FROM notification $title")
        }
        //Sprawdzenie czy mamy zezwolenie wyslania SMS`a
        //Jak nie to prosimy o dostęp
        if (checkAndRequestPermissions()) {
        }

        userDataMainActivity = repository.getUserData() as MutableLiveData<User>
        userDataMainActivity.observe(this) { pack ->
            pack.access?.let { menuBar(it) }
        }

        replaceFragment(TakepackFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(SettingsFragment())
                R.id.takePack -> replaceFragment(TakepackFragment())
                R.id.sendPack -> replaceFragment(Send())
                R.id.logout -> logout()
                else -> {
                }
            }
            true
        }

        binding.bottomNavigationViewDeliwer.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(SettingsFragment())
                R.id.pickup -> replaceFragment(PickupPackFragment())
                R.id.takePack -> replaceFragment(TakepackFragment())
                R.id.sendPack -> replaceFragment(Send())
                R.id.logout -> logout()
                else -> {
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()

    }

    //Wyswietlanie odpowiedniego menu bottom Bar (dla kuriera i zwyklego uzytkownika)
    private fun menuBar(access:Int){
        if (access == 1)
        {
            binding.bottomNavigationViewDeliwer.visibility = View.VISIBLE
            binding.bottomNavigationView.visibility = View.INVISIBLE
        }else
        {
            binding.bottomNavigationViewDeliwer.visibility = View.INVISIBLE
            binding.bottomNavigationView.visibility = View.VISIBLE
        }
    }
    //Wpisanie do bazy danych zaktualizowanego tokenu uzytkownika
    private fun pushNewToken(){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(@NonNull task: Task<String?>) {
                    if (!task.isSuccessful()) {
                        println("Fetching FCM registration token failed")
                        return
                    }

                    // Get new FCM registration token
                    val token: String? = task.getResult()

                    // Log and toast
                    token?.let { Log.d("moj token ", it) }

                    repository.pushToken(token.toString())
                }
            })
    }
    //Wylogowanie uzytkownika
    private fun logout(){
        fbAuth.signOut()
        val AktywnoscPierwszeOkno: Intent = Intent(applicationContext, RegistrationActivity::class.java)
        startActivity(AktywnoscPierwszeOkno)
    }

    //Funkacja sprawdzająca czy może aplikacja wysyłać i odczytywać sms`y
    private fun checkAndRequestPermissions(): Boolean {
        val permissionSendMessage = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        )
        val locationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }
    //Zapytanie o pozwolenie na dostęp do SMS
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("Permission", "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()

                // Initialize the map with both permissions
                perms[Manifest.permission.SEND_SMS] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.RECEIVE_SMS] = PackageManager.PERMISSION_GRANTED

                // Fill with actual results from user
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }

                    // Check for both permissions
                    if (perms[Manifest.permission.SEND_SMS] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.RECEIVE_SMS] == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d("sms", "READ_SMS & RECEIVE_SMS services permission granted")

                        // process the normal flow

                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("Some", "Some permissions are not granted ask again ")

                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission

//                        // shouldShowRequestPermissionRationale will return true

                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.SEND_SMS
                            ) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.RECEIVE_SMS
                            )
                        ) {
                            showDialogOK("READ_SMS and RECEIVE_SMS Services Permission required for this app",
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE -> {
                                        }
                                    }
                                })
                        } else {

                            //  Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();

                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }
    //Logika zapytania o dostep do SMS
    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }
}









