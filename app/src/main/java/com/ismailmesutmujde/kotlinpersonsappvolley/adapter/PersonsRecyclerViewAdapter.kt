package com.ismailmesutmujde.kotlinpersonsappvolley.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ismailmesutmujde.kotlinpersonsappvolley.R
import com.ismailmesutmujde.kotlinpersonsappvolley.model.Persons

class PersonsRecyclerViewAdapter(private val mContext : Context,
                                 private var personsList : List<Persons>)
    : RecyclerView.Adapter<PersonsRecyclerViewAdapter.CardDesignHolder>() {

    inner class CardDesignHolder(view : View) : RecyclerView.ViewHolder(view) {
        var textViewPersonInfo : TextView
        var imageViewDot : ImageView

        init {
            textViewPersonInfo = view.findViewById(R.id.textViewPersonInfo)
            imageViewDot = view.findViewById(R.id.imageViewDot)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignHolder {
        val design = LayoutInflater.from(mContext).inflate(R.layout.person_card_design, parent, false)
        return CardDesignHolder(design)
    }

    override fun getItemCount(): Int {
        return personsList.size
    }

    override fun onBindViewHolder(holder: CardDesignHolder, position: Int) {
        val person = personsList.get(position)

        holder.textViewPersonInfo.text = "${person.person_name} - ${person.person_phone}"
        holder.imageViewDot.setOnClickListener {

            val popupMenu = PopupMenu(mContext,holder.imageViewDot)
            popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem->
                when(menuItem.itemId) {
                    R.id.action_delete -> {
                        Snackbar.make(holder.imageViewDot,"Delete ${person.person_name}?", Snackbar.LENGTH_SHORT)
                            .setAction("YES") {

                            }.show()
                        true
                    }
                    R.id.action_update -> {
                        showAlert(person)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    fun showAlert(person:Persons) {
        val design = LayoutInflater.from(mContext).inflate(R.layout.alert_design, null)
        val editTextPersonName = design.findViewById(R.id.editTextPersonName) as EditText
        val editTextPersonPhone = design.findViewById(R.id.editTextPersonPhone) as EditText

        editTextPersonName.setText(person.person_name)
        editTextPersonPhone.setText(person.person_phone)

        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setTitle("Update Person")
        alertDialog.setView(design)
        alertDialog.setPositiveButton("Update") { dialogInterface, i ->
            val person_name = editTextPersonName.text.toString().trim()
            val person_phone = editTextPersonPhone.text.toString().trim()

            Toast.makeText(mContext, "${person_name} - ${person_phone}", Toast.LENGTH_SHORT).show()

        }
        alertDialog.setNegativeButton("Cancel") { dialogInterface, i ->


        }
        alertDialog.show()
    }
}