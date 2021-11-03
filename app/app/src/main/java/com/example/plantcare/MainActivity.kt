package com.example.plantcare

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var progressBar: ProgressBar? = null
        var progressBar2: ProgressBar? = null
        var textohumedad= findViewById<TextView>(R.id.textView3)
        var textoagua= findViewById<TextView>(R.id.textView4)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar2 = findViewById<ProgressBar>(R.id.progressBar2)

        //Para entrar a Firebase
        val Database= Firebase.database
        val myref= Database.getReference("sensores")


        //Intentar si existe un cambio
        val userListener= object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue<User>()
                //Evaluar los datos desde BD
                var currentProgressHumedity = user?.humedad
                var currentProgressWater= user?.agua
                //Mandar a Interfaz
                if (currentProgressHumedity != null) {
                    progressBar.setProgress(currentProgressHumedity)
                    var texto=currentProgressHumedity.toString()+"%"
                    textohumedad.setText(texto)
                }
                if (currentProgressWater != null) {
                    progressBar2.setProgress(currentProgressWater)
                    var texto2= currentProgressWater.toString()+"%"
                    textoagua.setText(texto2)

                }
                progressBar.setMax(100)
                progressBar2.setMax(100)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "userData:onCancelled", databaseError.toException())
            }
        }
        myref.addValueEventListener(userListener)


    }
}
