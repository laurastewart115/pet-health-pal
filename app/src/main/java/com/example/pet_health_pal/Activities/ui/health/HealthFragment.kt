package com.example.pet_health_pal.Activities.ui.health

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_health_pal.Activities.HealthNoteListAdapter
import com.example.pet_health_pal.Activities.ui.ViewModel.PetViewModel
import com.example.pet_health_pal.DatabaseHandler.PetDatabaseHandler
import com.example.pet_health_pal.Entities.HealthNote
import com.example.pet_health_pal.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_health.*
import org.threeten.bp.LocalDate
import java.util.*

class HealthFragment : Fragment(), HealthNoteListAdapter.OnItemClickListener {

    private lateinit var healthViewModel: HealthViewModel

    private lateinit var petViewModel: PetViewModel
    private lateinit var db: PetDatabaseHandler
    private var healthNotes = ArrayList<HealthNote>()
    private lateinit var healthNoteAdapter: HealthNoteListAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        healthViewModel = ViewModelProvider(this).get(HealthViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_health, container, false)

        setupBackButton()

        //instantiating the Shared petViewModel to access pet data
        petViewModel = ViewModelProvider(requireActivity()).get(PetViewModel::class.java)

        //instantiating db handler
        db = PetDatabaseHandler(requireContext())

        healthNotes.addAll(db.viewPetHealthNotes(petViewModel.getpetData().value!!))

        // set up the list of notes
        var recyclerview = root.findViewById<RecyclerView>(R.id.recyclerviewNotes)
        val notesAdapter = HealthNoteListAdapter(petViewModel.getpetData().value!!,healthNotes,recyclerview, this)
        recyclerview.adapter = notesAdapter
        recyclerview.layoutManager = LinearLayoutManager(root.context)

        val btnCreateNote = root.findViewById<FloatingActionButton>(R.id.btnCreateNote)

        btnCreateNote.setOnClickListener {
            // source for creating a popup window: https://android--code.blogspot.com/2018/02/android-kotlin-popup-window-example.html
            val inflater:LayoutInflater = root.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val popupView = inflater.inflate(R.layout.popup_add_note, null)
            val popupForm = PopupWindow(popupView,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)

            popupForm.isFocusable = true
            popupForm.update()

            popupForm.elevation = 10.0F

            // Create a new slide animation for popup window enter transition
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupForm.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.RIGHT
            popupForm.exitTransition = slideOut

            // Get the widgets
            val txtTitle = popupView.findViewById<EditText>(R.id.editTextNoteTitle)
            val txtBody = popupView.findViewById<EditText>(R.id.editTextNoteBody)
            val btnSave = popupView.findViewById<Button>(R.id.btnConfirmNote)
            val btnDismiss = popupView.findViewById<Button>(R.id.btnDismissNote)

            var cal = Calendar.getInstance()

            // Set button on click listeners
            btnSave.setOnClickListener {
                if (txtTitle.text.toString().isNullOrEmpty() || txtBody.text.toString().isNullOrEmpty()) {
                    Toast.makeText(root.context, "Please enter all data", Toast.LENGTH_SHORT).show()
                }
                else {
                    val title = txtTitle.text.toString()
                    val body = txtBody.text.toString()
                    val date = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,cal.get(Calendar.DAY_OF_MONTH))

                    // store the note in the database
                    var petHN = HealthNote(1, date, title, body, petViewModel.getpetData().value!!.id)
                    if(db.addPetHealthNote(petHN) != (-1.0).toLong()){
                        healthNotes.clear()
                        healthNotes.addAll(db.viewPetHealthNotes(petViewModel.getpetData().value!!))
                        //healthNoteAdapter.notifyDataSetChanged()
                    }
                    else{
                        Toast.makeText(context,"Was not able to create a health note, please try again!",Toast.LENGTH_SHORT).show()
                    }


                    popupForm.dismiss()
                }
            }

            btnDismiss.setOnClickListener {
                popupForm.dismiss()
            }

            // Show the popup
            TransitionManager.beginDelayedTransition(rootHealthLayout)
            popupForm.showAtLocation(
                    rootHealthLayout, // Location to display popup window
                    Gravity.CENTER, // Exact position of layout to display popup
                    0, // X offset
                    0 // Y offset
            )

        }

        var itemTouchHelper = ItemTouchHelper(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(recyclerview)

        return root
    }

    val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            db.deletePetHealthNote(healthNotes[viewHolder.adapterPosition])
            healthNoteAdapter.notifyDataSetChanged()
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(),R.color.Red))
                .addSwipeLeftActionIcon(R.drawable.ic_delete_50)
                .create()
                .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }


    }

    //triggered when a list item is clicked
    override fun onItemClick(position: Int) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBackButton() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}