package com.example.go_fit.api

class bookingkelasApi {
    companion object{
        val BASE_URL = "http://192.168.1.6/Server_Go_Fit/public/"

        val GET_ALL_URL = BASE_URL + "bookingkelas"
        val GET_BY_USERNAME = BASE_URL + "bookingkelas/"
        val ADD_URL = BASE_URL + "bookingkelas"
        val UPDATE_URL = BASE_URL + "bookingkelas/"
        val DELETE_URL = BASE_URL + "bookingkelas/"
    }
}