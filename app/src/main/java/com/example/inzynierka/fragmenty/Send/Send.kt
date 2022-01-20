package com.example.inzynierka.fragmenty.Send

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzynierka.R
import com.example.inzynierka.aktywnosci.MainActivity
import com.example.inzynierka.data.Box
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User
import com.example.inzynierka.databinding.SendFragmentBinding
import com.example.inzynierka.databinding.TakepackFragmentBinding
import com.example.inzynierka.fragmenty.TakePack.TakepackViewModel
import com.example.inzynierka.fragmenty.home.HomeFragmentDirections
import com.example.inzynierka.fragmenty.repository.BaseFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay

var boxId = String()
var numerPaczkiGL = String()

class Send : Fragment() {

    private val Send_DEBUG = "Send_DEBUG"
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
    }

    //Funkcja która się wykonuje w momencie wpisania id paczki i wciśnięciu przycisku "Umieść paczkę"
    private fun PutPack(){
        binding.SendUmiesPaczke.setOnClickListener {
            //Pobieramy stringa wpisanego przez użytkownika (powinien to być numer ID paczki)

            val numerPaczki = binding.SendWpiszNumer.text?.trim().toString()
            numerPaczkiGL = numerPaczki
            //Wywołujemy wynkjcę, która szuka paczkę o danym ID i zapisuje jej wynik w cloudResult

            SendVm.putPack(numerPaczki)
            SendVm.cloudResult.observe(viewLifecycleOwner,{pack->
                //Sprawdzamy czy przesyłka o takim ID znajduje się w naszej bazie danych
                if(pack!=null)
                {
                    //Wyświetlamy informacje o paczce z wpisanym ID
                    bindPackInfo(pack)
                    //Sprawdzamy czy rozmiar paczki o danym id jest 1,2 czy 3. (mała, średnia, duża)
                      if(pack.Size == 1.toString())
                      {
                          Log.d("To jest mała paczka",pack.Size)

                          SendVm.findEmptyBoxS("boxsS")
                          SendVm.cloudResultBoxS.observe(viewLifecycleOwner,{idBoxS->
                              if(idBoxS!=null)
                              {
                                  //Jeżeli funkcja zwróciła wolna skrytkę wywołuje funkcję która nakazuje jej otwarcie
                                  //Poprzez zmienienie jej stanu OC na 1
                                  //Następnie wyświetla się komunikat użytkownikowi że dana skrytka jest otwarta i że należy
                                  //Umieścić w niej paczkę a następnie potwierdzić że się ją zamknęło
                                  //Po potwierdzeniu ustawiamy stan skrytki że jest pełna oraz że została zamknięta

                                  Toast.makeText(requireContext(), idBoxS, Toast.LENGTH_SHORT).show()
                                  boxId = idBoxS.toString()

                                  SendVm.editBoxData("boxsS" , idBoxS, numerPaczki)
                                  //TODO to powinno wywoływać moment zamknięcia skrytki
                                  //TODO stworzyc funkcje ktora przekazuje informacje do kolejnego fragmentu
                                  findNavController()
                                      .navigate(SendDirections.actionSendToConfirmSend().actionId)
                                  //W tym momencie wyświetla się ekran z potwierdzeniem
                                  //TODO Zrobić dlaszą część tego if`a. W tym momencie mamy zmiane w bazie danych ze skrzynka jest otwarta

                              }
                              else
                              {
                                  //Jeżeli wszystkie skrytki są zajęte wyświetla komunikat użytkownikowi!
                                  Toast.makeText(requireContext(),"Wszystkie małe skrytki są zajęte!", Toast.LENGTH_SHORT).show()
                              }
                          })
                      }
                      else
                      {
                          //TODO zrobić kod do paczki z rozmiarem 2 i 3
                          Log.d("To jest rozmiar paczki",pack.Size.toString())
                      }
                }
                //Jeżeli nie ma takiej paczki wyświetla Tosta z informacją dla użytkownika
                else
                {
                    Log.d("Ten numer paczki nie istnieje!",numerPaczki)
                    Toast.makeText(requireContext(),"Nie ma takiej paczki w bazie danych", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

//Funkcja wyswietlajaca  dane naszej paczki oraz osobno jeszcze jej rozmiar
    private fun bindPackInfo(pack: Pack) {

        Log.d("Informacje o naszej paczce", pack.toString())
        Log.d("Rozmiar naszej paczki",pack.Size.toString())
    }

    fun getIdPack(): String{
        val numerPaczki = numerPaczkiGL
        return numerPaczki
    }

    fun getIdBox(): String{
        Log.d("To zwracam",boxId)
        return boxId
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}