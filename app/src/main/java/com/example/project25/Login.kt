package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {

    var monster : Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val tv_register = findViewById<TextView>(R.id.tv_register)
        val btn_login = findViewById<ImageView>(R.id.btnlogin)

        btn_login.setOnClickListener{
            val intent = Intent(this,MainMenu::class.java)
            intent.putExtra("monster", monster)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
            finish()
        }

        tv_register.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
            finish()
        }
    }
}