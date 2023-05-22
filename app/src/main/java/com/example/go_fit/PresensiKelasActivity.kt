package com.example.go_fit

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.go_fit.adapter.MemberAdapter
import com.example.go_fit.adapter.ScheduleAdapter
import com.example.go_fit.api.InstrukturApi
import com.example.go_fit.api.bookingkelasApi
import com.example.go_fit.api.jadwalharianApi
import com.example.go_fit.databinding.ActivityPresensiKelasBinding
import com.example.go_fit.model.jadwalharian
import com.example.go_fit.model.member
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.HashMap

class PresensiKelasActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding : ActivityPresensiKelasBinding
    private lateinit var loading : LinearLayout
    private lateinit var mbunlde : Bundle
    private lateinit var toolbar: Toolbar
    private lateinit var vuser:String
    private lateinit var vpass:String
    private lateinit var vkelas:String
    private lateinit var vjam:String
    private lateinit var vtanggal:String
    private lateinit var drawer : DrawerLayout
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var navigationView : NavigationView
    private lateinit var recyclerView: RecyclerView
    private var adapter: MemberAdapter? = null
    private lateinit var memberAdapter: MemberAdapter
    private var queue: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresensiKelasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = findViewById(R.id.rv_item)
        loading = findViewById(R.id.layout_loading)
        queue = Volley.newRequestQueue(this)
        toolbar = findViewById(R.id.toolbar)
        drawer =findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
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
        getBundle()
        getName(vuser,vpass)
        loadMember(vuser,vkelas,vtanggal)
    }

    fun setLoading(isLoading:Boolean){
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

    private fun loadMember(email:String,kelas: String,tanggal : String) {
        // Show loading spinner
        setLoading(true)
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            bookingkelasApi.GET_BY_USERNAME +email+"/"+ kelas +"/"+ tanggal,
            Response.Listener { response ->
                val gson = Gson()
                setLoading(false)
                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("data")
                val itemList : List<member> = gson.fromJson(jsonArray.toString(), Array<member>::class.java).toList()
                recyclerView = binding.rvItem
                adapter = MemberAdapter(itemList,this)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter= adapter
                adapter!!.getVariable(vuser,vpass,vkelas,vtanggal,vjam)
                recyclerView.visibility = View.VISIBLE
                if(!itemList.isEmpty()){
                    Toast.makeText(this@PresensiKelasActivity,"Data Berhasil Diambil", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@PresensiKelasActivity,"Data Kosong", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener { error->
                try{
                    setLoading(false)
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    recyclerView = binding.rvItem
                    recyclerView.visibility = View.GONE
                    Toast.makeText(
                        this@PresensiKelasActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e: Exception){
                    Toast.makeText(this@PresensiKelasActivity,e.message, Toast.LENGTH_SHORT).show()
                }
            }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String,String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }
        queue!!.add(StringRequest)
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
                        this@PresensiKelasActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@PresensiKelasActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun getBundle(){
        try{
            mbunlde = intent?.getBundleExtra("profile")!!
            if(mbunlde != null){
                vuser =mbunlde.getString("username")!!
                vpass = mbunlde.getString("password")!!
                vkelas = mbunlde.getString("kelas")!!
                vtanggal = mbunlde.getString("tanggal")!!
                vjam = mbunlde.getString("jam")!!
                println("jabatan: $vuser")
            }
        }catch(e: NullPointerException) {

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menulogout) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@PresensiKelasActivity)
            builder.setMessage("Want to log out?")
                .setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {

                    }
                })
                .setPositiveButton("YES", object : DialogInterface.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {
                        startActivity(Intent(this@PresensiKelasActivity, MainActivity::class.java))
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
            val intent = Intent(this,PresensiKelasActivity::class.java)
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