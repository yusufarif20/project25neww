package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textTitle = findViewById<ImageView>(R.id.textTitle)

        Handler().postDelayed({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            textTitle.startAnimation(fadeOut)
            Handler().postDelayed({
                val intent = Intent(this, HalamanUtama::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
                finish()
            }, 1000)
        }, 1000)
    }
}