package com.example.go_fit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.example.go_fit.databinding.ActivityDetailsBinding

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
    private lateinit var vjam:String
    private lateinit var vinstruktur :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        kelas = binding.kelas
        instruktur = binding.instruktur
        harga = binding.harga
        tanggal = binding.tanggal
        loading = findViewById(R.id.layout_loading)
        getBundle()
        kelas.setText(vkelas)
        instruktur.setText(vinstruktur)
        harga.setText(vharga)
        tanggal.setText(vtanggal)
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

    private fun bookingKelas(username : String,){

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