package com.example.go_fit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.go_fit.databinding.ActivityPresensiKelasBinding

class PresensiKelasActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPresensiKelasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presensi_kelas)
    }
}