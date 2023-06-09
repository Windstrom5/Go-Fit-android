package com.example.go_fit

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.go_fit.api.InstrukturApi
import com.example.go_fit.api.MemberApi
import com.example.go_fit.api.jadwalharianApi
import com.example.go_fit.api.jadwalumumapi
import com.example.go_fit.databinding.ActivityHomeInstrukturBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class HomeInstrukturActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding : ActivityHomeInstrukturBinding
    private lateinit var loading : LinearLayout
    private lateinit var value1 : TextView
    private lateinit var value2 : TextView
    private lateinit var vuser : String
    private lateinit var vpass : String
    private lateinit var navigationView : NavigationView
    private lateinit var name: TextView
    private lateinit var mbunlde : Bundle
    private lateinit var toolbar: Toolbar
    private lateinit var jam :String
    private lateinit var textViewClasses : TextView
    private lateinit var textViewjmlhPeserta :TextView
    private lateinit var drawer : DrawerLayout
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var tanggal : String
    private var savedHari: String? = null
    private var queue: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeInstrukturBinding.inflate(layoutInflater)
        setContentView(binding.root)
        queue= Volley.newRequestQueue(this)
        loading = findViewById(R.id.layout_loading)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.nav_view)
        getBundle()
        setValue(vuser,vpass)
        getName(vuser,vpass)
        toolbar.setNavigationOnClickListener(){
            drawer =findViewById(R.id.drawer_layout)
            navigationView.setNavigationItemSelectedListener(this)
            toggle = ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close)
            drawer.addDrawerListener(toggle)
            toggle.syncState()
        }
        drawer =findViewById(R.id.drawer_layout)
        navigationView.setNavigationItemSelectedListener(this)
        toggle = ActionBarDrawerToggle(this,drawer,toolbar,
            R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        setSupportActionBar(toolbar);
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
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

    private fun getBundle(){
        try{
            mbunlde = intent?.getBundleExtra("profile")!!
            if(mbunlde != null){
                vuser =mbunlde.getString("username")!!
                vpass = mbunlde.getString("password")!!
                println("jabatan: $vuser")
            }
        }catch(e: NullPointerException) {

        }
    }

    private fun getName(email:String,pass: String){
        setLoading(true)
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            InstrukturApi.GET_BY_USERNAME + email + "/" + pass + "/" + "get",
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                setLoading(false)

                // Check if role is Manajer Operasional
                val data = jsonObject.optJSONObject("data")
                val nama = data?.optString("nama_member")
                println("nama: $nama")
                nama?.let { setUsername(navigationView, it) }
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@HomeInstrukturActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@HomeInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun setUsername(navigationView: NavigationView, username: String) {
        println("status: $username")
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.username_show)
        usernameTextView.text = username
    }

    private fun setValue(email: String,pass: String){
        setLoading(true)
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            InstrukturApi.GET_BY_USERNAME + email + "/" + pass,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val data = jsonObject.optJSONObject("data")
                val nama = data?.optString("email")
                if (nama != null) {
                    getjumlahKelas(nama)
                    setnextKelas(nama)
                }
                name = binding.textView
                name.setText(nama)
                val valuekelas = data?.optString("keterlambatan")
                value2 = binding.value2
                value2.setText(valuekelas)
                println("nama: $nama")
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@HomeInstrukturActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@HomeInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun getjumlahKelas(nama:String){
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            jadwalumumapi.GET_BY_USERNAME + nama,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val dataArray = jsonObject.optJSONArray("data")
                val count = dataArray?.length() ?: 0
                value1 = binding.value1
                value1.text = count.toString()
                println("nama: $nama")
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@HomeInstrukturActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@HomeInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun setnextKelas(nama: String){
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            jadwalharianApi.GET_BY_USERNAME + nama,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val data = jsonObject.optJSONArray("data")
                textViewClasses = binding.textViewClasses
                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, 0)
                today.set(Calendar.MINUTE, 0)
                today.set(Calendar.SECOND, 0)
                val currentTime = today.time
                var closestDate: Date? = null
                var closestItem: JSONObject? = null

                // Find the closest date data from the current time
                if (data != null) {
                    for (i in 0 until data.length()) {
                        val item = data.optJSONObject(i)
                        val tanggalKelas = item.optString("tanggal_kelas")
                        val jam = item.optString("jam")
                        val dateTimeString = "$tanggalKelas $jam"
                        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateTimeString)
                        // Compare the date with the current time
                        if (dateTime != null && dateTime.after(currentTime) && (closestDate == null || dateTime.before(closestDate))) {
                            closestDate = dateTime
                            closestItem = item
                        }
                    }
                }
                // Perform the desired action with the closestItem
                if (closestItem != null) {
                    val namaKelas = closestItem.optString("nama_kelas")
                    val hari = closestItem.optString("hari")
                    val nama = closestItem.optString("nama")
                    val jam = closestItem.optString("jam")
                    val tanggal = closestItem.optString("tanggal_kelas")
                    textViewClasses.text = namaKelas
                    jumlahPeserta(nama, hari, jam,tanggal)
                    println("Closest item: $closestItem")
                }
                println("nama: $nama")
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@HomeInstrukturActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@HomeInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun jumlahPeserta(nama:String,hari:String,jam_kelas:String,tanggal_kelas:String){
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            jadwalharianApi.GET_BY_USERNAME + nama + "/" + hari+"/"+jam_kelas +"/"+tanggal_kelas,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                println("nama: $jsonObject")
                val response = JSONObject(response) // Convert the response string to JSONObject
                val totalData = response.optInt("totaldata")
                if (totalData > 0) {
                    val dataArray = response.getJSONArray("data") // Get the data array from the response
                    val data = dataArray.getJSONObject(0) // Get the first object from the data array
                    val total = data.optString("sisa_peserta")
                    tanggal = data.optString("tanggal_kelas")
                    jam = data.optString("jam")
                    println("total: $total")
                    val jumlah = 10 - (total?.toIntOrNull() ?: 0)
                    textViewjmlhPeserta = binding.textViewjmlhPeserta
                    textViewjmlhPeserta.text = jumlah.toString()
                } else {
                    // Handle the case when no data is returned
                }
                println("nama: $nama")
                setLoading(false)
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@HomeInstrukturActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@HomeInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menulogout) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@HomeInstrukturActivity)
            builder.setMessage("Want to log out?")
                .setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {

                    }
                })
                .setPositiveButton("YES", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {
                        startActivity(Intent(this@HomeInstrukturActivity, MainActivity::class.java))
                    }
                })
                .show()
        }else if(item.itemId == R.id.menuProfile){
            val intent = Intent(this,ProfileMemberActivity::class.java)
            val mBundle = Bundle()
            mBundle.putString("username",vuser)
            mBundle.putString("password",vpass)
            intent.putExtra("profile",mBundle)
            startActivity(intent)
        }else if(item.itemId == R.id.menuGym){

        }else if(item.itemId == R.id.menuPresensi){
            val nama_kelas = textViewClasses.getText().toString()
            val intent = Intent(this,PresensiKelasActivity::class.java)
            val mBundle = Bundle()
            mBundle.putString("username",vuser)
            mBundle.putString("password",vpass)
            mBundle.putString("kelas",nama_kelas)
            mBundle.putString("tanggal",tanggal)
            mBundle.putString("jam",jam)
            intent.putExtra("profile",mBundle)
            startActivity(intent)
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        Log.d("CDA", "onBackPressed Called")
        val builder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(this@HomeInstrukturActivity)
        builder.setMessage("Want to log out?")
            .setNegativeButton("No", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {

                }
            })
            .setPositiveButton("YES", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    startActivity(Intent(this@HomeInstrukturActivity, MainActivity::class.java))
                }
            })
            .show()
    }
}