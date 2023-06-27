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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.go_fit.DetailsActivity
import com.example.go_fit.R
import com.example.go_fit.api.presensiInstrukturApi
import com.example.go_fit.api.presensikelasApi
import com.example.go_fit.model.jadwalharian
import com.example.go_fit.model.presensiinstruktur
import com.example.go_fit.model.presensikelas
import com.google.gson.Gson
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class InstrukturAdapter(private var itemList: List<jadwalharian>, context: Context) :
    RecyclerView.Adapter<InstrukturAdapter.ViewHolder>(), Filterable {
    private var filteredItemList: MutableList<jadwalharian>
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
        val view = inflater.inflate(R.layout.instrukturitem, parent, false)
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
        println("status: ${item.tanggal_kelas}")
        val jam = LocalTime.parse(item.jam, DateTimeFormatter.ofPattern("HH:mm:ss"))
        val jamPlus2Hours = jam.plusHours(2)
        val jamFormatted = jam.format(DateTimeFormatter.ofPattern("HH:mm"))
        val jamPlus2HoursFormatted = jamPlus2Hours.format(DateTimeFormatter.ofPattern("HH:mm"))
        holder.tvJam.text = "$jamFormatted-$jamPlus2HoursFormatted"
        // Fetch data from the API and auto-select the radio button based on status
        val requestQueue = Volley.newRequestQueue(context)
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            presensiInstrukturApi.GET_BY_USERNAME + item.nama_kelas + "/" + item.nama + "/" + item.tanggal_kelas + "/" + item.jam,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val data = jsonObject.optJSONArray("data")

                if (data != null && data.length() > 0) {
                    // Data found, get the status value
                    val presensiData = data.getJSONObject(0)
                    val status = presensiData.optString("status")
                    setRadioButtonSelection(holder, status)
                } else {
                    // Data not found, set default radio button selection
                    setRadioButtonSelection(holder, "")
                }
            },
            Response.ErrorListener { error ->
                // Handle error during GET request
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["nama_kelas"] = item.nama_kelas
                params["nama"] = item.nama
                params["tanggal"] = item.tanggal_kelas
                params["jam"] = item.jam
                return params
            }
        }
        requestQueue.add(StringRequest)
        holder.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val status: String = when (checkedId) {
                R.id.radio_present -> "hadir"
                R.id.radio_absent -> "tidak hadir"
                else -> ""
            }
            if (status.isNotEmpty()) {
                val StringRequest2: StringRequest = object :  StringRequest(
                    Method.GET,
                        presensiInstrukturApi.GET_BY_USERNAME + item.nama_kelas + "/" + item.nama + "/" + item.tanggal_kelas + "/" +item.jam,
                    Response.Listener { response ->
                        val gson = Gson()
                        val jsonObject = JSONObject(response)
                        val data = jsonObject.optJSONArray("data")

                        if (data != null && data.length() > 0) {
                            // Data found, perform update
                            val presensiData = data.getJSONObject(0)
                            val id = presensiData.optString("id")
                            if (id != null) {
                                // Perform PUT request to update the presensi
                                val stringRequest2: StringRequest = object : StringRequest(
                                    Method.PUT,
                                    presensikelasApi.UPDATE_URL + id,
                                    Response.Listener { response ->
                                        // Handle successful update
                                    },
                                    Response.ErrorListener { error ->
                                        // Handle error during update
                                    }
                                ) {
                                    @Throws(AuthFailureError::class)
                                    override fun getHeaders(): Map<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["Accept"] = "application/json"
                                        return headers
                                    }

                                    @Throws(AuthFailureError::class)
                                    override fun getBodyContentType(): String {
                                        return "application/json"
                                    }

                                    override fun getBody(): ByteArray {
                                        val params = HashMap<String, String>()
                                        params["status"] = status
                                        val jsonParams = JSONObject(params as Map<*, *>)
                                        return jsonParams.toString().toByteArray(Charsets.UTF_8)
                                    }
                                }
                                requestQueue.add(stringRequest2)
                            }
                        } else {
                            // Data not found, perform creation
                            val presensiInstruktur = presensiinstruktur(
                                item.nama,
                                item.nama_kelas,
                                item.tanggal_kelas,
                                item.jam,
                                status
                            )
                            val StringRequest3: StringRequest = object : StringRequest(
                                Method.POST,
                                presensikelasApi.ADD_URL,
                                Response.Listener { response ->
                                    val gson = Gson()
                                    val presensi = gson.fromJson(response, presensiInstruktur::class.java)
                                    // Handle successful addition
                                },
                                Response.ErrorListener { error ->
                                    // Handle error during addition
                                }
                            ) {
                                @Throws(AuthFailureError::class)
                                override fun getHeaders(): Map<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Accept"] = "application/json"
                                    return headers
                                }

                                override fun getParams(): Map<String, String> {
                                    val params = HashMap<String, String>()
                                    params["nama"] = item.nama
                                    params["nama_kelas"] = item.nama_kelas
                                    params["tanggal"] = item.tanggal_kelas
                                    params["jam"] = item.jam
                                    params["status"] = status
                                    return params
                                }
                            }
                            requestQueue.add(StringRequest3)
                        }
                    },
                    Response.ErrorListener { error ->
                        // Handle error during GET request
                    }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Accept"] = "application/json"
                        return headers
                    }
                }
                requestQueue.add(StringRequest2)
            }
        }
    }
    private fun setRadioButtonSelection(holder: ViewHolder, status: String) {
        when (status) {
            "hadir" -> holder.radioGroup.check(R.id.radio_present)
            "tidak hadir" -> holder.radioGroup.check(R.id.radio_absent)
            else -> holder.radioGroup.clearCheck()
        }
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

    fun getVariable(user : String,pass : String){
        vuser = user
        vpass = pass
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvKelas : TextView
        var tvJam : TextView
        var tvInstruktur : TextView
        var radioGroup : RadioGroup
        var radioPresent: RadioButton
        var radioAbsent: RadioButton
        init {
            tvKelas = itemView.findViewById(R.id.class_name)
            tvJam = itemView.findViewById(R.id.clock)
            tvInstruktur = itemView.findViewById(R.id.instruktur_name)
            radioGroup = itemView.findViewById(R.id.radioGroup)
            radioPresent = itemView.findViewById(R.id.radio_present)
            radioAbsent = itemView.findViewById(R.id.radio_absent)
        }
    }
}