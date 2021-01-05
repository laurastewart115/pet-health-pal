package com.example.pet_health_pal.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.isDigitsOnly
import com.example.pet_health_pal.DatabaseEntities.PetBreed
import com.example.pet_health_pal.DatabaseEntities.PetType
import com.example.pet_health_pal.DatabaseHandler.PetDatabaseHandler
import com.example.pet_health_pal.Entities.Pet
import com.example.pet_health_pal.R
import com.example.pet_health_pal.SharedPreferencesHandler.PetFormDataHandler
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.NumberFormatException
import kotlin.collections.ArrayList

class AddPetActivity : AppCompatActivity() {

    private var bitmap: Bitmap? = null
    private lateinit var AddPet: Button
    private lateinit var PetNameEditText: EditText
    private lateinit var PetAgeEditText: EditText
    private lateinit var petTypeSpinner: Spinner
    private lateinit var petBreedSpinner: Spinner
    private lateinit var petCoatSpinner: Spinner
    private lateinit var petColorSpinner: Spinner
    private lateinit var petGenderSpinner: Spinner
    private lateinit var pet_Image: ImageView
    private lateinit var petFormDataHandler: PetFormDataHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pet)

        AddPet = findViewById<Button>(R.id.AddPet)
        PetNameEditText = findViewById<EditText>(R.id.PetName)
        PetAgeEditText = findViewById<EditText>(R.id.PetAge)
        petTypeSpinner = findViewById<Spinner>(R.id.petTypeSpinner)
        petBreedSpinner = findViewById<Spinner>(R.id.petBreedSpinner)
        petCoatSpinner = findViewById<Spinner>(R.id.petCoatSpinner)
        petColorSpinner = findViewById<Spinner>(R.id.petColorSpinner)
        petGenderSpinner = findViewById<Spinner>(R.id.petGenderSpinner)
        pet_Image = findViewById<ImageView>(R.id.Pet_Image)


        val db = PetDatabaseHandler(this)

        var petTypeList = db.viewPetTypes()
        var petBreedList = db.viewPetBreeds()
        var petTypeNames = ArrayList<String>()

        for (petType: PetType in petTypeList){
            petTypeNames.add(petType.TypeName)
        }


        var PetTypeAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,petTypeNames)
        PetTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        petTypeSpinner.adapter = PetTypeAdapter

        petTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View, p2: Int, p3: Long) {
                val itemselected = petTypeSpinner.selectedItem.toString()
                for(petType: PetType in petTypeList){
                    if(petType.TypeName == itemselected){
                        for(petbreed: PetBreed in petBreedList){
                            if(petbreed.TypeName == itemselected){
                                var petBreedAdapter = ArrayAdapter(p1.context,android.R.layout.simple_spinner_item,petbreed.Breeds)
                                petBreedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                petBreedSpinner.adapter = petBreedAdapter
                                petBreedSpinner.setSelection(0)
                            }
                        }
                            var petCoatAdapter = ArrayAdapter(p1.context,android.R.layout.simple_spinner_item,petType.coats)
                            petCoatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            petCoatSpinner.adapter = petCoatAdapter
                            petCoatSpinner.setSelection(0)

                            var petColorAdapter = ArrayAdapter(p1.context,android.R.layout.simple_spinner_item,petType.colors)
                            petColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            petColorSpinner.adapter = petColorAdapter
                            petColorSpinner.setSelection(0)

                            var petGenderAdapter = ArrayAdapter(p1.context,android.R.layout.simple_spinner_item,petType.genders)
                            petGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            petGenderSpinner.adapter = petGenderAdapter
                            petGenderSpinner.setSelection(0)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        petFormDataHandler = PetFormDataHandler(this)
        var petformdata = petFormDataHandler.getPetFormData()
        if(petformdata != null){
            PetNameEditText.setText(petformdata.Name)
            PetAgeEditText.setText(petformdata.Age.toString())
            if(petformdata.petType.isNotEmpty()){
                petTypeSpinner.setSelection(petformdata.petType.toInt())
            }
            if(petformdata.Breed.isNotEmpty()){
                petBreedSpinner.setSelection(petformdata.Breed.toInt())
            }
            if(petformdata.Coat.isNotEmpty()){
                petCoatSpinner.setSelection(petformdata.Coat.toInt())
            }
            if(petformdata.Color.isNotEmpty()){
                petColorSpinner.setSelection(petformdata.Color.toInt())
            }
            if(petformdata.Gender.isNotEmpty()){
                petGenderSpinner.setSelection(petformdata.Gender.toInt())
            }
        }

        // get image
        val startCameraForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    var stream: InputStream? = null
                    try {
                        if (intent != null) {
                            bitmap = intent.extras?.get("data") as Bitmap
                            pet_Image.setImageBitmap(bitmap)
                        }
                    } catch (ex: Exception) {
                        System.out.println(ex)
                    } finally {
                        try {
                            stream?.close()
                        } catch (ex2: Exception) {
                            System.out.println(ex2)
                        }
                    }
                }
            }

        // open camera
        pet_Image.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA)  == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startCameraForResult.launch(intent)
            } else {
                val requestPermissionLauncher =
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                        if (isGranted) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startCameraForResult.launch(intent)
                        } else {
                            Toast.makeText(
                                this,
                                "Camera Permission is needed to use this functionality",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        AddPet.setOnClickListener {
            if(PetNameEditText.text.isEmpty()){
                PetNameEditText.setError("Please Enter a Name")
            }
            else if(PetAgeEditText.text.isEmpty() || PetAgeEditText.text.toString().toInt() < 0){
                PetAgeEditText.setError("Please enter a valid Age")
            }
            else if(pet_Image.drawable.constantState == resources.getDrawable(android.R.drawable.ic_menu_crop).constantState){
                Toast.makeText(this,"please add image!",Toast.LENGTH_SHORT).show()
            }
            else{
                var Name = PetNameEditText.text.toString()
                var Age = PetAgeEditText.text.toString()
                var petType=""
                var petBreed=""
                var petCoat=""
                var petColor=""
                var petGender=""
                if(petTypeSpinner.selectedItem != null){ petType = petTypeSpinner.selectedItem.toString() }
                if(petBreedSpinner.selectedItem != null){ petBreed = petBreedSpinner.selectedItem.toString() }
                if(petCoatSpinner.selectedItem != null){petCoat = petCoatSpinner.selectedItem.toString()}
                if(petColorSpinner.selectedItem != null){petColor = petColorSpinner.selectedItem.toString()}
                if(petGenderSpinner.selectedItem != null){petGender = petGenderSpinner.selectedItem.toString()}

                var pet = Pet(1,BitmapToByteArray(bitmap!!),Name,Age.toInt(),petType,petBreed,petCoat,petColor,petGender)
                db.addPet(pet)
                petFormDataHandler.ClearAllData()

                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
        }
    }


    override fun onStop() {
        super.onStop()
        savedata()
    }

    override fun onDestroy() {
        super.onDestroy()
        savedata()
    }

    override fun onPause() {
        super.onPause()
        savedata()

    }
    private fun savedata(){
        var Name = PetNameEditText.text.toString()
        var Age: Int
        try {
            Age = PetAgeEditText.text.toString().toInt()
        } catch (e: NumberFormatException) {
            Age = 0
        }

        var petType=""
        var petBreed=""
        var petCoat=""
        var petColor=""
        var petGender=""
        if(petTypeSpinner.selectedItem != null){ petType = petTypeSpinner.selectedItemId.toString() }
        if(petBreedSpinner.selectedItem != null){ petBreed = petBreedSpinner.selectedItemId.toString() }
        if(petCoatSpinner.selectedItem != null){petCoat = petCoatSpinner.selectedItemId.toString()}
        if(petColorSpinner.selectedItem != null){petColor = petColorSpinner.selectedItemId.toString()}
        if(petGenderSpinner.selectedItem != null){petGender = petGenderSpinner.selectedItemId.toString()}
        var petFormInfo = Pet(1, null,
            Name,
            Age,
            petType,
            petBreed,
            petCoat,
            petColor,
            petGender)


        petFormDataHandler.setUserFormData(petFormInfo)
    }

    private fun BitmapToByteArray(bitmap: Bitmap): ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
        return stream.toByteArray()
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap{
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }
}
