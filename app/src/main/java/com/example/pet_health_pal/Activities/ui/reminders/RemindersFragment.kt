package com.example.pet_health_pal.Activities.ui.reminders

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_health_pal.Activities.RecurringRemindersListAdapter
import com.example.pet_health_pal.Entities.RecurringReminder
import com.example.pet_health_pal.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.threeten.bp.LocalTime
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.pet_health_pal.Activities.ui.ViewModel.PetViewModel
import com.example.pet_health_pal.DatabaseHandler.PetDatabaseHandler
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_reminders.*

class RemindersFragment : Fragment(),RecurringRemindersListAdapter.OnItemClickListener  {

    private lateinit var petViewModel: PetViewModel
    private lateinit var db: PetDatabaseHandler
    private var reminders = ArrayList<RecurringReminder>()
    private lateinit var remindersAdapter: RecurringRemindersListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_reminders, container, false)

        //instantiating the Shared petViewModel to access pet data
        petViewModel = ViewModelProvider(requireActivity()).get(PetViewModel::class.java)

        //instantiating db handler
        db = PetDatabaseHandler(requireContext())

        reminders.addAll(db.viewPetRecurringReminders(petViewModel.getpetData().value!!))

        var recyclerview = root.findViewById<RecyclerView>(R.id.recyclerviewReminders)
        remindersAdapter = RecurringRemindersListAdapter(petViewModel.getpetData().value!!,reminders,recyclerview, this)
        recyclerview.adapter = remindersAdapter
        recyclerview.layoutManager = LinearLayoutManager(root.context)


        // Create a popup window to create a new reminder
        val btnCreateReminder = root.findViewById<FloatingActionButton>(R.id.btnCreateReminder)

        btnCreateReminder.setOnClickListener {
            // source for creating a popup window: https://android--code.blogspot.com/2018/02/android-kotlin-popup-window-example.html
            val inflater:LayoutInflater = root.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val popupView = inflater.inflate(R.layout.popup_add_reminder, null)
            val popupForm = PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
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
            val editTextRemName = popupView.findViewById<EditText>(R.id.editTextReminderTitle)
            val spinnerRemDay = popupView.findViewById<Spinner>(R.id.spinnerWeekday)
            val reminderTime = popupView.findViewById<TimePicker>(R.id.timePickerReminder)
            val btnConfirm = popupView.findViewById<Button>(R.id.btnConfirmReminder)
            val btnDismiss = popupView.findViewById<Button>(R.id.btnDismiss)

            // Set up the spinner
            ArrayAdapter.createFromResource(
                    root.context,
                    R.array.weekdays,
                    android.R.layout.simple_spinner_item
            ).also { arrayAdapter ->
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                spinnerRemDay.adapter = arrayAdapter
            }

            // Set button on click listeners
            btnConfirm.setOnClickListener {
                if (editTextRemName.text.toString().isNullOrEmpty()) {
                    Toast.makeText(root.context, "Please enter all data", Toast.LENGTH_SHORT).show()
                }
                else {
                    val title = editTextRemName.text.toString()
                    val day = spinnerRemDay.selectedItem.toString()
                    val time = LocalTime.of(reminderTime.hour, reminderTime.minute)

                    // store a reminder in the database
                    var petRR = RecurringReminder(1,title,day,true,time,petViewModel.getpetData().value!!.id)
                    if(db.addPetRecurringReminder(petRR) != (-1.0).toLong()){
                        reminders.clear()
                        reminders.addAll(db.viewPetRecurringReminders(petViewModel.getpetData().value!!))
                        remindersAdapter.notifyDataSetChanged()
                    }
                    else{
                        Toast.makeText(context,"Was not able to insert Reminder, please try again!",Toast.LENGTH_SHORT).show()
                    }

                    popupForm.dismiss()
                }
            }

            btnDismiss.setOnClickListener {
                popupForm.dismiss()
            }

            // Show the popup
            TransitionManager.beginDelayedTransition(rootReminderLayout)
            popupForm.showAtLocation(
                    rootReminderLayout, // Location to display popup window
                    Gravity.CENTER, // Exact position of layout to display popup
                    0, // X offset
                    0 // Y offset
            )
        }

        setupBackButton()

        var itemTouchHelper = ItemTouchHelper(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(recyclerview)

        return root
    }

    val itemTouchHelper = object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            db.deletePetRecurringReminder(reminders[viewHolder.adapterPosition])
            remindersAdapter.notifyDataSetChanged()
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
        // TODO: do something
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