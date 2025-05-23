package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class GameGelud : AppCompatActivity() {
    private var playerTurn = true
    private var playerHealth = 100
    private var robotHealth = 100
    private var star: Int = 0
    private var game_mode = 0
    private var monster: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_gelud)

        star = intent.getIntExtra("star", 0)
        monster = intent.getIntExtra("monster", 0)
        game_mode = intent.getIntExtra("game_mode", 0)

        val bomb = findViewById<ImageView>(R.id.bomb)
        val rock = findViewById<ImageView>(R.id.rock)
        val snow = findViewById<ImageView>(R.id.snow)
        val fire = findViewById<ImageView>(R.id.fire)

        val proyektilbom = findViewById<ImageView>(R.id.proyektilbom)
        val proyektibatu = findViewById<ImageView>(R.id.proyektilbatu)
        val proyektiles = findViewById<ImageView>(R.id.proyektiles)
        val proyektiapi = findViewById<ImageView>(R.id.proyektilapi)

        val playerHealthBar = findViewById<ProgressBar>(R.id.playerHealthBar)
        val robotHealthBar = findViewById<ProgressBar>(R.id.robotHealthBar)

        playerHealthBar.max = 100
        robotHealthBar.max = 100

        playerHealthBar.progress = playerHealth
        robotHealthBar.progress = robotHealth

        playerHealthBar.isVisible = true
        robotHealthBar.isVisible = true

        // Sembunyikan semua proyektil di awal
        hideAllProjectiles()
        updateHealthBars()

        bomb.setOnClickListener { launchProjectile(proyektilbom, 20, robotHealthBar) }
        rock.setOnClickListener { launchProjectile(proyektibatu, 15, robotHealthBar) }
        snow.setOnClickListener { launchProjectile(proyektiles, 10, robotHealthBar) }
        fire.setOnClickListener { launchProjectile(proyektiapi, 25, robotHealthBar) }
    }

    private fun updateHealthBars() {
        val playerHealthBar = findViewById<ProgressBar>(R.id.playerHealthBar)
        val robotHealthBar = findViewById<ProgressBar>(R.id.robotHealthBar)

        // Gunakan setProgress dengan single parameter
        playerHealthBar.progress = playerHealth
        robotHealthBar.progress = robotHealth

        Log.d("GameGelud", "Player Health: $playerHealth, Robot Health: $robotHealth")
        Log.d("GameGelud", "Player Health Bar Progress: ${playerHealthBar.progress}, Robot Health Bar Progress: ${robotHealthBar.progress}")
    }

    private fun hideAllProjectiles() {
        val allProjectiles = listOf(
            findViewById<ImageView>(R.id.proyektilbom),
            findViewById<ImageView>(R.id.proyektilbatu),
            findViewById<ImageView>(R.id.proyektiles),
            findViewById<ImageView>(R.id.proyektilapi),
            findViewById<ImageView>(R.id.proyektilbom2),
            findViewById<ImageView>(R.id.proyektilbatu2),
            findViewById<ImageView>(R.id.proyektiles2),
            findViewById<ImageView>(R.id.proyektilapi2)
        )
        allProjectiles.forEach {
            it.isVisible = false
            it.translationX = 0f
        }
    }

    private fun launchProjectile(playerProjectile: ImageView, damage: Int, robotHealthBar: ProgressBar) {
        if (playerTurn) {
            hideAllProjectiles()
            playerProjectile.isVisible = true
            playerProjectile.animate().translationXBy(800f).withEndAction {
                playerProjectile.isVisible = false

                // Ganti robotHealthBar.decrementProgressBy(damage) dengan:
                robotHealth = (robotHealth - damage).coerceAtLeast(0)
                robotHealthBar.progress = robotHealth

                updateHealthBars()
                checkGameOver()
                endPlayerTurn(damage)
            }
        }
    }

    private fun endPlayerTurn(damage: Int) {
        if (!playerTurn) return // Cegah eksekusi ganda jika sudah bukan giliran pemain

        playerTurn = false
        toggleButtons(false)

        // Tambahkan delay sebelum robot menyerang (1.5 detik)
        Handler(Looper.getMainLooper()).postDelayed({
            if (!playerTurn) { // Pastikan hanya menyerang jika masih giliran robot
                robotAttack(damage)
            }
        }, 1500)
    }

    private fun robotAttack(damage: Int) {
        hideAllProjectiles()

        val robotProjectiles = listOf(
            Pair(findViewById<ImageView>(R.id.proyektilbom2), 20),
            Pair(findViewById<ImageView>(R.id.proyektilbatu2), 15),
            Pair(findViewById<ImageView>(R.id.proyektiles2), 10),
            Pair(findViewById<ImageView>(R.id.proyektilapi2), 25)
        )

        // Pilih serangan acak untuk robot
        val (selectedProjectile, selectedDamage) = robotProjectiles.random()

        selectedProjectile.isVisible = true
        selectedProjectile.animate().translationXBy(-800f).withEndAction {
            selectedProjectile.isVisible = false
            playerHealth = (playerHealth - selectedDamage).coerceAtLeast(0)

            // Update player health bar
            findViewById<ProgressBar>(R.id.playerHealthBar).progress = playerHealth

            updateHealthBars()
            checkGameOver()

            if (playerHealth > 0) { // Pastikan game belum berakhir sebelum mengembalikan giliran
                startPlayerTurn()
            }
        }
    }


    private fun startPlayerTurn() {
        playerTurn = true
        toggleButtons(true)
    }

    private fun toggleButtons(enable: Boolean) {
        val bomb = findViewById<ImageView>(R.id.bomb)
        val rock = findViewById<ImageView>(R.id.rock)
        val snow = findViewById<ImageView>(R.id.snow)
        val fire = findViewById<ImageView>(R.id.fire)
        bomb.isEnabled = enable
        rock.isEnabled = enable
        snow.isEnabled = enable
        fire.isEnabled = enable
        bomb.isVisible = enable
        rock.isVisible = enable
        snow.isVisible = enable
        fire.isVisible = enable
    }

    private fun checkGameOver() {
        if (playerHealth <= 0) {
            toggleButtons(false)
                val intent = Intent(this, rute::class.java)
                startActivity(intent)
                return
        }else if (robotHealth <= 0) {
            toggleButtons(false)
                val intent = Intent(this, HasilQuiz::class.java)
                intent.putExtra("game_mode", game_mode)
                intent.putExtra("monster", monster)
                intent.putExtra("star", star)
                startActivity(intent)
                return
        }
    }
}
