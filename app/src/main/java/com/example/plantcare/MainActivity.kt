package com.example.plantcare

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var CurrentProgressHumedity = 70
        var CurrentProgressWater= 60
        var progressBar: ProgressBar? = null
        var progressBar2: ProgressBar? = null



        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar2 = findViewById<ProgressBar>(R.id.progressBar2)

        progressBar.setProgress(CurrentProgressHumedity)
        progressBar2.setProgress(CurrentProgressWater)

        progressBar.setMax(100)
        progressBar2.setMax(100)


    }
}