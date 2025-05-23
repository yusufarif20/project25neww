package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HasilQuiz : AppCompatActivity() {

    private var game_mode = 0
    var monster : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_quiz)

        monster = intent.getIntExtra("monster", 0)
        game_mode = intent.getIntExtra("game_mode", 0)
        var star = intent.getIntExtra("star", 0)
        game_mode = 2

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
            val intent = Intent(this, rute::class.java)
            intent.putExtra("game_mode", game_mode)
            intent.putExtra("monster", monster)
            startActivity(intent)
            finish()
        }
    }
}
