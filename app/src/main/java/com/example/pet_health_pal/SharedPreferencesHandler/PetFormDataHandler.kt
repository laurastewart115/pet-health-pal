package com.example.pet_health_pal.SharedPreferencesHandler

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.pet_health_pal.Entities.Pet
import com.google.gson.Gson

class PetFormDataHandler(activity: Activity)
{
    private val PET_REFERENCE = "Pet"
    private val PREFERENCES_REFRENCES = "FormInfo"
    private var sharedPreferences: SharedPreferences? = activity.getSharedPreferences(PREFERENCES_REFRENCES, Context.MODE_PRIVATE)
    private var gson : Gson = Gson()

    fun setUserFormData(pet: Pet) {
        val editor = sharedPreferences!!.edit()
        if (pet != null) {
            val jsonCustomer: String = gson?.toJson(pet)
            editor.putString(PET_REFERENCE, jsonCustomer)
        }
        editor.commit()
    }

    fun getPetFormData(): Pet? {
        val json = sharedPreferences!!.getString(PET_REFERENCE, null)
        return if (json != null) {
            gson.fromJson(json, Pet::class.java)
        } else null
    }

    fun ClearAllData() {
        sharedPreferences!!.edit().clear().commit()
    }
}