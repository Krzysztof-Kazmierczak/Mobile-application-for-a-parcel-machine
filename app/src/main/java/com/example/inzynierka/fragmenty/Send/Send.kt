package com.example.inzynierka.fragmenty.Send

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.inzynierka.R
import com.example.inzynierka.databinding.SendFragmentBinding
import com.example.inzynierka.fragmenty.potwierdzenie.ConfirmSend

// wspólny viewModel, datastore/sharePrefereces, callback, navargs / intent.bundle (putString()) todo wytlumaczenie N
var boxId = String()
var numerPaczkiGL = String()

class Send : Fragment() {

    private var _binding: SendFragmentBinding? = null
    private val binding get() = _binding!!
    private val SendVm by viewModels<SendViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SendFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
        return inflater.inflate(R.layout.send_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PutPack()
        //Sprawdzanie połączenia z internetem
        observeInternetConnection()
    }
    //Sprawdzanie połączenia z internetem
    override fun onResume() {
        super.onResume()
        SendVm.checkInternetConnection(requireActivity().application)
    }
    //Sprawdzanie połączenia z internetem
    private fun observeInternetConnection(){
        SendVm.isConnectedToTheInternet.observe(viewLifecycleOwner){
            it?.let{
                binding.networkConnection.visibility = if(it) View.GONE else View.VISIBLE
            }
        }
    }

    // Funkcja szukajaca wolnej skrytki do ktorej mozemy umiescic wysylaną paczkę
    private fun PutPack(){
        //Sprawdzenie czy użytkownik klikną przycisk "wyslij paczkę"
        binding.SendUmiesPaczke.setOnClickListener {
            //Pobranie wpisanego tekstu przez uzytkownika
            val numerPaczki = binding.SendWpiszNumer.text?.trim().toString()
            //Sprawdzamy czy zostalo cos wpisane
            if (numerPaczki != "") {
                //Przypisanie informacji do zmiennej globalnej (POPRAWIC TO!) todo N
                numerPaczkiGL = numerPaczki
                //Pobranie informacji o naszej paczce
                SendVm.putPack(numerPaczki)
                SendVm.cloudResult.observe(viewLifecycleOwner, { pack ->
                    //Sprawdzenie czy paczka istnieje
                    if (pack != null) {
                        //Sprawdzenie rozmiaru paczki. (NIEUŻYWANE)
                        if (pack.Size == 1.toString()) {
                            //Szukamy wolnego boxu w bazie danych
                            SendVm.findEmptyBoxS("box")
                            SendVm.cloudResultBoxS.observe(viewLifecycleOwner, { idBoxS ->
                                //Sprawdzamy czy został znaleziony wolny box
                                if (idBoxS != null) {
                                    //Wyświetlamy informacje użytkownikowi
                                    Toast.makeText(requireContext(), idBoxS, Toast.LENGTH_SHORT).show()
                                    //Przypisanie informacji do zmiennej globalnej (POPRAWI TO!) todo N
                                    boxId = idBoxS.toString()
                                    //Zaktualizowanie informacji boxu
                                    SendVm.editBoxData("box", idBoxS, numerPaczki)
                                    //Zamiana fragmentu na potwierdzenie nadania paczki
                                    val fragmentTransaction = fragmentManager?.beginTransaction()
                                    fragmentTransaction?.replace(R.id.frame_layout, ConfirmSend())
                                    fragmentTransaction?.commit()
                                } else {
                                    //Jeżeli nie ma wolnych box`ów wyświetlamy komunikat użytkownikowi
                                    //todo dodac angielski tekst (string)
                                    Toast.makeText(requireContext(),"Wszystkie małe skrytki są zajęte!",Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else {}
                    } else {
                        //Jeżeli użytkownik wpisał numer paczki której nie ma w bazie danych wyświetlamy komunikat
                        //todo dodac angielski tekst (string)
                        Toast.makeText(requireContext(),"Nie ma takiej paczki w bazie danych",Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
    // Funkcja udostepniajaca do innych fragmentow numer paczki który został wpisany w polu "numer paczki"
    fun getIdPack(): String{
        return numerPaczkiGL
    }
    // Funkcja udostepniajaca do innych fragmentow w jakim boxie bedzie paczka
    fun getIdBox(): String{
        return boxId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}