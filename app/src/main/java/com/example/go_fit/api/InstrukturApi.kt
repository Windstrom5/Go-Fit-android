package com.example.go_fit.api

class InstrukturApi {
    companion object{
        val BASE_URL = "http://192.168.1.2/Server_Go_Fit/public/"

        val GET_ALL_URL = BASE_URL + "instruktur"
        val GET_BY_USERNAME = BASE_URL + "instruktur/"
        val ADD_URL = BASE_URL + "instruktur"
        val UPDATE_URL = BASE_URL + "instruktur/"
        val DELETE_URL = BASE_URL + "instruktur/"
    }
}