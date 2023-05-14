package com.example.go_fit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.RequestQueue
import com.example.go_fit.databinding.ActivityHomeMemberBinding
import com.example.go_fit.databinding.ActivityHomePegawaiBinding
import com.google.android.material.navigation.NavigationView

class HomePegawaiActivity : AppCompatActivity() {
    private var queue: RequestQueue? = null
    private lateinit var binding: ActivityHomePegawaiBinding
    private lateinit var bookgym : CardView
    private lateinit var mbunlde : Bundle
    private lateinit var countinstruktur : CardView
    private lateinit var countclass : CardView
    private lateinit var aboutus : CardView
    private lateinit var vuser:String
    private lateinit var vpass:String
    private lateinit var name: TextView
    private lateinit var drawer : DrawerLayout
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var vjenis:String
    private lateinit var navigationView : NavigationView
    private lateinit var value1 : TextView
    private lateinit var value2 : TextView
    private lateinit var loading : LinearLayout
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePegawaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar)
        loading = findViewById(R.id.layout_loading)
    }
}