package com.example.project25

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HalamanUtama : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_halaman_utama)

        // Mendapatkan referensi dari objek di layout
        val buttonMulai = findViewById<ImageView>(R.id.Buttonmulai)
        val textTitle = findViewById<ImageView>(R.id.textTitle)
        val robot = findViewById<ImageView>(R.id.robot)
        val kata = findViewById<ImageView>(R.id.kata)

        // Set objek tidak terlihat sebelum animasi dimulai
        buttonMulai.alpha = 0f
        textTitle.alpha = 0f
        robot.alpha = 0f
        kata.alpha = 0f

        // Animasi Fade In untuk textTitle
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        textTitle.alpha = 1f
        textTitle.startAnimation(fadeIn)

        // Delay untuk animasi move up pada textTitle
        Handler().postDelayed({
            val moveUpTitle = ObjectAnimator.ofFloat(textTitle, "translationY", 0f, -200f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }, 1000) // 1 detik setelah fade in

        // Delay untuk animasi fade in robot
        Handler().postDelayed({
            val fadeInRobot = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            robot.alpha = 1f
            robot.startAnimation(fadeInRobot)
        }, 2000) // 2 detik setelah textTitle move up

        // Delay untuk animasi fade in kata
        Handler().postDelayed({
            val fadeInKata = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            kata.startAnimation(fadeInKata)
            kata.alpha = 1f
        }, 3000) // 3 detik setelah robot fade in

        // Delay untuk animasi fade in dan bounce pada buttonPlay
        Handler().postDelayed({
            val fadeInButton = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            val bounceButton = AnimationUtils.loadAnimation(this, R.anim.loop_up_down)
            buttonMulai.startAnimation(fadeInButton)
            buttonMulai.startAnimation(bounceButton)
            buttonMulai.alpha = 1f
        }, 4000) // 4 detik setelah kata fade in

        // Action ketika tombol Play diklik
        buttonMulai.setOnClickListener {
            // Pindah ke aktivitas berikutnya
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
            finish()
        }
    }
}
