package com.example.go_fit.api

class PegawaiApi {
    companion object{
        val BASE_URL = "http://10.53.6.143/Server_Go_Fit/public/"

        val GET_ALL_URL = BASE_URL + "pegawai"
        val GET_BY_USERNAME = BASE_URL + "pegawai/"
        val ADD_URL = BASE_URL + "pegawai"
        val UPDATE_URL = BASE_URL + "pegawai/"
        val DELETE_URL = BASE_URL + "pegawai/"
    }
}