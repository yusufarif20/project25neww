package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val splashScreenDuration = 5000L

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HalamanUtama::class.java)
            startActivity(intent)
            finish()
        }, splashScreenDuration)
    }
}
