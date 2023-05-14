package com.example.go_fit.api

class deposituangApi {
    companion object{
        val BASE_URL = "http://192.168.1.3/Server_Go_Fit/public/"

        val GET_ALL_URL = BASE_URL + "deposituang"
        val GET_BY_USERNAME = BASE_URL + "deposituang/"
        val ADD_URL = BASE_URL + "deposituang"
        val UPDATE_URL = BASE_URL + "deposituang/"
        val DELETE_URL = BASE_URL + "deposituang/"
    }
}