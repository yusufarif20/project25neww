package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainMenu : AppCompatActivity(), View.OnClickListener {

    private lateinit var homeButton: FrameLayout
    private lateinit var mainButton: FrameLayout
    private lateinit var profileButton: FrameLayout
    private lateinit var leaderboardButton: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        // Initialize buttons
        homeButton = findViewById(R.id.fl_home)
        mainButton = findViewById(R.id.fl_main)
        profileButton = findViewById(R.id.fl_profile)
        leaderboardButton = findViewById(R.id.fl_leaderboard)

        // Set click listeners
        homeButton.setOnClickListener(this)
        mainButton.setOnClickListener(this)
        profileButton.setOnClickListener(this)
        leaderboardButton.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fl_home -> {
            }
            R.id.fl_main -> {
                // Navigate to Main activity
                val intent = Intent(this, rute::class.java)
                startActivity(intent)
            }
            R.id.fl_profile -> {
                // Navigate to Profile activity
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.fl_leaderboard -> {
                // Navigate to Leaderboard activity
                val intent = Intent(this, LeaderboardActivity::class.java)
                startActivity(intent)
            }
        }
    }
}