package com.example.inzynierka.fragmenty.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.inzynierka.R
import com.example.inzynierka.data.Pack
import com.example.inzynierka.fragmenty.Send.SendDirections
import com.example.inzynierka.fragmenty.TakePack.TakepackFragmentDirections

class MyPacksAdapter(private val listener: OnPackItemLongClick) :
    RecyclerView.Adapter<MyPacksAdapter.MyPacksViewHolder>() {


    private val mypacksList = ArrayList<Pack>()

    fun setMyPacks(list: ArrayList<Pack>){
        mypacksList.clear()
        mypacksList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPacksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.mypacks_row,parent,false)
        return MyPacksViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPacksViewHolder, position: Int) {

        bindData(holder)
    }

    private fun bindData(holder: MyPacksViewHolder){



        val id_pack = holder.itemView.findViewById<TextView>(R.id.MP_ID_pack)
        val size_pack = holder.itemView.findViewById<TextView>(R.id.MP_rozmiar_paczki)
        val id_box = holder.itemView.findViewById<TextView>(R.id.MP_id_box)

       // val take_pack = holder.itemView.findViewById<Button>(R.id.MP_b_odbierzPaczke)

        id_pack.text = mypacksList[holder.adapterPosition].packID
        size_pack.text = mypacksList[holder.adapterPosition].Size
        id_box.text = mypacksList[holder.adapterPosition].Id_box

       // take_pack.setOnClickListener {
        //    listener.onMyPackLongClick(mypacksList[adapterPosition], adapterPosition)
       // }

       // take_pack

    }

    override fun getItemCount(): Int {
        return mypacksList.size
    }

    inner class MyPacksViewHolder(view: View) : RecyclerView.ViewHolder(view){
        init{
            val take_pack = itemView.findViewById<Button>(R.id.MP_b_odbierzPaczke)
            take_pack.setOnClickListener {
                listener.onBoxOpenClick(mypacksList[adapterPosition],adapterPosition)
               // findNavController(view).navigate(MyPacksAdapterDirections.action)
          //      navigate(TakepackFragmentDirections.actionTakepackFragmentToConfirmTake().actionId)
            }



           // view.setOnClickListener{
            //    listener.onMyPackLongClick(mypacksList[adapterPosition], adapterPosition)
               //on tu ma setonClicklistener true
            //}
        }
    }
}

interface OnPackItemLongClick {
    //fun onMyPackLongClick(pack: Pack , position: Int)
    fun onBoxOpenClick(pack: Pack , position: Int)
}