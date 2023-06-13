package com.example.go_fit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.go_fit.api.MemberApi
import com.example.go_fit.api.bookingkelasApi
import com.example.go_fit.databinding.ActivityDetailsBinding
import com.example.go_fit.model.bookingkelas
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.HashMap

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailsBinding
    private lateinit var kelas : TextView
    private lateinit var instruktur : TextView
    private lateinit var harga : TextView
    private lateinit var tanggal : TextView
    private lateinit var mbunlde : Bundle
    private lateinit var loading : LinearLayout
    private lateinit var vuser : String
    private lateinit var vpass : String
    private lateinit var vkelas : String
    private lateinit var vharga : String
    private lateinit var vtanggal : String
    private lateinit var btnUang : Button
    private lateinit var btnKelas : Button
    private lateinit var vjam:String
    private lateinit var nama:String
    private lateinit var vinstruktur :String
    private var queue: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        queue= Volley.newRequestQueue(this)
        kelas = binding.kelas
        instruktur = binding.instruktur
        harga = binding.harga
        tanggal = binding.tanggal
        loading = findViewById(R.id.layout_loading)
        btnUang = binding.buttonuang
        btnKelas = binding.buttonkelas
        getBundle()
        kelas.setText(vkelas)
        instruktur.setText(vinstruktur)
        harga.setText(vharga)
        tanggal.setText(vtanggal+'/'+vjam)
        if(vuser != null.toString() && vpass != null.toString()){
            getName(vuser,vpass)
            btnUang.setOnClickListener() {
                paywithUang(vuser,vpass,vharga)
            }
            btnKelas.setOnClickListener(){
                paywithpaket(vuser,vpass,vharga)
            }
        }else {
            btnUang.visibility =View.GONE
            btnKelas.visibility = View.GONE
        }
    }

    fun paywithUang(vuser: String,pass:String,harga : String){
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            MemberApi.GET_BY_USERNAME + vuser +"/"+pass,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                // Check if role is Manajer Operasional
                val data = jsonObject.optJSONObject("data")
                val uang = data?.optString("deposit_uang")
                if (uang != null) {
                    val bayar = uang.toInt()
                    val total = harga.toInt()
                    if(bayar < total){
                        val dialogBuilder = AlertDialog.Builder(this)
                        dialogBuilder
                            .setTitle("Peringatan")
                            .setMessage("Uang Anda tidak cukup")
                            .setPositiveButton("OK") { dialog, which ->
                                dialog.dismiss()
                            }
                            .setCancelable(false)
                            .show()
                    }else{
                        val dialogBuilder = AlertDialog.Builder(this)
                        val dialogView = layoutInflater.inflate(R.layout.popup_layout, null)
                        val valueBeforeDeposit = dialogView.findViewById<TextView>(R.id.value_before_deposit)
                        val valueAfterDeposit = dialogView.findViewById<TextView>(R.id.value_after_deposit)
                        valueBeforeDeposit.text = uang
                        valueAfterDeposit.text = (bayar - total).toString()

                        dialogBuilder.setView(dialogView)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Yes") { dialog, which ->
                                bookingKelas("regular")
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, which ->
                                // No button clicked
                                // Perform your desired action here
                                dialog.dismiss()
                            }
                            .setCancelable(false)
                            .show()
                    }
                }
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@DetailsActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@DetailsActivity, e.message, Toast.LENGTH_SHORT).show()
                }
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
                params["username"] = vuser
                params["password"] = vpass
                return params
            }
        }
        queue!!.add(StringRequest)
    }

    fun paywithpaket(vuser: String,pass:String,harga : String){
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            MemberApi.GET_BY_USERNAME + vuser+"/"+pass,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                // Check if role is Manajer Operasional
                val data = jsonObject.optJSONObject("data")
                val kelas = data?.optString("deposit_kelas")
                val nama_kelas = data?.optString("nama_kelas")
                if (nama_kelas != null) {
                    val bayar = kelas?.toInt()
                    val total = harga.toInt()
                    if (bayar != null) {
                        if(bayar < total){
                            val dialogBuilder = AlertDialog.Builder(this)
                            dialogBuilder
                                .setTitle("Peringatan")
                                .setMessage("Deposit Kelas Anda tidak cukup")
                                .setPositiveButton("OK") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .setCancelable(false)
                                .show()
                        }else{
                            val dialogBuilder = AlertDialog.Builder(this)
                            val dialogView = layoutInflater.inflate(R.layout.popup_layout, null)
                            val valueBeforeDeposit = dialogView.findViewById<TextView>(R.id.value_before_deposit)
                            val valueAfterDeposit = dialogView.findViewById<TextView>(R.id.value_after_deposit)
                            valueBeforeDeposit.text = bayar.toString()
                            valueAfterDeposit.text = (bayar - total).toString()

                            dialogBuilder.setView(dialogView)
                                .setTitle("Confirmation")
                                .setMessage("Are you sure?")
                                .setPositiveButton("Yes") { dialog, which ->
                                    bookingKelas("paket")
                                    dialog.dismiss()
                                }
                                .setNegativeButton("No") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .setCancelable(false)
                                .show()
                        }
                    }
                }else{
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder
                        .setTitle("Peringatan")
                        .setMessage("Anda Tidak Memiliki Deposit Kelas Untuk Kelas Ini")
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
                }
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@DetailsActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@DetailsActivity, e.message, Toast.LENGTH_SHORT).show()
                }
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
                params["username"] = vuser
                params["password"] = vpass
                return params
            }
        }
        queue!!.add(StringRequest)
    }

    private fun getBundle(){
        setLoading(true)
        try{
            mbunlde = intent?.getBundleExtra("profile")!!
            if(mbunlde != null){
                vuser =mbunlde.getString("username")!!
                vpass = mbunlde.getString("password")!!
                vinstruktur = mbunlde.getString("instruktur")!!
                vkelas = mbunlde.getString("kelas")!!
                vjam = mbunlde.getString("jam")!!
                vtanggal = mbunlde.getString("tanggal")!!
                vharga = mbunlde.getString("harga")!!
            }
        }catch(e: NullPointerException) {

        }
        setLoading(false)
    }

    private fun bookingKelas(jenis : String){
        setLoading(true)
        val bookingKelas = bookingkelas(
            nama,
            vkelas,
            vtanggal,
            vjam,
            jenis
        )
        val bookingKelasJson = Gson().toJson(bookingKelas)
        val StringRequest:StringRequest = object : StringRequest(Method.POST, bookingkelasApi.ADD_URL,
            Response.Listener { response ->
                val gson = Gson()
                val booking = gson.fromJson(response, bookingkelas::class.java)
                if (booking != null) {
                    Toast.makeText(
                        this@DetailsActivity,
                        "Booking Kelas Berhasil Ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                setLoading(false)
            },Response.ErrorListener { error->
                setLoading(false)
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@DetailsActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e: Exception){
                    Toast.makeText(this@DetailsActivity,e.message, Toast.LENGTH_SHORT).show()
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
                params.put("nama_member",nama)
                params.put("nama_kelas",vkelas)
                params.put("tanggal",vtanggal)
                params.put("jam",vjam)
                params.put("jenis",jenis)
                println("jabatan: $params")
                return params
            }
        }
        queue!!.add(StringRequest)
    }

    private fun getName(email:String,pass: String){
        setLoading(true)
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            MemberApi.GET_BY_USERNAME + email + "/" + pass,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                setLoading(false)
                // Check if role is Manajer Operasional
                val data = jsonObject.optJSONObject("data")
                nama = data.optString("nama_member")
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@DetailsActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@DetailsActivity, e.message, Toast.LENGTH_SHORT).show()
                }
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
                params["username"] = vuser
                params["password"] = vpass
                return params
            }
        }
        queue!!.add(StringRequest)
    }

    private fun setLoading(isLoading:Boolean){
        if(isLoading){
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            loading!!.visibility = View.VISIBLE
        }else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            loading!!.visibility = View.INVISIBLE
        }
    }
}