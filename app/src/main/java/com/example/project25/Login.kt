package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    private lateinit var loginButton: ImageView
    private lateinit var registerButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        registerButton = findViewById(R.id.tv_register)
        loginButton = findViewById(R.id.btnlogin)

        registerButton.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
            finish()
        }
        loginButton.setOnClickListener {
            val intent = Intent(this, PilihGameActivity::class.java)
            startActivity(intent)
        }
    }
}