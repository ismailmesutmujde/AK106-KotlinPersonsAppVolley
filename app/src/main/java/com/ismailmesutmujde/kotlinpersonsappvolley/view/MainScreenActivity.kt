package com.ismailmesutmujde.kotlinpersonsappvolley.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ismailmesutmujde.kotlinpersonsappvolley.R
import com.ismailmesutmujde.kotlinpersonsappvolley.adapter.PersonsRecyclerViewAdapter
import com.ismailmesutmujde.kotlinpersonsappvolley.databinding.ActivityMainScreenBinding
import com.ismailmesutmujde.kotlinpersonsappvolley.model.Persons
import org.json.JSONObject

class MainScreenActivity : AppCompatActivity() , SearchView.OnQueryTextListener {
    private lateinit var bindingMainScreen : ActivityMainScreenBinding

    private lateinit var personsList : ArrayList<Persons>
    private lateinit var adapterPersons : PersonsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMainScreen = ActivityMainScreenBinding.inflate(layoutInflater)
        val view = bindingMainScreen.root
        setContentView(view)

        bindingMainScreen.toolbar.title = "Persons Application"
        setSupportActionBar(bindingMainScreen.toolbar)

        bindingMainScreen.recyclerView.setHasFixedSize(true)
        bindingMainScreen.recyclerView.layoutManager = LinearLayoutManager(this)

        /*
        personsList = ArrayList()
        val p1 = Persons(1,"Ahmet", "888888")
        val p2 = Persons(2,"Zeynep", "666666")
        val p3 = Persons(3,"Ece", "333333")

        personsList.add(p1)
        personsList.add(p2)
        personsList.add(p3)

        adapterPersons = PersonsRecyclerViewAdapter(this, personsList)
        bindingMainScreen.recyclerView.adapter = adapterPersons
        */

        bindingMainScreen.fab.setOnClickListener {
            showAlert()
        }

        allPersons()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    fun showAlert() {
        val design = LayoutInflater.from(this).inflate(R.layout.alert_design, null)
        val editTextPersonName = design.findViewById(R.id.editTextPersonName) as EditText
        val editTextPersonPhone = design.findViewById(R.id.editTextPersonPhone) as EditText

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Add Person")
        alertDialog.setView(design)
        alertDialog.setPositiveButton("Add") { dialogInterface, i ->
            val person_name = editTextPersonName.text.toString().trim()
            val person_phone = editTextPersonPhone.text.toString().trim()
            insertPerson(person_name, person_phone)
            Toast.makeText(applicationContext, "${person_name} - ${person_phone}", Toast.LENGTH_SHORT).show()

        }
        alertDialog.setNegativeButton("Cancel") { dialogInterface, i ->


        }
        alertDialog.show()
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        searchPerson(query)
        Log.e("Sent Search", query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        searchPerson(newText)
        Log.e("As Letters Enter", newText)
        return true
    }

    fun allPersons() {
        val url = "http://kasimadalan.pe.hu/kisiler/tum_kisiler.php"
        val request = StringRequest(Request.Method.GET, url, Response.Listener { response->
            try {
                personsList = ArrayList()
                val jsonObject = JSONObject(response)
                val persons = jsonObject.getJSONArray("kisiler")

                for (i in 0 until persons.length()) {
                    val p = persons.getJSONObject(i)
                    val person = Persons(p.getInt("kisi_id")
                        ,p.getString("kisi_ad")
                        ,p.getString("kisi_tel"))
                    personsList.add(person)
                }

                adapterPersons = PersonsRecyclerViewAdapter(this@MainScreenActivity, personsList)
                bindingMainScreen.recyclerView.adapter = adapterPersons

            } catch (e:Exception) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {  })
        Volley.newRequestQueue(this@MainScreenActivity).add(request)
    }

    fun searchPerson(searchingWord:String) {
        val url = "http://kasimadalan.pe.hu/kisiler/tum_kisiler_arama.php"
        val request = object : StringRequest(Request.Method.POST, url, Response.Listener { response->
            try {
                personsList = ArrayList()
                val jsonObject = JSONObject(response)
                val persons = jsonObject.getJSONArray("kisiler")

                for (i in 0 until persons.length()) {
                    val p = persons.getJSONObject(i)
                    val person = Persons(p.getInt("kisi_id")
                        ,p.getString("kisi_ad")
                        ,p.getString("kisi_tel"))
                    personsList.add(person)
                }

                adapterPersons = PersonsRecyclerViewAdapter(this@MainScreenActivity, personsList)
                bindingMainScreen.recyclerView.adapter = adapterPersons

            } catch (e:Exception) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {  }){
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String,String>()
                params["kisi_ad"] = searchingWord
                return params
            }
        }
        Volley.newRequestQueue(this@MainScreenActivity).add(request)
    }

    fun insertPerson(person_name:String, person_phone:String) {
        val url = "http://kasimadalan.pe.hu/kisiler/insert_kisiler.php"
        val request = object : StringRequest(Request.Method.POST, url, Response.Listener { response->

            allPersons()

        }, Response.ErrorListener {  }){
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String,String>()
                params["kisi_ad"] = person_name
                params["kisi_tel"] = person_phone
                return params
            }
        }
        Volley.newRequestQueue(this@MainScreenActivity).add(request)
    }


}