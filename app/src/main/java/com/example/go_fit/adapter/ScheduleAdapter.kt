package com.example.go_fit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.go_fit.R
import com.example.go_fit.model.jadwalharian
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleAdapter(private var itemList: List<jadwalharian>, context: Context) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>(), Filterable {
    private var filteredItemList: MutableList<jadwalharian>
    private val context:Context
    private var queue: RequestQueue? = null

    init {
        filteredItemList = ArrayList(itemList)
        this.context = context
        queue = Volley.newRequestQueue(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.schedule_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    fun setItemList(listClass: Array<jadwalharian>){
        this.itemList = listClass.toList()
        filteredItemList = listClass.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItemList[position]
        holder.tvKelas.text = item.nama_kelas
        holder.tvInstruktur.text = item.nama
        holder.tvHari.text = item.hari
        println("status: ${item.nama_kelas}")
        val jam = LocalTime.parse(item.jam, DateTimeFormatter.ofPattern("HH:mm"))
        val jamPlus2Hours = jam.plusHours(2)
        val jamFormatted = jam.format(DateTimeFormatter.ofPattern("HH:mm"))
        val jamPlus2HoursFormatted = jamPlus2Hours.format(DateTimeFormatter.ofPattern("HH:mm"))
        holder.tvJam.text = "$jamFormatted-$jamPlus2HoursFormatted"
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<jadwalharian> = java.util.ArrayList()
                if(charSequenceString.isEmpty()){
                    filtered.addAll(itemList)
                }else{
                    for(item in itemList){
                        if(item.nama_kelas.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))
                        )filtered.add(item)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                for (i in filtered){
                    println(i.nama_kelas)
                }
                return filterResults
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredItemList.clear()
                filteredItemList.addAll(filterResults.values as List<jadwalharian>)
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvKelas : TextView
        var tvJam : TextView
        var tvTanggal : TextView
        var tvHari : TextView
        var tvInstruktur : TextView
        var btnMore : TextView
        var cvItem : CardView

        init {
            tvKelas = itemView.findViewById(R.id.tv_namaKelas)
            tvJam = itemView.findViewById(R.id.tv_jam)
            tvHari = itemView.findViewById(R.id.tv_namaHari)
            tvTanggal = itemView.findViewById(R.id.tv_tanggal)
            tvInstruktur = itemView.findViewById(R.id.tv_instruktur)
            btnMore = itemView.findViewById(R.id.seeMore)
            cvItem = itemView.findViewById(R.id.card_view)
        }
    }
}