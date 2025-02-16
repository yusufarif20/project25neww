package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HasilQuiz : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_quiz)

        // Ambil data dari Intent
        val currentScore = intent.getIntExtra("currentScore", 0)
        val True = intent.getIntExtra("True", 0)
        val False = intent.getIntExtra("False", 0)

        // Ambil referensi ke TextView
        val resultTextView = findViewById<TextView>(R.id.TextView)
        val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        val trueTextView = findViewById<TextView>(R.id.TrueTextView)
        val submit = findViewById<ImageView>(R.id.submit)

        // Logika hasil berdasarkan jumlah jawaban benar dan salah
        if (True > 3) {
            resultTextView.text = "Selamat anda lolos"
        } else if (False > 2) {
            resultTextView.text = "Maaf anda gagal"
        } else {
            resultTextView.text = "Coba lagi!"
        }

        // Tampilkan skor, jumlah benar, dan salah
        scoreTextView.text = "$currentScore"
        trueTextView.text = "$True"

        submit.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}
