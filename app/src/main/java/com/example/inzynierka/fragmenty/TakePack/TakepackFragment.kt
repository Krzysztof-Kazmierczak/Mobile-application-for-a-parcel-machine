package com.example.inzynierka.fragmenty.TakePack

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inzynierka.R
import com.example.inzynierka.data.User
import com.example.inzynierka.databinding.TakepackFragmentBinding
import com.example.inzynierka.fragmenty.Send.SendDirections
import com.example.inzynierka.fragmenty.Send.numerPaczkiGL
import com.example.inzynierka.fragmenty.home.HomeFragmentDirections
import com.example.inzynierka.fragmenty.home.HomeViewModel
import com.example.inzynierka.fragmenty.potwierdzenie.ConfirmTakeDirections
import com.example.inzynierka.fragmenty.repository.BaseFragment

var boxIdTF = String()
var numerPaczkiGLTF = String()

class TakepackFragment : Fragment() {

    private val PROFILE_DEBUG = "PROFILE_DEBUG"
    private var _binding: TakepackFragmentBinding? = null
    private val binding get() = _binding!!
    private val TakepackVm by viewModels<TakepackViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TakepackFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
        return inflater.inflate(R.layout.takepack_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        takePack()
    }

    private fun takePack() {
        binding.TakePack.setOnClickListener {
            //Pobralem id paczki ktora chce odebrac
            TakepackVm.packToMe()
            TakepackVm.idPackToMe.observe(viewLifecycleOwner, { idMyPack ->
                if (idMyPack != null) {
                    numerPaczkiGLTF = idMyPack.toString()
                    //Pobieram informacje o paczce
                    TakepackVm.packData(idMyPack)
                    TakepackVm.cloudResult.observe(viewLifecycleOwner, { packInfo ->
                        val sizePack = packInfo.Size
                        val idBoxToOpen = packInfo.Id_box
                        boxIdTF = packInfo.Id_box.toString()

                        //Otwiera naszą skrytkę
                        TakepackVm.openBox(sizePack.toString(), idBoxToOpen.toString())

                        findNavController()
                            .navigate(TakepackFragmentDirections.actionTakepackFragmentToConfirmTake().actionId)
                    })
                }
            })
        }
    }

    fun getIdBox(): String{
        Log.d("To zwracam", boxIdTF)
        return boxIdTF
    }

    fun getIdPack(): String{
        val numerPaczki = numerPaczkiGLTF
        return numerPaczki
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}