package com.example.pet_health_pal.Activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_health_pal.DatabaseHandler.PetDatabaseHandler
import com.example.pet_health_pal.Entities.Pet
import com.example.pet_health_pal.Entities.RecurringReminder
import com.example.pet_health_pal.R
import com.google.android.material.switchmaterial.SwitchMaterial

class RecurringRemindersListAdapter(private val pet: Pet, private val reminders: ArrayList<RecurringReminder>,val recyclerView: RecyclerView, private val listener:OnItemClickListener): RecyclerView.Adapter<RecurringRemindersListAdapter.MyViewHolder>() {

    private lateinit var db: PetDatabaseHandler
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecurringRemindersListAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recurring_reminder_row,parent,false)
        db = PetDatabaseHandler(parent.context)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecurringRemindersListAdapter.MyViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.Title.text = reminder.Title
        holder.Weekday.text = reminder.Weekday
        holder.Time.text = reminder.Time.toString()
        holder.Active.isChecked = reminder.Active
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var Title: TextView = itemView.findViewById(R.id.txtReminderHeading)
        var Weekday: TextView = itemView.findViewById(R.id.textViewWeekday)
        var Time: TextView = itemView.findViewById(R.id.textViewReminderTime)
        var Active: SwitchMaterial = itemView.findViewById(R.id.switchActive)

        init {
            itemView.setOnClickListener(this)

            Active.setOnCheckedChangeListener { compoundButton, b ->
                // save the reminder state in the database
                var petRR = reminders.get(adapterPosition)
                petRR.Active = b
                if(db.updatePetRecurringReminder(petRR) == 0){
                    if(b){
                        Toast.makeText(itemView.context,"Could not turn on the reminder",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(itemView.context,"Could not turn off the reminder",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    if(b){
                        Toast.makeText(itemView.context,"${petRR.Title} Reminder was turned ON",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(itemView.context,"${petRR.Title} Reminder was turned OFF",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}