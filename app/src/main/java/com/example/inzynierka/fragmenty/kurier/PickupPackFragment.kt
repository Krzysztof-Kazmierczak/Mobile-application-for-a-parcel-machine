package com.example.inzynierka.fragmenty.kurier

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzynierka.R
import com.example.inzynierka.data.BoxS
import com.example.inzynierka.data.Pack
import com.example.inzynierka.databinding.PickupPackFragmentBinding
import com.example.inzynierka.fragmenty.TakePack.TakepackFragmentDirections
import com.example.inzynierka.fragmenty.TakePack.boxIdTF
import com.example.inzynierka.fragmenty.TakePack.numerPaczkiGLTF
import com.example.inzynierka.fragmenty.home.MyPacksAdapter
import com.example.inzynierka.fragmenty.home.PickupPacksAdapter

class PickupPackFragment : Fragment() {
    private val PROFILE_DEBUG = "PROFILE_DEBUG"
    private var _binding: PickupPackFragmentBinding? = null
    private val binding get() = _binding!!
    private val PickupPackVm by viewModels<PickupPackViewModel>()

    private lateinit var adapter: PickupPacksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PickupPackFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
        return inflater.inflate(R.layout.pickup_pack_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val connect =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connect.activeNetworkInfo
        binding.recyclerViewPickuppack.layoutManager = LinearLayoutManager(requireContext())

        adapter = PickupPacksAdapter { position ->

            val cos1 = PickupPackVm.endTimeBoxS.value?.get(position)?.ID_Box.toString()
            val cos2 = PickupPackVm.endTimeBoxS.value?.get(position)?.ID.toString()
            if(networkInfo != null && networkInfo.isConnected) {

                //todo OPEN BOX! i zmiana fragmentu
              //  findNavController()
               //     .navigate(TakepackFragmentDirections.actionTakepackFragmentToConfirmTake())
            }

        }

        binding.recyclerViewPickuppack.adapter = adapter
    }

    private fun networkConnectioCheck(){
        val connect =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connect.activeNetworkInfo
        // if(.isNotEmpty()){
        if(networkInfo != null && networkInfo.isConnected)
        {
            binding.networkConnection.visibility = View.INVISIBLE
            binding.PUPBrakPaczek.visibility = View.VISIBLE
        }else
        {
            binding.networkConnection.visibility = View.VISIBLE
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        PickupPackVm.endTimeBoxs()
        PickupPackVm.endTimeBoxS.observe(viewLifecycleOwner, { listEndTimePack ->

            if(listEndTimePack.isNotEmpty()){
                networkConnectioCheck()
                binding.PUPBrakPaczek.visibility = View.INVISIBLE

                    adapter.setEndTimePacks(listEndTimePack as ArrayList<BoxS>)
            }
            else{
                networkConnectioCheck()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}