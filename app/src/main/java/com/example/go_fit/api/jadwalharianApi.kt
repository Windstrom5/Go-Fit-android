package com.example.go_fit.api

class jadwalharianApi {
    companion object{
        val BASE_URL = "http://192.168.1.3/Server_Go_Fit/public/"

        val GET_ALL_URL = BASE_URL + "jadwalharian"
        val GET_BY_USERNAME = BASE_URL + "jadwalharian/"
        val ADD_URL = BASE_URL + "jadwalharian"
        val UPDATE_URL = BASE_URL + "jadwalharian/"
        val DELETE_URL = BASE_URL + "jadwalharian/"
    }
}