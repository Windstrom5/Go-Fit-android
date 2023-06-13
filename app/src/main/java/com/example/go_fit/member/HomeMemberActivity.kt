package com.example.go_fit.member

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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
import com.example.go_fit.api.*
import com.example.go_fit.databinding.ActivityHomeMemberBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home_member.*
import kotlinx.android.synthetic.main.schedule_item.view.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.HashMap

class HomeMemberActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private var queue: RequestQueue? = null
    private lateinit var binding: ActivityHomeMemberBinding
    private lateinit var bookgym : CardView
    private lateinit var mbunlde : Bundle
    private lateinit var bookclass : CardView
    private lateinit var scheadule : CardView
    private lateinit var aboutus : CardView
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar)
        loading = findViewById(R.id.layout_loading)
        queue= Volley.newRequestQueue(this)
        bookclass = binding.cardviewBookclass
        bookgym = binding.cardviewBookgym
        scheadule = binding.cardviewJadwal
        aboutus = binding.cardviewabout
        navigationView = findViewById(R.id.nav_view)
        getBundle()
        getName(vuser,vpass)
        setValue(vuser,vpass)
        toolbar.setNavigationOnClickListener(){
            drawer =findViewById(R.id.drawer_layout)
            navigationView.setNavigationItemSelectedListener(this)
            toggle = ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()
        }
        drawer =findViewById(R.id.drawer_layout)
        navigationView.setNavigationItemSelectedListener(this)
        toggle = ActionBarDrawerToggle(this,drawer,toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        setSupportActionBar(toolbar);
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
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

    private fun setValue(email: String,pass: String){
        setLoading(true)
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            MemberApi.GET_BY_USERNAME + email + "/" + pass,
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                // Check if role is Manajer Operasional
                val data = jsonObject.optJSONObject("data")
                val nama = data?.optString("nama_member")
                name = binding.textView
                name.setText(nama)
                val valueuang = data?.optString("deposit_uang")
                val valuekelas = data?.optString("deposit_kelas")
                value1 = binding.value1
                value2 = binding.value2
                value1.setText(valueuang)
                value2.setText(valuekelas)
                println("nama: $nama")
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@HomeMemberActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@HomeMemberActivity, e.message, Toast.LENGTH_SHORT).show()
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
                MemberApi.GET_BY_USERNAME + email + "/" + pass + "/",
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
                            this@HomeMemberActivity,
                            "Akun Belum Terdaftar",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@HomeMemberActivity, e.message, Toast.LENGTH_SHORT).show()
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
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@HomeMemberActivity)
            builder.setMessage("Want to log out?")
                .setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {

                    }
                })
                .setPositiveButton("YES", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {
                        startActivity(Intent(this@HomeMemberActivity, MainActivity::class.java))
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
        }else if(item.itemId == R.id.historyclass){
            val intent = Intent(this, HistoryKelasActivity::class.java)
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
        val builder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(this@HomeMemberActivity)
        builder.setMessage("Want to log out?")
            .setNegativeButton("No", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {

                }
            })
            .setPositiveButton("YES", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    startActivity(Intent(this@HomeMemberActivity, MainActivity::class.java))
                }
            })
            .show()
    }
}

