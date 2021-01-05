package com.example.pet_health_pal.DatabaseHandler

interface AsyncResponse {
    fun onResponseRecieved(s: String,b : Boolean?)
    fun onErrorListener(s: String)
}