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
        var star = intent.getIntExtra("star", 0)

        // Ambil referensi ke TextView
        val resultTextView = findViewById<TextView>(R.id.TextView)
        val trueTextView = findViewById<TextView>(R.id.TrueTextView)
        val submit = findViewById<ImageView>(R.id.submit)

        // Logika hasil berdasarkan jumlah jawaban benar dan salah
        if (star > 3) {
            resultTextView.text = "Selamat anda lolos"
        } else if (star > 2) {
            resultTextView.text = "Maaf anda gagal"
        } else {
            resultTextView.text = "Coba lagi!"
        }

        // Tampilkan skor, jumlah benar, dan salah
        trueTextView.text = "$star"

        submit.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}
