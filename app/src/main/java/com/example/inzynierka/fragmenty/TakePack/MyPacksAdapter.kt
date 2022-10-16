package com.example.inzynierka.fragmenty.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inzynierka.R
import com.example.inzynierka.data.Pack
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyPacksAdapter(val onPackClicked: (Int) -> Unit) : RecyclerView.Adapter<MyPacksAdapter.MyPacksViewHolder>() {
    //Tworzenie listy do adaptera
    private val mypacksList = ArrayList<Pack>()
    //Tworzenie wyglądu ekranu scrollView (lista naszych danych)
    fun setMyPacks(list: ArrayList<Pack>) {
        mypacksList.clear()
        mypacksList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPacksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.mypacks_row, parent, false)
        return MyPacksViewHolder(view)
    }
    //Przypisanie do przycisku "wyciągnij paczkę" funkcji pobrania danych z "kafelka"
    override fun onBindViewHolder(holder: MyPacksViewHolder, position: Int) {
        holder.itemView.findViewById<Button>(R.id.MP_b_odbierzPaczke).setOnClickListener {
            onPackClicked(position)
        }
        bindData(holder)
    }

    private fun bindData(holder: MyPacksViewHolder) {
        holder.itemView.apply {
            //Przypisanie do zmiennych odpowiednich pół z "kafelka"
            val id_pack = findViewById<TextView>(R.id.MP_ID_pack)
            val size_pack = findViewById<TextView>(R.id.MP_rozmiar_paczki)
            val id_box = findViewById<TextView>(R.id.MP_id_box)
            val pickup_time = findViewById<TextView>(R.id.MP_czas_odbioru)
            //Pobieranie daty z kalendarza
            val cal = Calendar.getInstance()
            cal.time
            mypacksList[holder.adapterPosition].apply {
                id_pack.text = packID
                size_pack.text = Size
                id_box.text = Id_box
                //-1 miesiac bo jakos miesiace dziwnie zapisuje od 0 a nie od 1......(w bazie danych jak zapisuje dodaje 1 to tu muszę odjąć)
                cal[Calendar.DAY_OF_MONTH] = day?.toInt()!!
                cal[Calendar.MONTH] = month?.toInt()!! - 1
                cal[Calendar.YEAR] = year?.toInt()!!
                //Wyswietlanie daty w danym formacie
                val wyswietlanieDaty = SimpleDateFormat("dd-MM-yyyy", Locale.UK).format(cal.time)
                pickup_time.text = wyswietlanieDaty
            }
        }
    }

    override fun getItemCount(): Int {
        return mypacksList.size
    }

    inner class MyPacksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            val take_pack = itemView.findViewById<Button>(R.id.MP_b_odbierzPaczke)
            take_pack.setOnClickListener {
            }
        }
    }
}
