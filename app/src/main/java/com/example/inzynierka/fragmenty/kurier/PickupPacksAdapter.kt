package com.example.inzynierka.fragmenty.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inzynierka.R
import com.example.inzynierka.data.BoxS
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PickupPacksAdapter(val onPackClicked: (Int) -> Unit) : RecyclerView.Adapter<PickupPacksAdapter.PickupPacksViewHolder>() {
    //Tworzenie listy do adaptera
    private val pickuppacksList = ArrayList<BoxS>()
    //Tworzenie wyglądu ekranu scrollView (lista naszych danych)
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
    //Przypisanie do przycisku "wyjmij paczkę" funkcji pobrania danych z "kafelka"
    override fun onBindViewHolder(holder: PickupPacksViewHolder, position: Int) {
        holder.itemView.findViewById<Button>(R.id.PUP_b_wyciagnijPaczke).setOnClickListener {
            onPackClicked(position)
        }
        bindData(holder)
    }

    private fun bindData(holder: PickupPacksViewHolder) {
        holder.itemView.apply {
            //Przypisanie do zmiennych odpowiednich pół z "kafelka"
            val id_pack = findViewById<TextView>(R.id.PUP_ID_pack)
            val size_pack = findViewById<TextView>(R.id.PUP_rozmiar_paczki)
            val id_box = findViewById<TextView>(R.id.PUP_id_box)
            val pickup_time = findViewById<TextView>(R.id.PUP_czas_odbioru)
            //Pobieranie daty z kalendarza
            val cal = Calendar.getInstance()
            cal.time
            pickuppacksList[holder.adapterPosition].apply {
                id_pack.text = ID
                size_pack.text = Size
                id_box.text = ID_Box
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
