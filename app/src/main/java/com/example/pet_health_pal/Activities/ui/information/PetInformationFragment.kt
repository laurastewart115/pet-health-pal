package com.example.pet_health_pal.Activities.ui.information

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pet_health_pal.Activities.ui.ViewModel.PetViewModel
import com.example.pet_health_pal.R
import java.lang.Exception

class PetInformationFragment : Fragment() {

    private lateinit var petInformationViewModel: PetInformationViewModel

    private lateinit var petViewModel: PetViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        petInformationViewModel = ViewModelProvider(this).get(PetInformationViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_info, container, false)

        setupBackButton()

        //instantiating the Shared petViewModel to access pet data
        petViewModel = ViewModelProvider(requireActivity()).get(PetViewModel::class.java)

        val webView = root.findViewById<WebView>(R.id.webViewInfo)

        //search for the pet (opens chrome)
        val pet = petViewModel.getpetData()
        val search = pet.value?.petType + " " + pet.value?.Breed
        if (!search.isNullOrEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.putExtra(SearchManager.QUERY, search)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(root.context, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(root.context, "Could not search for pet information", Toast.LENGTH_SHORT).show()
        }

        // show the google search results

        return root
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