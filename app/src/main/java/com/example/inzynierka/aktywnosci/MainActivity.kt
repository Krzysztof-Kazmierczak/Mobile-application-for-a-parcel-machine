package com.example.inzynierka.aktywnosci

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.inzynierka.constants.Constants.Companion.TOPIC
import com.example.inzynierka.databinding.ActivityMainBinding
import com.example.inzynierka.firebase.NotificationData
import com.example.inzynierka.firebase.PushNotification
import com.example.inzynierka.firebase.RetrofitInstance
import com.example.inzynierka.fragmenty.Send.Send
import com.example.inzynierka.fragmenty.home.HomeFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fm = supportFragmentManager
        val fragmentSend = Send()
        val HomeFragment = HomeFragment()

        Log.i("Powtorzenie", "1")

        intent.extras?.getString("title")?.let{ title ->
            Log.i("MyTag", "FROM notification $title")
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        Log.i("Powtorzenie", "1")
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("Permission", "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()

                Log.i("Powtorzenie", "1")
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

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
        Log.i("Powtorzenie", "1")
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

   /* private fun checkPermission(permission:String,requestcode:Int) {
        if (ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED){
            //Take Permision
            ActivityCompat.requestPermissions(this,arrayOf(permission),requestcode)
            }else
        {
            Toast.makeText(this,"Permission alredy",Toast.LENGTH_SHORT).show()
        }
    }*/

  /*  override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == SMS_SEND_PERMISSION){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Send correty",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Send wrong",Toast.LENGTH_SHORT).show()
            }
        }
    }*/


}

