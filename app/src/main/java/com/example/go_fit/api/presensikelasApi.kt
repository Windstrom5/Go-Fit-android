package com.example.go_fit.api

class presensikelasApi {
    companion object{
        val BASE_URL = "http://192.168.1.5/Server_Go_Fit/public/"

        val GET_ALL_URL = BASE_URL + "presensikelas"
        val GET_BY_USERNAME = BASE_URL + "presensikelas/"
        val ADD_URL = BASE_URL + "presensikelas"
        val UPDATE_URL = BASE_URL + "presensikelas/"
        val DELETE_URL = BASE_URL + "presensikelas/"
    }
}