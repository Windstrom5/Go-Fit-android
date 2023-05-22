package com.example.go_fit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.go_fit.DetailsActivity
import com.example.go_fit.R
import com.example.go_fit.api.bookingkelasApi
import com.example.go_fit.api.presensikelasApi
import com.example.go_fit.model.bookingkelas
import com.example.go_fit.model.member
import com.example.go_fit.model.presensikelas
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
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
    private lateinit var vtanggal : String
    private lateinit var vkelas : String
    private lateinit var vjam : String
    private var queue: RequestQueue? = null

    init {
        filteredItemList = ArrayList(itemList)
        this.context = context
        queue = Volley.newRequestQueue(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.member_item, parent, false)
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

    fun getVariable(user : String,pass : String,kelas:String,tanggal : String, jam:String){
        vuser = user
        vpass = pass
        vkelas = kelas
        vtanggal = tanggal
        vjam = jam
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nama : TextView
        var radioGroup : RadioGroup
        var radioPresent: RadioButton
        var radioAbsent: RadioButton
        init {
            nama = itemView.findViewById(R.id.student_name_adapter)
            radioGroup = itemView.findViewById(R.id.radioGroup)
            radioPresent = itemView.findViewById(R.id.radio_present)
            radioAbsent = itemView.findViewById(R.id.radio_absent)
        }
    }

    override fun onBindViewHolder(holder: MemberAdapter.ViewHolder, position: Int) {
        val item = filteredItemList[position]
        holder.nama.text = item.nama_member
        holder.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val status: String = when (checkedId) {
                R.id.radio_present -> "hadir"
                R.id.radio_absent -> "tidak hadir"
                else -> ""
            }
            // Perform the desired action based on the selected status
            if (status.isNotEmpty()) {
                // Make the string request and handle the response accordingly

            }else{
                val presensiKelas = presensikelas(
                    item.nama_member,
                    vkelas,
                    vtanggal,
                    vjam,
                    status
                )
                val StringRequest:StringRequest = object : StringRequest(Method.POST, presensikelasApi.ADD_URL,
                    Response.Listener { response ->
                        val gson = Gson()
                        val presensi = gson.fromJson(response, presensiKelas::class.java)
                        if(presensi != null)
                            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                    },Response.ErrorListener { error->
                        try{
                            val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                            val errors = JSONObject(responseBody)
                            Toast.makeText(
                                context,
                                errors.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }catch (e: Exception){
                            Toast.makeText(context,e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                ){
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = java.util.HashMap<String, String>()
                        headers["Accept"] = "application/json"
                        return headers
                    }

                    override fun getParams(): Map<String, String>? {
                        val params = java.util.HashMap<String, String>()
                        params.put("nama_kelas",vkelas)
                        params.put("nama_member",item.nama_member)
                        params.put("tanggal",vtanggal)
                        params.put("jam",vjam)
                        params.put("jenis",status)
                        return params
                    }
                }
                queue!!.add(StringRequest)
            }
        }
    }
}