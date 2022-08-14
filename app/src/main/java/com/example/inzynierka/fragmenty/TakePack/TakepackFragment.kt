package com.example.inzynierka.fragmenty.TakePack

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzynierka.R
import com.example.inzynierka.data.Pack
import com.example.inzynierka.databinding.TakepackFragmentBinding
import com.example.inzynierka.fragmenty.home.MyPacksAdapter
import com.example.inzynierka.fragmenty.home.OnPackItemLongClick

var boxIdTF = String()
var numerPaczkiGLTF = String()

class TakepackFragment : Fragment(), OnPackItemLongClick {

    private val PROFILE_DEBUG = "PROFILE_DEBUG"
    private var _binding: TakepackFragmentBinding? = null
    private val binding get() = _binding!!
    private val TakepackVm by viewModels<TakepackViewModel>()

    private val adapter = MyPacksAdapter(this)


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

        binding.recyclerViewTakepack.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTakepack.adapter = adapter
        takePack()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
      //  TakepackVm.packsToMe()
        TakepackVm.idPacksToMe2.observe(viewLifecycleOwner, {listMyPack ->

            TakepackVm.packData(listMyPack)
            TakepackVm.mypacks.observe(viewLifecycleOwner, {list ->
                adapter.setMyPacks(list as ArrayList<Pack>)
            })
        })
    }

    private fun takePack() {
        binding.recyclerViewTakepack.setOnClickListener{
       // binding.TakePack.setOnClickListener {
            //Pobralem id paczki ktora chce odebrac
            //TakepackVm.getUserData()

            TakepackVm.packsToMe()
            TakepackVm.idPackToMe.observe(viewLifecycleOwner, { idMyPacks ->
                if (idMyPacks != "") {
                    numerPaczkiGLTF = idMyPacks.toString()
                    //Pobieram informacje o paczce
                    TakepackVm.packData(idMyPacks)
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



            TakepackVm.packToMe()
            TakepackVm.idPackToMe.observe(viewLifecycleOwner, { idMyPack ->
                if (idMyPack != "") {
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

    override fun onMyPackLongClick(pack: Pack, position: Int) {
        Toast.makeText(requireContext(),pack.Id_box,Toast.LENGTH_SHORT).show()
    }
}