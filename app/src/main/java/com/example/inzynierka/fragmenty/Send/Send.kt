package com.example.inzynierka.fragmenty.Send

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inzynierka.R
import com.example.inzynierka.data.Pack
import com.example.inzynierka.databinding.SendFragmentBinding
import com.example.inzynierka.fragmenty.potwierdzenie.ConfirmSend


// wspólny viewModel, datastore/sharePrefereces, callback, navargs / intent.bundle (putString())
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
    }


    private fun PutPack(){
        binding.SendUmiesPaczke.setOnClickListener {
            val numerPaczki = binding.SendWpiszNumer.text?.trim().toString()
            if (numerPaczki != "") {
                numerPaczkiGL = numerPaczki
                SendVm.putPack(numerPaczki)
                SendVm.cloudResult.observe(viewLifecycleOwner, { pack ->
                    if (pack != null) {
                        bindPackInfo(pack)
                        if (pack.Size == 1.toString()) {
                            Log.d("To jest mała paczka", pack.Size)
                            SendVm.findEmptyBoxS("box")
                            SendVm.cloudResultBoxS.observe(viewLifecycleOwner, { idBoxS ->
                                if (idBoxS != null) {
                                    Toast.makeText(requireContext(), idBoxS, Toast.LENGTH_SHORT)
                                        .show()
                                    boxId = idBoxS.toString()
//                                SendVm.setNumberId(idBoxS.toString())
                                    SendVm.editBoxData("box", idBoxS, numerPaczki)

                                    //SendVm.editUserData(pack.uid.toString(), numerPaczki)

                                    //val fragmentManager = supportFragmentManager
                                    val fragmentTransaction = fragmentManager?.beginTransaction()
                                    fragmentTransaction?.replace(R.id.frame_layout, ConfirmSend())
                                    fragmentTransaction?.commit()

                                    //findNavController()
                                        //.navigate(SendDirections.actionSendToConfirmSend().actionId)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Wszystkie małe skrytki są zajęte!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        } else {
                            Log.d("To jest rozmiar paczki", pack.Size.toString())
                        }
                    } else {
                        Log.d("Ten numer paczki nie istnieje!", numerPaczki)
                        Toast.makeText(
                            requireContext(),
                            "Nie ma takiej paczki w bazie danych",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
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