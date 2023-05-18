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
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.go_fit.DetailsActivity
import com.example.go_fit.R
import com.example.go_fit.model.member
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class MemberAdapter (private var itemList: List<member>, context: Context) :
    RecyclerView.Adapter<MemberAdapter.ViewHolder>(), Filterable{
    private var filteredItemList: MutableList<member>
    private val context:Context
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
        val view = inflater.inflate(R.layout.schedule_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    fun setItemList(listClass: Array<member>){
        this.itemList = listClass.toList()
        filteredItemList = listClass.toMutableList()
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<member> = java.util.ArrayList()
                if(charSequenceString.isEmpty()){
                    filtered.addAll(itemList)
                }else{
                    for(item in itemList){
                        if(item.nama_member.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))
                        )filtered.add(item)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                for (i in filtered){
                    println(i.nama_member)
                }
                return filterResults
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredItemList.clear()
                filteredItemList.addAll(filterResults.values as List<member>)
                notifyDataSetChanged()
            }
        }
    }

    fun getVariable(user : String,pass : String){
        vuser = user
        vpass = pass
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nama : TextView
        var hadir : RadioButton
        var absent : RadioButton
        init {
            nama = itemView.findViewById(R.id.student_name_adapter)
            hadir = itemView.findViewById(R.id.radio_present)
            absent = itemView.findViewById(R.id.radio_absent)
        }
    }

    override fun onBindViewHolder(holder: MemberAdapter.ViewHolder, position: Int) {
        val item = filteredItemList[position]
        holder.nama.text = item.nama_member
        holder.hadir.setOnClickListener {
            if (holder.hadir.isChecked) {
                item.Status = "Hadir"
//                updateDataInDatabase(item.id_member, "Hadir")
            }
        }
    }
}