package com.example.inzynierka.fragmenty.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inzynierka.R
import com.example.inzynierka.data.Pack

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

        //val button_takePack = holder.itemView.findViewById<TextView>(R.id.MP_b_odbierzPaczke)
    }

    private fun bindData(holder: MyPacksViewHolder){
        val id_pack = holder.itemView.findViewById<TextView>(R.id.MP_id_pack)
        val size_pack = holder.itemView.findViewById<TextView>(R.id.MP_rozmiar_paczki)

        id_pack.text = mypacksList[holder.adapterPosition].Id_box
        size_pack.text = mypacksList[holder.adapterPosition].Size

    }

    override fun getItemCount(): Int {
        return mypacksList.size
    }

    inner class MyPacksViewHolder(view: View) : RecyclerView.ViewHolder(view){
        init{

            view.setOnClickListener{
                listener.onMyPackLongClick(mypacksList[adapterPosition], adapterPosition)
               //on tu ma setonClicklistener true
            }
        }
    }
}

interface OnPackItemLongClick {
    fun onMyPackLongClick(pack: Pack , position: Int)
    
}