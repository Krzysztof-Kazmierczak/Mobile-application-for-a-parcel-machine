package com.example.inzynierka.fragmenty.TakePack

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzynierka.R
import com.example.inzynierka.data.Pack
import com.example.inzynierka.databinding.TakeFragmentBinding
import com.example.inzynierka.fragmenty.home.MyPacksAdapter
import com.example.inzynierka.fragmenty.potwierdzenie.ConfirmTake

var boxIdTF = String()
var numerPaczkiGLTF = String()

class TakepackFragment : Fragment(){

    private var _binding: TakeFragmentBinding? = null
    private val binding get() = _binding!!
    private val TakepackVm by viewModels<TakepackViewModel>()

    private lateinit var adapter: MyPacksAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TakeFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
        return inflater.inflate(R.layout.take_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Sprawdzanie który "kafelek" wybraliśmy
        val connect =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connect.activeNetworkInfo
            binding.recyclerViewTakepack.layoutManager = LinearLayoutManager(requireContext())

            adapter = MyPacksAdapter { position ->
            //Pobieramy informacje z wybranego "kafelka" i przypisanie do zmiennych globalnych
                boxIdTF = TakepackVm.mypacks.value?.get(position)?.Id_box.toString()
                numerPaczkiGLTF = TakepackVm.mypacks.value?.get(position)?.packID.toString()
                if(networkInfo != null && networkInfo.isConnected) {
                    //Otwarcie boxu z paczką użytkownika
                    TakepackVm.openBox(TakepackVm.mypacks.value?.get(position)?.Size.toString(),boxIdTF)
                    //Przejście do fragmentu z potwierdzeniem wyciągnięcia paczki
                    val fragmentTransaction = fragmentManager?.beginTransaction()
                    fragmentTransaction?.replace(R.id.frame_layout, ConfirmTake())
                    fragmentTransaction?.commit()
                }
            }
            binding.recyclerViewTakepack.adapter = adapter
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Pobranie listy paczek użytkownika
        TakepackVm.idPacksToMe.observe(viewLifecycleOwner, { listMyPack ->
            //Sprawdzamy czy lista nie jest pusta
            if(listMyPack.isNotEmpty()){
                //Sprawdzamy połączenie internetowe
                networkConnectioCheck()
                //Jeżeli użytkownik ma paczki do odebrania "chowamy" komunikat o braku paczek
                binding.TPBrakPaczek.visibility = View.INVISIBLE
                //Pobieramy informacje o paczkach
                TakepackVm.packData(listMyPack)
                //Wyświetlamy za pomocą adaptera paczki w scrollview
                TakepackVm.mypacks.observe(viewLifecycleOwner, { list ->
                    adapter.setMyPacks(list as ArrayList<Pack>)
                })}
            else{
                //Sprawdzamy połączenie internetowe
                networkConnectioCheck()
            }
        })
    }
    //Funkacja sprawdzająca połączenie internetowe i ewntualnie wyświetla komunikat o braku połączenia
    private fun networkConnectioCheck(){
        val connect =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connect.activeNetworkInfo
        //Jeżeli mamy połaczenie internetowe "chowamy" napis o braku połączenia
        if(networkInfo != null && networkInfo.isConnected)
        {
            binding.networkConnection.visibility = View.INVISIBLE
            binding.TPBrakPaczek.visibility = View.VISIBLE
        }else
        //Jeżeli nie mamy połączenia wyświetlamy komunikat że nie ma połączenia z internetem
        {
            binding.networkConnection.visibility = View.VISIBLE
        }
    }
    //Wpisujemy w zmiennej globalnej id boxu
    fun getIdBox(): String {
        return boxIdTF
    }
    //Wpisujemy w zmiennej globlanej numer paczki
    fun getIdPack(): String {
        return numerPaczkiGLTF
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}