package com.example.pet_health_pal.Activities

import android.provider.ContactsContract
import android.text.style.LineHeightSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_health_pal.DatabaseHandler.PetDatabaseHandler
import com.example.pet_health_pal.Entities.HealthNote
import com.example.pet_health_pal.Entities.Pet
import com.example.pet_health_pal.R

class HealthNoteListAdapter(private val pet: Pet,
                            private val notes: ArrayList<HealthNote>,
                            val recyclerView: RecyclerView,
                            private val listener: HealthNoteListAdapter.OnItemClickListener):
        RecyclerView.Adapter<HealthNoteListAdapter.MyViewHolder>() {

    private lateinit var db: PetDatabaseHandler

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthNoteListAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.health_note_row,parent,false)
        db = PetDatabaseHandler(parent.context)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HealthNoteListAdapter.MyViewHolder, position: Int) {
        val note = notes[position]
        holder.Title.text = note.Title
        holder.NoteDate.text = note.Date.toString()
        holder.NoteBody.text = note.NoteText
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var Title: TextView = itemView.findViewById(R.id.txtNoteHeading)
        var NoteDate: TextView = itemView.findViewById(R.id.textViewNoteDate)
        var NoteBody: TextView = itemView.findViewById(R.id.textViewNoteBody)

        init {
            itemView.setOnClickListener(this)
            NoteBody.visibility = View.GONE
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {

                if (NoteBody.visibility == View.GONE) {
                    NoteBody.visibility = View.VISIBLE
                }
                else if (NoteBody.visibility == View.VISIBLE) {
                    NoteBody.visibility = View.GONE
                }

                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}