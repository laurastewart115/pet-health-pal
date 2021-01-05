package com.example.pet_health_pal.DatabaseHandler

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.pet_health_pal.DatabaseEntities.PetBreed
import com.example.pet_health_pal.DatabaseEntities.PetType
import com.example.pet_health_pal.Entities.HealthNote
import com.example.pet_health_pal.Entities.Pet
import com.example.pet_health_pal.Entities.RecurringReminder
import org.apache.commons.lang3.StringUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class PetDatabaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "PetDatabase"
        private val TABLE_PETS = "PetTable"
        private val TABLE_PETTYPE = "PetTypeTable"
        private val TABLE_PETBREED = "PetBreedTable"
        private val TABLE_RECURRING_REMINDER = "PetRecurringReminder"
        private val TABLE_HEALTH_NOTE="PetHealthNote"
        //Pet Table
        private val KEY_PET_ID = "id"
        private val KEY_PET_IMAGE = "Image"
        private val KEY_PET_NAME = "Name"
        private val KEY_PET_AGE = "Age"
        private val KEY_PET_TYPE = "Type"
        private val KEY_PET_BREED = "Breed"
        private val KEY_PET_COAT = "Coat"
        private val KEY_PET_COLOR = "Color"
        private val KEY_PET_GENDER = "Gender"
        //PetType Table
        private val KEY_PETTYPE_ID = "id"
        private val KEY_PETTYPE_TYPENAME = "TypeName"
        private val KEY_PETTYPE_COATS = "Coats"
        private val KEY_PETTYPE_COLORS = "Colors"
        private val KEY_PETTYPE_GENDERS = "Genders"
        //PetBreed Table
        private val KEY_PETBREED_ID = "id"
        private val KEY_PETBREED_TYPENAME = "TypeName"
        private val KEY_PETBREED_BREEDS = "Breeds"
        //RecurringReminder Table
        private val KEY_RECURRING_REMINDER_ID = "id"
        private val KEY_RECURRING_REMINDER_TITLE = "Title"
        private val KEY_RECURRING_REMINDER_WEEKDAY = "Weekday"
        private val KEY_RECURRING_REMINDER_ACTIVE = "Active"
        private val KEY_RECURRING_REMINDER_TIME = "Time"
        private val KEY_RECURRING_REMINDER_PET_ID = "pet_id"
        // HealthNote Table
        private val KEY_HEALTH_NOTE_ID = "id"
        private val KEY_HEALTH_NOTE_DATE = "Date"
        private val KEY_HEALTH_NOTE_TITLE = "Title"
        private val KEY_HEALTH_NOTE_NOTE_TEXT = "NoteText"
        private val KEY_HEALTH_NOTE_PET_ID = "pet_id"

    }

    override fun onCreate(db: SQLiteDatabase?)
    {
        //Creates all three tables
        CreatePetTable(db)
        CreatePetTypeTable(db)
        CreatePetBreedTable(db)
        CreatePetRecurringReminderTable(db)
        CreatePetHealthNoteTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS" + TABLE_PETS)
        db!!.execSQL("DROP TABLE IF EXISTS" + TABLE_PETTYPE)
        db!!.execSQL("DROP TABLE IF EXISTS" + TABLE_PETBREED)
        db!!.execSQL("DROP TABLE IF EXISTS" + TABLE_RECURRING_REMINDER)
        db!!.execSQL("DROP TABLE IF EXISTS" + TABLE_HEALTH_NOTE)
        onCreate(db)
    }

    //Create Table functions
    private fun CreatePetTable(db: SQLiteDatabase?){
        val CREATE_PETS_TABLE =("CREATE TABLE " + TABLE_PETS + "("
                + KEY_PET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + KEY_PET_IMAGE + " BLOB, "
                + KEY_PET_NAME + " TEXT, "
                + KEY_PET_AGE + " INTEGER, "
                + KEY_PET_TYPE + " TEXT, "
                + KEY_PET_BREED + " TEXT, "
                + KEY_PET_COAT + " TEXT, "
                + KEY_PET_COLOR + " TEXT, "
                + KEY_PET_GENDER + " TEXT)")
        db?.execSQL(CREATE_PETS_TABLE)
    }
    private fun CreatePetTypeTable(db: SQLiteDatabase?){
        val CREATE_PETTYPE_TABLE =("CREATE TABLE " + TABLE_PETTYPE + "("
                + KEY_PETTYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + KEY_PETTYPE_TYPENAME + " TEXT, "
                + KEY_PETTYPE_COATS + " TEXT, "
                + KEY_PETTYPE_COLORS + " TEXT, "
                + KEY_PETTYPE_GENDERS + " TEXT)")
        db?.execSQL(CREATE_PETTYPE_TABLE)
    }
    private fun CreatePetBreedTable(db: SQLiteDatabase?){
        val CREATE_PETBREED_TABLE =("CREATE TABLE " + TABLE_PETBREED + "("
                + KEY_PETBREED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + KEY_PETBREED_TYPENAME + " TEXT, "
                + KEY_PETBREED_BREEDS + " TEXT);")
        db?.execSQL(CREATE_PETBREED_TABLE)
    }
    private fun CreatePetRecurringReminderTable(db: SQLiteDatabase?){
        val CREATE_PET_RECURRING_REMINDER_TABLE =("CREATE TABLE " + TABLE_RECURRING_REMINDER + "("
                + KEY_RECURRING_REMINDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + KEY_RECURRING_REMINDER_TITLE + " TEXT, "
                + KEY_RECURRING_REMINDER_WEEKDAY + " TEXT, "
                + KEY_RECURRING_REMINDER_ACTIVE + " BOOLEAN, "
                + KEY_RECURRING_REMINDER_TIME + " TEXT, "
                + KEY_RECURRING_REMINDER_PET_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(${KEY_RECURRING_REMINDER_PET_ID}) REFERENCES ${TABLE_PETS}(${KEY_PET_ID}));")
        db?.execSQL(CREATE_PET_RECURRING_REMINDER_TABLE)
    }

    private fun CreatePetHealthNoteTable(db: SQLiteDatabase?) {
        val CREATE_PET_HEALTH_NOTE_TABLE=("CREATE TABLE " + TABLE_HEALTH_NOTE + "("
                + KEY_HEALTH_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + KEY_HEALTH_NOTE_DATE + " TEXT, "
                + KEY_HEALTH_NOTE_TITLE + " TEXT, "
                + KEY_HEALTH_NOTE_NOTE_TEXT + " TEXT, "
                + KEY_HEALTH_NOTE_PET_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(${KEY_HEALTH_NOTE_PET_ID}) REFERENCES ${TABLE_PETS}(${KEY_PET_ID}));")
        db?.execSQL(CREATE_PET_HEALTH_NOTE_TABLE)
    }

    //insert
    fun addPet(pet: Pet):Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PET_NAME, pet.Name)
        contentValues.put(KEY_PET_IMAGE,pet.Image)
        contentValues.put(KEY_PET_AGE, pet.Age)
        contentValues.put(KEY_PET_TYPE, pet.petType)
        contentValues.put(KEY_PET_BREED, pet.Breed)
        contentValues.put(KEY_PET_COAT, pet.Coat)
        contentValues.put(KEY_PET_COLOR, pet.Color)
        contentValues.put(KEY_PET_GENDER, pet.Gender)

        // insert row
        val success = db.insert(TABLE_PETS, null, contentValues)
        db.close()

        return success
    }
    fun addPetType(petType: PetType):Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PETTYPE_TYPENAME, petType.TypeName)
        contentValues.put(KEY_PETTYPE_COATS, ConvertListToString(petType.coats,","))
        contentValues.put(KEY_PETTYPE_COLORS, ConvertListToString(petType.colors,","))
        contentValues.put(KEY_PETTYPE_GENDERS, ConvertListToString(petType.genders,","))

        // insert row
        val success = db.insert(TABLE_PETTYPE, null, contentValues)
        db.close()

        return success
    }
    fun addPetBreed(petBreed: PetBreed):Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PETBREED_TYPENAME, petBreed.TypeName)
        contentValues.put(KEY_PETBREED_BREEDS, ConvertListToString(petBreed.Breeds,","))

        // insert row
        val success = db.insert(TABLE_PETBREED, null, contentValues)
        db.close()

        return success
    }
    fun addPetRecurringReminder(recurringReminder: RecurringReminder):Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_RECURRING_REMINDER_TITLE, recurringReminder.Title)
        contentValues.put(KEY_RECURRING_REMINDER_WEEKDAY, recurringReminder.Weekday)
        contentValues.put(KEY_RECURRING_REMINDER_TIME, recurringReminder.Time.toString())
        contentValues.put(KEY_RECURRING_REMINDER_ACTIVE, recurringReminder.Active)
        contentValues.put(KEY_RECURRING_REMINDER_PET_ID, recurringReminder.pet_id)

        // insert row
        val success = db.insert(TABLE_RECURRING_REMINDER, null, contentValues)
        db.close()

        return success
    }
    fun addPetHealthNote(healthNote: HealthNote):Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_HEALTH_NOTE_DATE, healthNote.Date.toString())
        contentValues.put(KEY_HEALTH_NOTE_TITLE, healthNote.Title)
        contentValues.put(KEY_HEALTH_NOTE_NOTE_TEXT, healthNote.NoteText)
        contentValues.put(KEY_HEALTH_NOTE_PET_ID, healthNote.pet_id)

        val success = db.insert(TABLE_HEALTH_NOTE, null, contentValues)
        db.close()

        return success
    }

    //read
    fun viewPets():List<Pet> {
        val petList = ArrayList<Pet>()
        val selectQuery = "SELECT * FROM $TABLE_PETS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var petId: Int
        var petImage: ByteArray
        var petName: String
        var petAge: Int
        var petType: String
        var petBreed: String
        var petCoat: String
        var petColor: String
        var petGender: String

        if (cursor.moveToFirst()) {
            do {
                petId = cursor.getInt(cursor.getColumnIndex(KEY_PET_ID))
                petImage = cursor.getBlob(cursor.getColumnIndex(KEY_PET_IMAGE))
                petName = cursor.getString(cursor.getColumnIndex(KEY_PET_NAME))
                petAge = cursor.getInt(cursor.getColumnIndex(KEY_PET_AGE))
                petType = cursor.getString(cursor.getColumnIndex(KEY_PET_TYPE))
                petBreed = cursor.getString(cursor.getColumnIndex(KEY_PET_BREED))
                petCoat = cursor.getString(cursor.getColumnIndex(KEY_PET_COAT))
                petColor = cursor.getString(cursor.getColumnIndex(KEY_PET_COLOR))
                petGender = cursor.getString(cursor.getColumnIndex(KEY_PET_GENDER))
                var pet = Pet(petId,petImage,petName,petAge,petType,petBreed,petCoat,petColor,petGender)
                petList.add(pet)

            } while (cursor.moveToNext())
        }
        return petList
    }
    fun viewPetTypes():List<PetType> {
        val petTypeList = ArrayList<PetType>()
        val selectQuery = "SELECT * FROM $TABLE_PETTYPE"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var petTypeId: Int
        var petTypeName: String
        var petTypeCoats: String
        var petTypeColors: String
        var petTypeGenders: String

        if (cursor.moveToFirst()) {
            do {
                //getting values of each record from database
                petTypeId = cursor.getInt(cursor.getColumnIndex(KEY_PETTYPE_ID))
                petTypeName = cursor.getString(cursor.getColumnIndex(KEY_PETTYPE_TYPENAME))
                petTypeCoats = cursor.getString(cursor.getColumnIndex(KEY_PETTYPE_COATS))
                petTypeColors = cursor.getString(cursor.getColumnIndex(KEY_PETTYPE_COLORS))
                petTypeGenders = cursor.getString(cursor.getColumnIndex(KEY_PETTYPE_GENDERS))

                //converting Strings to lists
                var petTypeCoatsList = ConvertStringToList(petTypeCoats,",")
                var petTypeColorsList = ConvertStringToList(petTypeColors,",")
                var petTypeGendersList = ConvertStringToList(petTypeGenders,",")

                //creating a petType object to add to petType list
                var petType = PetType(petTypeId,petTypeName,petTypeCoatsList,petTypeColorsList,petTypeGendersList)
                petTypeList.add(petType)

            } while (cursor.moveToNext())
        }
        return petTypeList
    }
    fun viewPetBreeds():List<PetBreed> {
        val petBreedList = ArrayList<PetBreed>()
        val selectQuery = "SELECT * FROM $TABLE_PETBREED"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var petBreedId: Int
        var petBreedTypeName: String
        var petBreeds: String

        if (cursor.moveToFirst()) {
            do {
                //getting values of each record from database
                petBreedId = cursor.getInt(cursor.getColumnIndex(KEY_PETBREED_ID))
                petBreedTypeName = cursor.getString(cursor.getColumnIndex(KEY_PETBREED_TYPENAME))
                petBreeds = cursor.getString(cursor.getColumnIndex(KEY_PETBREED_BREEDS))

                //converting Strings to lists
                var petBreedBreedsList = ConvertStringToList(petBreeds,",")

                //creating a petType object to add to petType list
                var petBreed = PetBreed(petBreedId,petBreedTypeName,petBreedBreedsList)
                petBreedList.add(petBreed)

            } while (cursor.moveToNext())
        }
        return petBreedList
    }
    fun viewPetRecurringReminders(pet: Pet): List<RecurringReminder>{
        val PetRecurringReminderList = ArrayList<RecurringReminder>()
        val selectQuery = "SELECT * FROM $TABLE_RECURRING_REMINDER WHERE $KEY_RECURRING_REMINDER_PET_ID = ${pet.id}"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var PRR_Id: Int
        var PRR_Title: String
        var PRR_Weekday: String
        var PRR_Active: Boolean
        var PRR_Time: LocalTime
        var PRR_Pet_Id: Int

        if (cursor.moveToFirst()) {
            do {
                PRR_Id = cursor.getInt(cursor.getColumnIndex(KEY_RECURRING_REMINDER_ID))
                PRR_Title = cursor.getString(cursor.getColumnIndex(KEY_RECURRING_REMINDER_TITLE))
                PRR_Weekday = cursor.getString(cursor.getColumnIndex(KEY_RECURRING_REMINDER_WEEKDAY))
                PRR_Active = cursor.getInt(cursor.getColumnIndex(KEY_RECURRING_REMINDER_ACTIVE)) > 0
                PRR_Time = convertStringToLocalTime(cursor.getString(cursor.getColumnIndex(KEY_RECURRING_REMINDER_TIME)))
                PRR_Pet_Id = cursor.getInt(cursor.getColumnIndex(KEY_RECURRING_REMINDER_PET_ID))
                var petRR = RecurringReminder(PRR_Id,PRR_Title,PRR_Weekday,PRR_Active,PRR_Time,PRR_Pet_Id)
                PetRecurringReminderList.add(petRR)

            } while (cursor.moveToNext())
        }
        return PetRecurringReminderList
    }
    fun viewPetHealthNotes(pet: Pet): List<HealthNote>{
        val PetHealthNoteList = ArrayList<HealthNote>()
        val selectQuery = "SELECT * FROM $TABLE_HEALTH_NOTE WHERE $KEY_HEALTH_NOTE_PET_ID = ${pet.id}"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var PHN_Id: Int
        var PHN_Date: LocalDate
        var PHN_Title: String
        var PHN_NoteText: String
        var PHN_Pet_Id: Int

        if (cursor.moveToFirst()) {
            do {
                PHN_Id = cursor.getInt(cursor.getColumnIndex(KEY_HEALTH_NOTE_ID))
                PHN_Date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(KEY_HEALTH_NOTE_DATE)))
                PHN_Title = cursor.getString(cursor.getColumnIndex(KEY_HEALTH_NOTE_TITLE))
                PHN_NoteText = cursor.getString(cursor.getColumnIndex(KEY_HEALTH_NOTE_NOTE_TEXT))
                PHN_Pet_Id = cursor.getInt(cursor.getColumnIndex(KEY_HEALTH_NOTE_PET_ID))
                var petHN = HealthNote(PHN_Id,PHN_Date,PHN_Title,PHN_NoteText,PHN_Pet_Id)
                PetHealthNoteList.add(petHN)

            } while (cursor.moveToNext())
        }
        return PetHealthNoteList
    }

    //update
    fun updatePet(pet: Pet):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PET_ID, pet.id)
        contentValues.put(KEY_PET_IMAGE, pet.Image)
        contentValues.put(KEY_PET_NAME, pet.Name)
        contentValues.put(KEY_PET_AGE, pet.Age)
        contentValues.put(KEY_PET_TYPE, pet.petType)
        contentValues.put(KEY_PET_BREED, pet.Breed)
        contentValues.put(KEY_PET_COAT, pet.Coat)
        contentValues.put(KEY_PET_COLOR, pet.Color)
        contentValues.put(KEY_PET_GENDER, pet.Gender)

        // update row
        val success = db.update(TABLE_PETS, contentValues, "id="+pet.id, null)
        db.close()
        return success
    }
    fun updatePetType(petType: PetType):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PETTYPE_TYPENAME, petType.TypeName)
        contentValues.put(KEY_PETTYPE_COATS, ConvertListToString(petType.coats,","))
        contentValues.put(KEY_PETTYPE_COLORS, ConvertListToString(petType.colors,","))
        contentValues.put(KEY_PETTYPE_GENDERS, ConvertListToString(petType.genders,","))

        // update row
        val success = db.update(TABLE_PETTYPE, contentValues, "id="+petType.id, null)
        db.close()
        return success
    }
    fun updatePetBreed(petBreed: PetBreed):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PETBREED_TYPENAME, petBreed.TypeName)
        contentValues.put(KEY_PETBREED_BREEDS, ConvertListToString(petBreed.Breeds,","))

        // update row
        val success = db.update(TABLE_PETBREED, contentValues, "id="+petBreed.id, null)
        db.close()
        return success
    }
    fun updatePetRecurringReminder(recurringReminder: RecurringReminder):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_RECURRING_REMINDER_TITLE, recurringReminder.Title)
        contentValues.put(KEY_RECURRING_REMINDER_WEEKDAY, recurringReminder.Weekday)
        contentValues.put(KEY_RECURRING_REMINDER_TIME, recurringReminder.Time.toString())
        contentValues.put(KEY_RECURRING_REMINDER_ACTIVE, recurringReminder.Active)
        contentValues.put(KEY_RECURRING_REMINDER_PET_ID, recurringReminder.pet_id)

        // update row
        val success = db.update(TABLE_RECURRING_REMINDER, contentValues, "id="+recurringReminder.id, null)
        db.close()
        return success
    }
    fun updatePetHealthNote(healthNote: HealthNote):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_HEALTH_NOTE_DATE, healthNote.Date.toString())
        contentValues.put(KEY_HEALTH_NOTE_TITLE, healthNote.Title)
        contentValues.put(KEY_HEALTH_NOTE_NOTE_TEXT, healthNote.NoteText)
        contentValues.put(KEY_HEALTH_NOTE_PET_ID, healthNote.pet_id)

        // update row
        val success = db.update(TABLE_HEALTH_NOTE, contentValues, "id="+healthNote.id, null)
        db.close()
        return success
    }

    //delete
    fun deletePet(pet: Pet):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PET_ID, pet.id)

        // delete row
        val success = db.delete(TABLE_PETS, "id= "+pet.id, null)
        db.close()
        return success
    }
    fun deletePetType(petType: PetType):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PETTYPE_ID, petType.id)

        // delete row
        val success = db.delete(TABLE_PETTYPE, "id= "+petType.id, null)
        db.close()
        return success
    }
    fun deletePetBreed(petBreed: PetBreed):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PETBREED_ID, petBreed.id)

        // delete row
        val success = db.delete(TABLE_PETBREED, "id= "+petBreed.id, null)
        db.close()
        return success
    }
    fun deletePetRecurringReminder(recurringReminder: RecurringReminder):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_RECURRING_REMINDER_ID, recurringReminder.id)

        // delete row
        val success = db.delete(TABLE_RECURRING_REMINDER, "id= "+recurringReminder.id, null)
        db.close()
        return success
    }
    fun deletePetHealthNote(healthNote: HealthNote):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_HEALTH_NOTE_ID, healthNote.id)

        // delete row
        val success = db.delete(TABLE_HEALTH_NOTE, "id= "+healthNote.id, null)
        db.close()
        return success
    }

    //deleteAll
    fun deleteAllFromPetType(): Int{
        val db = this.writableDatabase

        val success = db.delete(TABLE_PETTYPE,null,null)
        db.close()
        return success
    }
    fun deleteAllFromPetBreeds():Int{
        val db = this.writableDatabase

        val success = db.delete(TABLE_PETBREED,null,null)
        db.close()
        return success
    }

    //utilities
    //Converting List to string with defined delimeter
    private fun ConvertListToString(objects: List<String>,delimeter: String):String {
        var delimterSeperatedString = ""
        try {
            if (!(objects.isEmpty())) {
                delimterSeperatedString = StringUtils.join(objects, delimeter)
            }
        }catch (ex: Exception){
            Log.e("CONVERT_LIST_TO_STRING", ex.toString())
        }

        return delimterSeperatedString;
    }
    //Converting String to List using defined delimeter
    private fun ConvertStringToList(objectString: String,delimeter: String):List<String>{
        var objects = ArrayList<String>()

        try {
            if(!(objectString.isEmpty())){
                objects.addAll(StringUtils.split(objectString,delimeter))
            }
        }catch (ex: Exception){
            Log.e("CONVERT_STRING_TO_LIST", ex.toString())
        }

        return objects;
    }
    //Converting Reminder Time string into valid local time
    private fun convertStringToLocalTime(objectString: String): LocalTime{
        return LocalTime.parse(objectString)
    }
}