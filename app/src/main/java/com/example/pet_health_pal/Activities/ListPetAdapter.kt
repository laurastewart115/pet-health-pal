package com.example.pet_health_pal.Activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_health_pal.Entities.Pet
import com.example.pet_health_pal.R

class ListPetAdapter(private val pets: List<Pet>,private val listener:OnItemClickListener): RecyclerView.Adapter<ListPetAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPetAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.custom_pet_row,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListPetAdapter.MyViewHolder, position: Int) {
        val pet = pets[position]
        holder.Age.text = pet.Age.toString()
        holder.Breed.text = pet.Breed
        holder.Name.text = pet.Name
        holder.Type.text = pet.petType
        holder.petImage.setImageBitmap(pet.Image?.let { byteArrayToBitmap(it) })
    }

    override fun getItemCount(): Int {
        return pets.size
    }

    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var petImage: ImageView = itemView.findViewById(R.id.petImage)
        var Name: TextView = itemView.findViewById(R.id.Name)
        var Age : TextView = itemView.findViewById(R.id.Age)
        var Breed: TextView = itemView.findViewById(R.id.Breed)
        var Type : TextView = itemView.findViewById(R.id.Type)

        init {
            itemView.setOnClickListener(this)
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

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap{
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }
}