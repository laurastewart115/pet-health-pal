package com.example.pet_health_pal.Activities

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_health_pal.Activities.ui.ViewModel.PetViewModel
import com.example.pet_health_pal.Activities.ui.reminders.RemindersViewModel
import com.example.pet_health_pal.DatabaseHandler.AsyncResponse
import com.example.pet_health_pal.DatabaseHandler.PetDatabaseHandler
import com.example.pet_health_pal.DatabaseHandler.PetFinderApiHandler
import com.example.pet_health_pal.Entities.Pet
import com.example.pet_health_pal.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),ListPetAdapter.OnItemClickListener {

    var pets = ArrayList<Pet>()
    lateinit var petAdapter: ListPetAdapter
    private lateinit var petViewModel: PetViewModel
    var db = PetDatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnAddPet.setOnClickListener {
            Toast.makeText(this, "Add Pet", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AddPetActivity::class.java)
            startActivity(intent)
        }

        pets.addAll(db.viewPets())

        var recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        petAdapter = ListPetAdapter(pets, this)
        recyclerview.adapter = petAdapter
        recyclerview.layoutManager = LinearLayoutManager(this)

        try {
            PetFinderApiHandler(this, asyncResponse = object : AsyncResponse {
                override fun onResponseRecieved(s: String, b: Boolean?) {
                    System.out.println(s + " : " + b)
                }

                override fun onErrorListener(s: String) {
                    System.out.println(s)
                }
            }).execute()
        }catch (ex: Exception){
            System.out.println(ex)
        }

        var itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pet = pets.get(viewHolder.adapterPosition)
                db.deletePet(pet)
                pets.clear()
                pets.addAll(db.viewPets())
                petAdapter.notifyDataSetChanged()
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(applicationContext,R.color.Red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_50)
                    .create()
                    .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerview)
    }

    override fun onResume() {
        super.onResume()
        pets.clear()
        pets.addAll(db.viewPets())
        petAdapter.notifyDataSetChanged()
    }

    //triggered when a list item is clicked
    override fun onItemClick(position: Int) {
        Toast.makeText(this, pets[position].Name, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, PetDetailsActivity::class.java)
        intent.putExtra("PET",pets[position])
        startActivity(intent)
    }
}
