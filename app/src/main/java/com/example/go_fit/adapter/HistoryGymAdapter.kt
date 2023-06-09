package com.example.go_fit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.go_fit.DetailsActivity
import com.example.go_fit.R
import com.example.go_fit.model.historygym
import com.example.go_fit.model.historykelas
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryGymAdapter (private var itemList: List<historygym>, context: Context) :
    RecyclerView.Adapter<HistoryGymAdapter.ViewHolder>(), Filterable {
    private var filteredItemList: MutableList<historygym>
    private val context: Context
    private lateinit var vuser : String
    private lateinit var vpass : String
    private var queue: RequestQueue? = null

    init {
        filteredItemList = ArrayList(itemList)
        this.context = context
        queue = Volley.newRequestQueue(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.historygym, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    fun setItemList(listClass: Array<historygym>){
        this.itemList = listClass.toList()
        filteredItemList = listClass.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItemList[position]

        println("status: ${item.tanggal}")
        val jammasuk = LocalTime.parse(item.jam_masuk, DateTimeFormatter.ofPattern("HH:mm:ss"))
        val jamkeluar = LocalTime.parse(item.jam_keluar, DateTimeFormatter.ofPattern("HH:mm:ss"))
        val jammasukFormatted = jammasuk.format(DateTimeFormatter.ofPattern("HH:mm"))
        val jamkeluarFormatted = jamkeluar.format(DateTimeFormatter.ofPattern("HH:mm"))
        holder.tvjammulai.text = jammasukFormatted
        holder.tvjamselesai.text = jammasukFormatted
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
        val date = LocalDate.parse(item.tanggal)
        val formattedDate = formatter.format(date)
        holder.tvTanggal.text = formattedDate
        holder.tvstatus.text = item.status
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<historygym> = java.util.ArrayList()
                if(charSequenceString.isEmpty()){
                    filtered.addAll(itemList)
                }else{
                    for(item in itemList){
                        if(item.tanggal.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))
                        )filtered.add(item)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                for (i in filtered){
                    println(i.tanggal)
                }
                return filterResults
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredItemList.clear()
                filteredItemList.addAll(filterResults.values as List<historygym>)
                notifyDataSetChanged()
            }
        }
    }

    fun getVariable(user : String,pass : String){
        vuser = user
        vpass = pass
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvTanggal : TextView
        var tvjamselesai : TextView
        var tvjammulai : TextView
        var cvItem : CardView
        var tvstatus : TextView
        init {
            tvTanggal = itemView.findViewById(R.id.tv_tanggalHistorygym)
            tvjammulai = itemView.findViewById(R.id.tv_jamMulaiHistory)
            tvjamselesai = itemView.findViewById(R.id.tv_jamselesaiHistory)
            cvItem = itemView.findViewById(R.id.card_view)
            tvstatus = itemView.findViewById(R.id.tv_statusHistory)
        }
    }
}