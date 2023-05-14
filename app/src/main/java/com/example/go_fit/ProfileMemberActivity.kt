package com.example.go_fit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.go_fit.api.MemberApi
import com.example.go_fit.databinding.ActivityProfileMemberBinding
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.HashMap

class ProfileMemberActivity : AppCompatActivity() {
    private lateinit var vuser:String
    private lateinit var mbunlde : Bundle
    private lateinit var vpass:String
    private lateinit var username : TextView
    private lateinit var kelas : TextView
    private lateinit var uang : TextView
    private lateinit var emailview : TextView
    private lateinit var status : TextView
    private lateinit var phonenum : TextView
    private lateinit var binding : ActivityProfileMemberBinding
    private var queue: RequestQueue? = null
    private lateinit var loading : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = findViewById(R.id.layout_loading)
        queue= Volley.newRequestQueue(this)
        getBundle()
        setValue(vuser,vpass)
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

    private fun setValue(email: String,pass: String){
        setLoading(true)
        val StringRequest: StringRequest = object : StringRequest(
            Method.GET,
            MemberApi.GET_BY_USERNAME + email + "/" + pass + "/" + "get",
            Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                // Check if role is Manajer Operasional
                username = binding.username
                kelas = binding.depositkelas
                uang = binding.deposituang
                status = binding.status
                emailview = binding.email
                phonenum = binding.phonenum
                val data = jsonObject.optJSONObject("data")
                val nama = data?.optString("nama_member")
                val valueuang = data?.optString("deposit_uang")
                val valuekelas = data?.optString("deposit_kelas")
                val emaildata = data?.optString("email")
                val no_telp = data?.optString("no_telp")
                username.setText(nama)
                uang.setText(valueuang)
                kelas.setText(valuekelas)
                emailview.setText(emaildata)
                phonenum.setText(no_telp)
                setLoading(false)
                println("nama: $nama")
            },
            Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@ProfileMemberActivity,
                        "Akun Belum Terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@ProfileMemberActivity, e.message, Toast.LENGTH_SHORT).show()
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

    fun getBundle(){
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
}