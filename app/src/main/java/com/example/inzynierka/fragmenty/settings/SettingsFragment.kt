package com.example.inzynierka.fragmenty.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Switch
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.R
import com.example.inzynierka.data.User
import com.example.inzynierka.databinding.SettingsFragmentBinding
import com.example.inzynierka.fragmenty.Send.Send
import com.example.inzynierka.fragmenty.repository.FirebaseRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging


class SettingsFragment : Fragment() {

    private val settingsVm by viewModels<SettingsViewModel>()
    private val repository = FirebaseRepository()
    var userSettings = MutableLiveData<User>()
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        //Wyświetlanie jaki język ma być używany w aplikacji
        val languages = resources.getStringArray(R.array.languageSelection)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,languages)
        binding.languageSelection.setAdapter(arrayAdapter)

        //Sprawdzanie połączenia internetowego
        settingsVm.checkInternetConnection(requireActivity().application)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pushNewToken()
        observeInternetConnection()
        setUserNotyficatio()
        offNotyfication()
        offSMS()
        darkMode()
        userInfo()
    }
    //Funkcja włącza i wyłacza darkmod + zapis w bazie danych todo sprawdzic czy to jest git...czemu samo przeskakuje do innego fragmentu n
    private fun darkMode(){
        val switchDarkMode = binding.switchDarkMode
        switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                settingsVm.darkMode(1)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                settingsVm.darkMode(0)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }
        }
    }
    //Sprawdzenie i wyswietlenie wyborow uzytkownika todo sprawdzic czy to jest git n
    private fun setUserNotyficatio(){
        settingsVm.userData.observe(viewLifecycleOwner, { user ->
            if(user.permitNotyfication == 1){
                binding.switchNotyfication.isChecked = true
            }else{
                binding.switchNotyfication.isChecked = false
            }
            if(user.permitSMS == 1){
                binding.switchSMS.isChecked = true
            }else{
                binding.switchSMS.isChecked = false
            }
            if(user.darkMode == 1){
                binding.switchDarkMode.isChecked = true
            }else{
                binding.switchDarkMode.isChecked = false
            }
        })
    }
    //Funkcja wyłączająca wysyłanie notyfikacji
    private fun offNotyfication(){

        val switchNotyfication = binding.switchNotyfication
        switchNotyfication.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                settingsVm.notyficationPermit(1)
            } else {
                settingsVm.notyficationPermit(0)
            }
        }
    }
    //Funkcja wyłączająca wysyłanie notyfikacji
    private fun offSMS(){
        val switchSMS = binding.switchSMS
        switchSMS.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                settingsVm.smsPermit(1)
            } else {
                settingsVm.smsPermit(0)
            }
        }
    }
    //Sprawdzanie połączenia z internetem
    private fun observeInternetConnection(){
        settingsVm.isConnectedToTheInternet.observe(viewLifecycleOwner){
            it?.let{
                binding.networkConnection.visibility = if(it) View.GONE else View.VISIBLE
            }
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
    //Wyswietlenie podstawowych informacji o uzytkowniku
    private fun userInfo(){
        settingsVm.userData.observe(viewLifecycleOwner, { user ->
            val mailUser = user.email
            val phoneUser = user.phone
            binding.userPhone.setText(phoneUser)
            binding.userMail.setText(mailUser)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}