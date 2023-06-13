package com.example.go_fit.member

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.go_fit.*
import com.example.go_fit.api.MemberApi
import com.example.go_fit.api.bookinggymApi
import com.example.go_fit.api.bookingkelasApi
import com.example.go_fit.databinding.ActivityBookingGymBinding
import com.example.go_fit.databinding.ActivityHomeMemberBinding
import com.example.go_fit.databinding.ActivityMainBinding
import com.example.go_fit.model.bookinggym
import com.example.go_fit.model.bookingkelas
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_booking_gym.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class BookingGymActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {
    private var queue: RequestQueue? = null
    private lateinit var binding: ActivityBookingGymBinding
    private lateinit var bookgym : CardView
    private lateinit var mbunlde : Bundle
    private lateinit var vuser:String
    private lateinit var vpass:String
    private lateinit var name: TextView
    private lateinit var drawer : DrawerLayout
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var navigationView : NavigationView
    private lateinit var value1 : TextView
    private lateinit var value2 : TextView
    private lateinit var loading : LinearLayout
    private lateinit var toolbar: Toolbar
    private lateinit var book :Button
    private lateinit var waktu : AutoCompleteTextView
    private lateinit var tanggal : TextInputLayout
    private val calender = Calendar.getInstance()
    private val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    companion object{
        private val JAM_LIST = arrayOf(
            "7-9",
            "9-11",
            "11-13",
            "13-15",
            "15-17",
            "17-19",
            "19-21"
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingGymBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()?.hide()
        setExposeDropDownMenu()
        getBundle()
        book = binding.button2
        waktu = binding.edWaktu
        loading = findViewById(R.id.layout_loading)
        queue= Volley.newRequestQueue(this)
        tanggal= binding.tanggalbooking
        getName(vuser,vpass)
        tanggal.setStartIconOnClickListener(View.OnClickListener{
            DatePickerDialog(this,this,calender.get(Calendar.YEAR),calender.get(Calendar.MONTH),calender.get(
                Calendar.DAY_OF_MONTH)).show()
        })
        tanggal!!.setFocusable(false)
        book.setOnClickListener {
            saveValue(vuser,vpass)
        }
    }
    private fun getJamMasuk(jam: String): String {
        return jam.split("-")[0]
    }

    private fun getJamKeluar(jam: String): String {
        return jam.split("-")[1]
    }

    private fun setExposeDropDownMenu() {
        val adapterList: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.item_list, JAM_LIST)
        waktu.setAdapter(adapterList)
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

    override fun onDateSet(view: DatePicker?, year:Int, month:Int , dayofMonth : Int) {
        Log.e("Calender","$year -- $month -- $dayofMonth")
        calender.set(year, month, dayofMonth)
        displayFormattedDate(calender.timeInMillis)
    }

    private fun displayFormattedDate(timestamp: Long){
        findViewById<EditText>(R.id.et_tanggalbooking).setText(formatter.format(timestamp))
        Log.i("Formatting",timestamp.toString())
    }

    private fun saveValue(email: String,pass: String){
        setLoading(true)
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            MemberApi.GET_BY_USERNAME + email + "/" + pass,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                // Check if role is Manajer Operasional
                val data = jsonObject.optJSONObject("data")
                val nama = data.optString("nama_member")
                val selectedJam = waktu.text.toString() // Assuming waktu is the dropdown menu view
                val jamMasuk = getJamMasuk(selectedJam)
                val jamKeluar = getJamKeluar(selectedJam)
                setLoading(true)
                val tanggal = et_tanggalbooking!!.getText().toString()
                val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

                val date = formatter.parse(tanggal) // Parse the date string
                val resultFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val tanggal_booking = resultFormatter.format(date) // Format the date with the desired pattern
                val bookingGym = bookinggym(
                    nama,
                    jamMasuk,
                    tanggal_booking,
                    jamKeluar
                )
                val bookingKelasJson = Gson().toJson(bookingGym)
                val StringRequest:StringRequest = object : StringRequest(Method.POST, bookinggymApi.ADD_URL,
                    Response.Listener { response ->
                        val gson = Gson()
                        val booking = gson.fromJson(response, bookingkelas::class.java)
                        if (booking != null) {
                            Toast.makeText(
                                this@BookingGymActivity,
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
                                this@BookingGymActivity,
                                errors.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }catch (e: Exception){
                            Toast.makeText(this@BookingGymActivity,e.message, Toast.LENGTH_SHORT).show()
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
                        params.put("tanggal",tanggal_booking)
                        params.put("jam_masuk",jamMasuk)
                        params.put("jam_keluar",jamKeluar)
                        println("jabatan: $params")
                        return params
                    }
                }
                queue!!.add(StringRequest)
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@BookingGymActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@BookingGymActivity, e.message, Toast.LENGTH_SHORT).show()
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
            MemberApi.GET_BY_USERNAME + email + "/" + pass,
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
                        this@BookingGymActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@BookingGymActivity, e.message, Toast.LENGTH_SHORT).show()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menulogout) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@BookingGymActivity)
            builder.setMessage("Want to log out?")
                .setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {

                    }
                })
                .setPositiveButton("YES", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {
                        startActivity(Intent(this@BookingGymActivity, MainActivity::class.java))
                    }
                })
                .show()
        }else if(item.itemId == R.id.menuProfile){
            val intent = Intent(this, ProfileMemberActivity::class.java)
            val mBundle = Bundle()
            mBundle.putString("username",vuser)
            mBundle.putString("password",vpass)
            intent.putExtra("profile",mBundle)
            startActivity(intent)
        }else if(item.itemId == R.id.menuGym){
            val intent = Intent(this, BookingGymActivity::class.java)
            val mBundle = Bundle()
            mBundle.putString("username",vuser)
            mBundle.putString("password",vpass)
            intent.putExtra("profile",mBundle)
            startActivity(intent)
        }else if(item.itemId == R.id.menuKelas){
            val intent = Intent(this, JadwalHarianActivity::class.java)
            val mBundle = Bundle()
            mBundle.putString("username",vuser)
            mBundle.putString("password",vpass)
            intent.putExtra("profile",mBundle)
            startActivity(intent)
        }else if(item.itemId == R.id.historygym){
            val intent = Intent(this, GymHistoryActivity::class.java)
            val mBundle = Bundle()
            mBundle.putString("username",vuser)
            mBundle.putString("password",vpass)
            intent.putExtra("profile",mBundle)
            startActivity(intent)
        }else if(item.itemId == R.id.historyclass){
            val intent = Intent(this, HistoryKelasActivity::class.java)
            val mBundle = Bundle()
            mBundle.putString("username",vuser)
            mBundle.putString("password",vpass)
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
}