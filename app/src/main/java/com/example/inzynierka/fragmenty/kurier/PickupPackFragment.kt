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
import com.example.inzynierka.databinding.PickupPackFragmentBinding
import com.example.inzynierka.fragmenty.TakePack.TakepackFragmentDirections
import com.example.inzynierka.fragmenty.TakePack.boxIdTF
import com.example.inzynierka.fragmenty.TakePack.numerPaczkiGLTF
import com.example.inzynierka.fragmenty.home.MyPacksAdapter

class PickupPackFragment : Fragment() {
    private val PROFILE_DEBUG = "PROFILE_DEBUG"
    private var _binding: PickupPackFragmentBinding? = null
    private val binding get() = _binding!!
    private val PickupPackVm by viewModels<PickupPackViewModel>()

    //private lateinit var adapter: MyPickupPackAdapter
    companion object {
        fun newInstance() = PickupPackFragment()
    }

    private lateinit var viewModel: PickupPackViewModel

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

        //adapter = MyPacksAdapter { position ->

           // boxIdTF = TakepackVm.mypacks.value?.get(position)?.Id_box.toString()
           // numerPaczkiGLTF = TakepackVm.mypacks.value?.get(position)?.packID.toString()
            if(networkInfo != null && networkInfo.isConnected) {
                findNavController()
                    .navigate(TakepackFragmentDirections.actionTakepackFragmentToConfirmTake())
            }

       // }

        //binding.recyclerViewTakepack.adapter = adapter
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
        viewModel = ViewModelProvider(this).get(PickupPackViewModel::class.java)
        // TODO: Use the ViewModel
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}