package com.example.go_fit.member

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.go_fit.R
import kotlinx.android.synthetic.main.activity_main.*

class BookingGymActivity : AppCompatActivity() {
    private lateinit var vuser : String
    private lateinit var vpass : String
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
        setContentView(R.layout.activity_booking_gym)
    }
    fun setExposeDropDownMenu() {
        val adapterList: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.item_list, JAM_LIST)
        ed_jenis!!.setAdapter(adapterList)
    }
}