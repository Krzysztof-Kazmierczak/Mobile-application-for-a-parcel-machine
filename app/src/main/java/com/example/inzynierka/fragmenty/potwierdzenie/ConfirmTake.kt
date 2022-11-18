package com.example.inzynierka.fragmenty.potwierdzenie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.inzynierka.R
import com.example.inzynierka.databinding.ConfirmTakeFragmentBinding
import com.example.inzynierka.fragmenty.TakePack.TakepackFragment

class ConfirmTake : Fragment() {
    //Fragment potwierdzajacy wyciagniecie naszej paczki z boxu
    private var _binding:  ConfirmTakeFragmentBinding? = null
    private val binding get() = _binding!!
    private val ConfirmTakeVm by viewModels<ConfirmTakeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ConfirmTakeFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
        return inflater.inflate(R.layout.confirm_take_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Sprawdzanie połączenia z internetem
        observeInternetConnection()

        var size = String()
        var numerIdBox = String()
        var numerIdPaczki = String()
        //Pobranie i przypisanie do zmiennych informacji o tym w jakim boxie jest paczka o naszym numerze
        ConfirmTakeVm.boxId()
        ConfirmTakeVm.packId()
        numerIdBox = ConfirmTakeVm.numerBoxu
        numerIdPaczki = ConfirmTakeVm.numerPaczki
        //Sprawdzenie jakiego rozmiaru jest paczka (NIEUŻYWANE)
        if(numerIdBox.toInt()<6)
        {
            size = "box"
        }
        else
        {
            if(numerIdBox.toInt()<11)
            {
                size = "boxsM"
            }
            else
            {
                size = "boxsL"
            }
        }
        //Wykrycie użycia przycisku "otwórz ponownie". Funkcja ponownie otwiera nasz box
        binding.CTFNie.setOnClickListener {
            ConfirmTakeVm.openBox(size, numerIdBox)
            Toast.makeText(requireContext(), R.string.LOGReopenBox.toString() + numerIdBox, Toast.LENGTH_SHORT).show()
        }
        //Wykrycie użycia przycisku potwierdzającego zakończenie odbierania paczki
        binding.CTFTak.setOnClickListener {
            //Pobranie informacji o paczkach użytkownika (Lista)
            ConfirmTakeVm.idPacksToMe.observe(viewLifecycleOwner, { listMyPack ->
                var nowaListaPaczekUzytkownika = ArrayList<String>()
                //Sprawdzenie ile paczek znajduje się na naszej liście i odjęcie tej którą użytkownik właśnie wyciągnął
                val liczbaPaczek = (listMyPack.size) - 1
                var idPaczek = listMyPack.get(0)
                for (i in 0..liczbaPaczek) {
                        //Szukanie paczki którą wyciągneliśmy
                        idPaczek = listMyPack.get(i)
                        if(numerIdPaczki != idPaczek)
                        {
                            //Tworzenie nowej listy bez paczki którą użytkownik właśnie wyciągnął
                            nowaListaPaczekUzytkownika.add(idPaczek)
                        }
                }
                //Zaktualizowanie w bazie danych listy paczek użytkownika
                ConfirmTakeVm.upDataUser(nowaListaPaczekUzytkownika)
                })
            //Zaktualizowanie informacji boxu że jest wolny/pusty
            ConfirmTakeVm.boxEmpty(size, numerIdBox)
            //Zaktualizowanie informacji paczki, że została wyjęta z boxu
            ConfirmTakeVm.upDataPack(numerIdPaczki)
            //"Zamknięcie" boxu
            ConfirmTakeVm.closeBox(size, numerIdBox)
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frame_layout, TakepackFragment())
            fragmentTransaction?.commit()
        }
    }
    //Sprawdzanie połączenia z internetem
    override fun onResume() {
        super.onResume()
        ConfirmTakeVm.checkInternetConnection(requireActivity().application)
    }
    //Sprawdzanie połączenia z internetem
    private fun observeInternetConnection(){
        ConfirmTakeVm.isConnectedToTheInternet.observe(viewLifecycleOwner){
            it?.let{
                binding.networkConnection.visibility = if(it) View.GONE else View.VISIBLE
            }
        }
    }
}