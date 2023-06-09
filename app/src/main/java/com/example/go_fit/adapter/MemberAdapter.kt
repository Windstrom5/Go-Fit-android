package com.example.go_fit.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.go_fit.DetailsActivity
import com.example.go_fit.R
import com.example.go_fit.api.PegawaiApi
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
        queue = Volley.newRequestQueue(context.applicationContext)
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
                val requestQueue = Volley.newRequestQueue(context)
                val StringRequest: StringRequest = object :  StringRequest(Method.GET,
                    presensikelasApi.GET_BY_USERNAME + vkelas + "/" + vtanggal+ "/" + vjam,
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
                            val presensiKelas = presensikelas(
                                item.nama_member,
                                vkelas,
                                vtanggal,
                                vjam,
                                status
                            )
                            val StringRequest: StringRequest = object : StringRequest(
                                Method.POST,
                                presensikelasApi.ADD_URL,
                                Response.Listener { response ->
                                    val gson = Gson()
                                    val presensi = gson.fromJson(response, presensiKelas::class.java)
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
                                    params["nama_kelas"] = vkelas
                                    params["nama_member"] = item.nama_member
                                    params["tanggal"] = vtanggal
                                    params["jam"] = vjam
                                    params["status"] = status
                                    return params
                                }
                            }
                            requestQueue.add(StringRequest)
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
                requestQueue.add(StringRequest)
            }
        }
    }
}