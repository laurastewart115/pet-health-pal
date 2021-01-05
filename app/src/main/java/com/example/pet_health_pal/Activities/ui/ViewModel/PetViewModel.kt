package com.example.pet_health_pal.Activities.ui.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pet_health_pal.Entities.Pet

class PetViewModel :ViewModel() {

    private val pet = MutableLiveData<Pet>()

    fun setPetData(pet: Pet){
        this.pet.value = pet
    }

    fun getpetData() : LiveData<Pet>{
        return pet
    }
}