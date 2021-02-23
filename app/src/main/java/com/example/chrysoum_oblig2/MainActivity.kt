package com.example.chrysoum_oblig2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.InputStream
import java.lang.Exception

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var response: Valg
    lateinit var results1: List<Vote>
    lateinit var results2: List<Vote>

    var adapter: PartyAdapter? = null

    var resultsD1 = ArrayList<Vote>()
    var resultsD2 = ArrayList<Vote>()
    var resultsD3 = ArrayList<Party>()

    var votes1 = 0
    var votes2 = 0
    var votes3 = 0
    var votes4 = 0

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler_view = findViewById<RecyclerView>(R.id.recyclerView)
        recycler_view.layoutManager= LinearLayoutManager(this,RecyclerView.VERTICAL, false)


        val el: MutableList<AlpacaParty> = mutableListOf<AlpacaParty>()
        Log.d("Main activity", el.toString())

        adapter = PartyAdapter(el)
        recycler_view.adapter = adapter

        val path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/alpacaparties.json"
        val d1Path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district1.json"
        val d2Path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district2.json"
        val d3Path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district3.xml"

        val gson = Gson()


        suspend fun getData() {
            try {
                response = gson.fromJson(Fuel.get(path).awaitString(), Valg::class.java)
            }
            catch(exception: Exception){
                exception.message?.let{Log.e("Main Activity", it)}
            }
        }

        suspend fun getDistrict1() {
            try {
                results1 =
                    gson.fromJson(Fuel.get(d1Path).awaitString(), Array<Vote>::class.java).toList()
            } catch (exception: Exception) {
                exception.message?.let { Log.e("Main Activity", it) }
            }
        }

        suspend fun getDistrict2() {
         try {
             results2 = gson.fromJson(Fuel.get(d2Path).awaitString(), Array<Vote>::class.java).toList()
         } catch (exception: Exception) {
             exception.message?.let { Log.e("Main Activity", it) }
         }
       }

        suspend fun getDistrict3(): String{
            return Fuel.get(d3Path).awaitString()
        }

        val spinner: Spinner = findViewById<Spinner>(R.id.Districts)


        CoroutineScope(Dispatchers.IO).launch{
            getData()

            getDistrict1()

            getDistrict2()


            val responseD3 = getDistrict3()


            withContext(Dispatchers.Main){

                val elements = response.elements()
                for (e in elements){
                    el.add(e)
                }

                for (r in results1){
                    resultsD1.add(r)
                }

                for (r in results2){
                    resultsD2.add(r)
                }

                val inputStream: InputStream = responseD3.byteInputStream()
                @Suppress("UNCHECKED_CAST")
                resultsD3 = XmlParser().parse(inputStream) as ArrayList<Party>

                ArrayAdapter.createFromResource(this@MainActivity,
                    R.array.districts, android.R.layout.simple_spinner_item).also{
                        adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }

                spinner.onItemSelectedListener = this@MainActivity

                adapter = PartyAdapter(el)
                recycler_view.adapter = adapter
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position)
        clearVotes()
        when(item){
            "Select a district:" -> {
                for (e in response.elements()){
                    e.updateAverage(0.0)
                }
            }
            "district 1" -> {
                setVotes(resultsD1)

                var total = 0
                for (e in response.elements()){
                    total += e.getVotes()
                }

                for (e in response.elements()){
                    e.updateAverage(e.getVotes().toDouble()/total*100)
                }
            }
            "district 2" -> {
                setVotes(resultsD2)

                var total = 0
                for (e in response.elements()){
                    total += e.getVotes()
                }

                for (e in response.elements()){
                    e.updateAverage(e.getVotes().toDouble()/total*100)
                }
            }
            "district 3" -> {
                for (i in 0..3){
                    resultsD3[i].votes?.let { response.elements()[i].setVotes(it) }
                }

                var total = 0
                for (p in resultsD3){
                    if (p.votes != null){
                        total += p.votes
                    }
                }

                for (e in response.elements()){
                    e.updateAverage(e.getVotes().toDouble()/total*100)
                }
            }

        }
        adapter?.notifyDataSetChanged()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        val toast = Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
        toast.show()
    }

    private fun setVotes(district: ArrayList<Vote>) {
        for (d in district){
            response.elements()[d.id.toInt()-1].addVote()
        }
    }

   private fun clearVotes(){
        for (e in response.elements()){
            e.setVotes(0)
        }
    }
}

data class Valg(val parties: MutableList<AlpacaParty>){

    fun elements(): MutableList<AlpacaParty>{
        return parties
    }
 }

data class Vote(val id: String, val votes: Int)
