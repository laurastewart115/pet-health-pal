package com.example.pet_health_pal.Entities

import java.io.Serializable

class Pet(
    val id: Int,
    val Image: ByteArray?,
    var Name: String,
    var Age: Int,
    var petType: String,
    var Breed: String,
    var Coat: String,
    var Color:String,
    var Gender: String): Serializable {


}