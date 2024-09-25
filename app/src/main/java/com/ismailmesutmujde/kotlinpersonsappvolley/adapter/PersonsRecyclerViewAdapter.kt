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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.ismailmesutmujde.kotlinpersonsappvolley.R
import com.ismailmesutmujde.kotlinpersonsappvolley.model.Persons
import org.json.JSONObject

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
                                deletePerson(person.person_id)
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
            updatePerson(person.person_id,person_name,person_phone)
            Toast.makeText(mContext, "${person_name} - ${person_phone}", Toast.LENGTH_SHORT).show()

        }
        alertDialog.setNegativeButton("Cancel") { dialogInterface, i ->


        }
        alertDialog.show()
    }

    fun updatePerson(person_id : Int, person_name:String, person_phone:String) {
        val url = "http://kasimadalan.pe.hu/kisiler/update_kisiler.php"
        val request = object : StringRequest(Request.Method.POST, url, Response.Listener { response->

            allPersons()

        }, Response.ErrorListener {  }){
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String,String>()
                params["kisi_id"] = person_id.toString()
                params["kisi_ad"] = person_name
                params["kisi_tel"] = person_phone
                return params
            }
        }
        Volley.newRequestQueue(mContext).add(request)
    }

    fun deletePerson(person_id : Int) {
        val url = "http://kasimadalan.pe.hu/kisiler/delete_kisiler.php"
        val request = object : StringRequest(Request.Method.POST, url, Response.Listener { response->

            allPersons()

        }, Response.ErrorListener {  }){
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String,String>()
                params["kisi_id"] = person_id.toString()
                return params
            }
        }
        Volley.newRequestQueue(mContext).add(request)
    }

    fun allPersons() {
        val url = "http://kasimadalan.pe.hu/kisiler/tum_kisiler.php"
        val request = StringRequest(Request.Method.GET, url, Response.Listener { response->
            try {
                val tempList = ArrayList<Persons>()
                val jsonObject = JSONObject(response)
                val persons = jsonObject.getJSONArray("kisiler")

                for (i in 0 until persons.length()) {
                    val p = persons.getJSONObject(i)
                    val person = Persons(p.getInt("kisi_id")
                        ,p.getString("kisi_ad")
                        ,p.getString("kisi_tel"))
                    tempList.add(person)
                }

                personsList = tempList
                notifyDataSetChanged()

            } catch (e:Exception) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {  })
        Volley.newRequestQueue(mContext).add(request)
    }
}