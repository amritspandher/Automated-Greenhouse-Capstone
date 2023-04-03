package com.example.automatedgreenhouse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton

class StatsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        var bt_home = findViewById<AppCompatImageButton>(R.id.bt_homeAnalytics)
        bt_home?.setOnClickListener {
            //uncomment below line to sync with app
            startActivity(Intent(this, MainDisplay::class.java))
        }

        var bt_back = findViewById<AppCompatImageButton>(R.id.bt_backAnalytics)
        bt_back?.setOnClickListener {
            finish()
        }

        val HUMID_url = "https://automatedgreenhouse.grafana.net/d/zHAqZiQnz/greenhousehumidity?orgId=1&kiosk"
        val HUMID_intent = Intent(Intent.ACTION_VIEW)
        HUMID_intent.data = Uri.parse(HUMID_url)

        val SM_url = "https://automatedgreenhouse.grafana.net/d/jH0Fc7Qnk/greenhousesoilmoisture?orgId=1&kiosk"
        val SM_intent = Intent(Intent.ACTION_VIEW)
        SM_intent.data = Uri.parse(SM_url)

        val TEMP_url = "https://automatedgreenhouse.grafana.net/d/bzQXyBz/greenhousetemperature?orgId=1&kiosk"
        val TEMP_intent = Intent(Intent.ACTION_VIEW)
        TEMP_intent.data = Uri.parse(TEMP_url)

        val COMB_url = "https://automatedgreenhouse.grafana.net/d/8K5rNp97z/combineddata?orgId=1&kiosk"
        val COMB_intent = Intent(Intent.ACTION_VIEW)
        COMB_intent.data = Uri.parse(COMB_url)

        val scheduleButtonHUMID = findViewById<AppCompatImageButton>(R.id.humidityGrafana)
        scheduleButtonHUMID.setOnClickListener {
            startActivity(HUMID_intent)
        }

        val scheduleButtonSM = findViewById<AppCompatImageButton>(R.id.soilMoistureGrafana)
        scheduleButtonSM.setOnClickListener {
            startActivity(SM_intent)
        }

        val scheduleButtonTEMP = findViewById<AppCompatImageButton>(R.id.temperatureGrafana)
        scheduleButtonTEMP.setOnClickListener {
            startActivity(TEMP_intent)
        }

        val scheduleButtonCOMB = findViewById<AppCompatImageButton>(R.id.combinedGrafana)
        scheduleButtonCOMB.setOnClickListener {
            startActivity(COMB_intent)
        }
    }
}