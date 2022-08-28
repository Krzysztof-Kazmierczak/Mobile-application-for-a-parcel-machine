package com.example.inzynierka.fragmenty.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inzynierka.R
import com.example.inzynierka.data.BoxS
import com.example.inzynierka.data.Pack
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//class MyPacksAdapter(private val listener: OnPackItemLongClick, val onPackClicked: (Int) -> Unit) :
class PickupPacksAdapter(val onPackClicked: (Int) -> Unit) :
    RecyclerView.Adapter<PickupPacksAdapter.PickupPacksViewHolder>() {


    private val pickuppacksList = ArrayList<BoxS>()

    fun setEndTimePacks(list: ArrayList<BoxS>) {
        pickuppacksList.clear()
        pickuppacksList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickupPacksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.packstotake_row, parent, false)
        return PickupPacksViewHolder(view)
    }

    override fun onBindViewHolder(holder: PickupPacksViewHolder, position: Int) {
        holder.itemView.findViewById<Button>(R.id.PUP_b_wyciagnijPaczke).setOnClickListener {
            // holder.itemView.setOnClickListener {
            onPackClicked(position)
        }
        bindData(holder)
    }

    private fun bindData(holder: PickupPacksViewHolder) {

        holder.itemView.apply {
            val id_pack = findViewById<TextView>(R.id.PUP_ID_pack)
            val size_pack = findViewById<TextView>(R.id.PUP_rozmiar_paczki)
            val id_box = findViewById<TextView>(R.id.PUP_id_box)
            val pickup_time = findViewById<TextView>(R.id.PUP_czas_odbioru)

            // val take_pack = holder.itemView.findViewById<Button>(R.id.MP_b_odbierzPaczke)
            val cal = Calendar.getInstance()
            cal.time
            pickuppacksList[holder.adapterPosition].apply {
                id_pack.text = ID
                size_pack.text = Size
                id_box.text = ID_Box
                cal[Calendar.DAY_OF_MONTH] = day?.toInt()!!
                cal[Calendar.MONTH] = month?.toInt()!! - 1
                cal[Calendar.YEAR] = year?.toInt()!!

                val wyswietlanieDaty = SimpleDateFormat("dd-MM-yyyy", Locale.UK).format(cal.time)
                pickup_time.text = wyswietlanieDaty //day + " - " + month + " - " + year
            }

            // take_pack.setOnClickListener {
            //    listener.onMyPackLongClick(mypacksList[adapterPosition], adapterPosition)
            // }

            // take_pack

        }
    }

    override fun getItemCount(): Int {
        return pickuppacksList.size
    }

    inner class PickupPacksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            val pickup_pack = itemView.findViewById<Button>(R.id.PUP_b_wyciagnijPaczke)
            pickup_pack.setOnClickListener {
            }
        }
    }
}
