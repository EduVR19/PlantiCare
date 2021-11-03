package com.example.plantcare

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RemoteViews
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val channelName = "channelName"
    private val channelId = "channelId"

    private lateinit var notificationCustomStyle: Notification
    private val notificationCustomStyleID = 1
    lateinit var progressBar: ProgressBar
    lateinit var progressBar2: ProgressBar
    lateinit var textohumedad: TextView
    lateinit var textoagua: TextView
    var currentUser:User? = User()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar2 = findViewById<ProgressBar>(R.id.progressBar2)
        textohumedad= findViewById<TextView>(R.id.textView3)
        textoagua= findViewById<TextView>(R.id.textView4)
        createNotificationChannel()
        buildNotificationCustomStyle()
        buttonsListener()



        //Para entrar a Firebase
        val Database= Firebase.database
        val myref= Database.getReference("sensores")



        //Intentar si existe un cambio
        val userListener= object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue<User>()
                currentUser=user
                //Evaluar los datos desde BD
                var currentProgressHumedity = user?.humedad
                var currentProgressWater= user?.agua
                changebars(currentProgressHumedity,currentProgressWater)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "userData:onCancelled", databaseError.toException())
            }
        }
        myref.addValueEventListener(userListener)
findViewById<Button>(R.id.buttonhumelow).setOnClickListener {
    var humedity=(currentUser?.humedad?:0)-1
    if (humedity<0) humedity=0
    val water=currentUser?.agua
    changebars(humedity,water)
    currentUser?.let {
        it.agua=water
        it.humedad=humedity
    }
}

    }

    private fun changebars(currentProgressHumedity:Int?,currentProgressWater:Int?){
        if (currentProgressHumedity != null) {
            progressBar.setProgress(currentProgressHumedity)
            var texto=currentProgressHumedity.toString()+"%"
            textohumedad.setText(texto)
            if (currentProgressHumedity==0) opennotifa()
        }
        if (currentProgressWater != null) {
            progressBar2.setProgress(currentProgressWater)
            var texto2= currentProgressWater.toString()+"%"
            textoagua.setText(texto2)
            if (currentProgressWater<=20) opennotifa()
        }
        progressBar.setMax(100)
        progressBar2.setMax(100)
    }

    private fun buildNotificationCustomStyle() {
        val notificationLayout = RemoteViews(packageName, R.layout.notificacion)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notificacion2)

        notificationCustomStyle = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_baseline_eco_24)
            it.setCustomContentView(notificationLayout)
            it.setCustomBigContentView(notificationLayoutExpanded)
        }.build()
    }
//boton de notificacion
    private fun buttonsListener() {


        button.setOnClickListener {
            opennotifa()
        }
    }
    private fun opennotifa(){
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationCustomStyleID, notificationCustomStyle)
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
                lightColor = Color.RED
                enableLights(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
