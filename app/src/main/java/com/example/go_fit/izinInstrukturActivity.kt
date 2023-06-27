package com.example.go_fit

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
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
import com.example.go_fit.api.jadwalumumapi
import com.example.go_fit.api.presensiInstrukturApi
import com.example.go_fit.databinding.ActivityHomeInstrukturBinding
import com.example.go_fit.databinding.ActivityIzinInstrukturBinding
import com.example.go_fit.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_izin_instruktur.*
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

class izinInstrukturActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener{
    private lateinit var binding: ActivityIzinInstrukturBinding
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
    private lateinit var drawer : DrawerLayout
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var tanggal_layout : TextInputLayout
    private lateinit var tanggal : TextInputEditText
    private lateinit var tanggal2 : String
    private lateinit var loading : LinearLayout
    private lateinit var vkelas :String
    private lateinit var vjam :String
    private lateinit var vtanggal : String
    private var updatedText: String = ""
    private var queue: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIzinInstrukturBinding.inflate(layoutInflater)
        setContentView(binding.root)
        queue= Volley.newRequestQueue(this)
        loading = findViewById(R.id.layout_loading)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.nav_view)
        getBundle()
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
        tanggal_layout = binding.tanggalbooking
        tanggal = binding.etTanggalbooking
        tanggal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Called when the text is being changed
            }

            override fun afterTextChanged(s: Editable?) {
                val updatedText = s.toString()
                val currentDate = getDayNameIndonesia(updatedText) // Convert updated text to Indonesian day format
                getData(vuser,currentDate)
            }
        })
    }

    private fun getData(email:String,hari:String){
        setLoading(true)
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            jadwalumumapi.GET_BY_USERNAME + email + "/" + hari,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                setLoading(false)

                // Check if role is Manajer Operasional
                val data = jsonObject.optJSONObject("data")
                val nama = data?.optString("nama_member")
                println("nama: $nama")
                nama?.let { setUsername(navigationView, it) }
                setExposeDropDownMenu(vuser, hari, jam)
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@izinInstrukturActivity,
                        "Anda Tidak Mempunyai Kelas Di Tanggal Tersebut",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@izinInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun getName(email:String,pass: String){
        setLoading(true)
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            InstrukturApi.GET_BY_USERNAME + email + "/" + pass,
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
                        this@izinInstrukturActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@izinInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun setExposeDropDownMenu(namainstruktur: String, tanggal: String, jam : String) {
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,
            jadwalumumapi.GET_ALL_URL,
            Response.Listener { response ->
                val loginList = mutableListOf<String>() // Replace with the appropriate function to get the current day name in Indonesian format
                val jsonObject = JSONObject(response)
                val data = jsonObject.optJSONArray("data")

                if (data != null && data.length() > 0) {
                    for (i in 0 until data.length()) {
                        val item = data.getJSONObject(i)
                        val hari = item.optString("hari")
                        val nama = item.optString("nama")
                        val itemJam = item.optString("jam")

                        if (nama != namainstruktur &&
                            (hari != tanggal && itemJam != jam)
                        ) {
                            loginList.add(nama)
                        }
                    }
                }
                val adapterList = ArrayAdapter(this, R.layout.item_list, loginList)
                ed_pengganti.setAdapter(adapterList)
            },
            Response.ErrorListener { error ->
                // Handle error
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }
        requestQueue.add(stringRequest)
    }

    fun getDayNameIndonesia(updatedText: String): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Adjust the date format as per your requirement
        val date = formatter.parse(updatedText)
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Map the day of week to the corresponding Indonesian day name
        val dayNames = arrayOf(
            "Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"
        )

        return dayNames[dayOfWeek - 1]
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menulogout) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@izinInstrukturActivity)
            builder.setMessage("Want to log out?")
                .setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {

                    }
                })
                .setPositiveButton("YES", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {
                        startActivity(Intent(this@izinInstrukturActivity, MainActivity::class.java))
                    }
                })
                .show()
        }else if(item.itemId == R.id.menuIzin){
            setLoading(true)
            val StringRequest: StringRequest = object : StringRequest(
                Method.GET,
                presensiInstrukturApi.GET_BY_USERNAME + vkelas + "/" + vtanggal + "/" + vjam,
                Response.Listener { response ->
                    setLoading(false)
                    val nama_kelas = textViewClasses.getText().toString()
                    val intent = Intent(this,PresensiKelasActivity::class.java)
                    val mBundle = Bundle()
                    mBundle.putString("username",vuser)
                    mBundle.putString("password",vpass)
                    mBundle.putString("kelas",vkelas)
                    mBundle.putString("tanggal",vtanggal)
                    mBundle.putString("jam",vjam)
                    intent.putExtra("profile",mBundle)
                    startActivity(intent)
                },
                Response.ErrorListener { error ->
                    setLoading(false)
                    try {
                        val responseBody =
                            String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        Toast.makeText(
                            this@izinInstrukturActivity,
                            "Anda Belum Presensi Untuk kelas Ini",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@izinInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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
        }else if(item.itemId == R.id.menuPresensi){
            setLoading(true)
            val StringRequest: StringRequest = object : StringRequest(
                Method.GET,
                presensiInstrukturApi.GET_BY_USERNAME + vkelas + "/" + vtanggal + "/" + vjam,
                Response.Listener { response ->
                    setLoading(false)
                    val nama_kelas = textViewClasses.getText().toString()
                    val intent = Intent(this,PresensiKelasActivity::class.java)
                    val mBundle = Bundle()
                    mBundle.putString("username",vuser)
                    mBundle.putString("password",vpass)
                    mBundle.putString("kelas",vkelas)
                    mBundle.putString("tanggal",vtanggal)
                    mBundle.putString("jam",jam)
                    intent.putExtra("profile",mBundle)
                    startActivity(intent)
                },
                Response.ErrorListener { error ->
                    setLoading(false)
                    try {
                        val responseBody =
                            String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        Toast.makeText(
                            this@izinInstrukturActivity,
                            "Anda Belum Presensi Untuk kelas Ini",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@izinInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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
        }else if(item.itemId == R.id.historyclassInstruktur){
            setLoading(true)
            val StringRequest: StringRequest = object : StringRequest(
                Method.GET,
                presensiInstrukturApi.GET_BY_USERNAME + vkelas + "/" + vtanggal + "/" + vjam,
                Response.Listener { response ->
                    setLoading(false)
                    val nama_kelas = textViewClasses.getText().toString()
                    val intent = Intent(this,HistoryInstrukturActivity::class.java)
                    val mBundle = Bundle()
                    mBundle.putString("username",vuser)
                    mBundle.putString("password",vpass)
                    mBundle.putString("kelas",vkelas)
                    mBundle.putString("tanggal",vtanggal)
                    mBundle.putString("jam",jam)
                    intent.putExtra("profile",mBundle)
                    startActivity(intent)
                },
                Response.ErrorListener { error ->
                    setLoading(false)
                    try {
                        val responseBody =
                            String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        Toast.makeText(
                            this@izinInstrukturActivity,
                            "Anda Belum Presensi Untuk kelas Ini",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@izinInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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
        drawer.closeDrawer(GravityCompat.START)
        return true
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
                vkelas = mbunlde.getString("nama_kelas")!!
                vjam = mbunlde.getString("jam")!!
                vtanggal = mbunlde.getString("tanggal")!!
                println("jabatan: $vuser")
            }
        }catch(e: NullPointerException) {

        }
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

}