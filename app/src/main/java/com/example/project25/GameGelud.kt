package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class GameGelud : AppCompatActivity() {
    private var playerTurn = true
    private var playerHealth = 100
    private var robotHealth = 100
    private var star: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_gelud)

        val bomb = findViewById<ImageView>(R.id.bomb)
        val rock = findViewById<ImageView>(R.id.rock)
        val snow = findViewById<ImageView>(R.id.snow)
        val fire = findViewById<ImageView>(R.id.fire)

        val proyektilbom = findViewById<ImageView>(R.id.proyektilbom)
        val proyektibatu = findViewById<ImageView>(R.id.proyektilbatu)
        val proyektiles = findViewById<ImageView>(R.id.proyektiles)
        val proyektiapi = findViewById<ImageView>(R.id.proyektilapi)

        val proyektilbom2 = findViewById<ImageView>(R.id.proyektilbom2)
        val proyektibatu2 = findViewById<ImageView>(R.id.proyektilbatu2)
        val proyektiles2 = findViewById<ImageView>(R.id.proyektiles2)
        val proyektiapi2 = findViewById<ImageView>(R.id.proyektilapi2)

        val playerHealthBar = findViewById<ProgressBar>(R.id.playerHealthBar)
        val robotHealthBar = findViewById<ProgressBar>(R.id.robotHealthBar)

        playerHealthBar.max = 100
        robotHealthBar.max = 100

        // Sembunyikan semua proyektil di awal
        hideAllProjectiles()
        updateHealthBars()

        bomb.setOnClickListener { launchProjectile(proyektilbom, 20) }
        rock.setOnClickListener { launchProjectile(proyektibatu, 15) }
        snow.setOnClickListener { launchProjectile(proyektiles, 10) }
        fire.setOnClickListener { launchProjectile(proyektiapi, 25) }
    }

    private fun updateHealthBars() {
        findViewById<ProgressBar>(R.id.playerHealthBar).progress = playerHealth
        findViewById<ProgressBar>(R.id.robotHealthBar).progress = robotHealth
        Log.d("GameGelud","Player Health: $playerHealth, Robot Health: $robotHealth")
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

    private fun launchProjectile(playerProjectile: ImageView, damage: Int) {
        if (playerTurn) {
            hideAllProjectiles()
            playerProjectile.isVisible = true
            playerProjectile.animate().translationXBy(800f).withEndAction {
                playerProjectile.isVisible = false
                robotHealth = (robotHealth - damage).coerceAtLeast(0)
                updateHealthBars()
                checkGameOver()
                endPlayerTurn(damage)
            }
        }
    }

    private fun endPlayerTurn(damage: Int) {
        playerTurn = false
        toggleButtons(false)
        robotAttack(damage)
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
            updateHealthBars()
            checkGameOver()
            startPlayerTurn()
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
        if (playerHealth <= 0 || robotHealth <= 0) {
            toggleButtons(false)
                val intent = Intent(this, HasilQuiz::class.java)
                intent.putExtra("star", star)
                startActivity(intent)
                return
        }
    }
}
