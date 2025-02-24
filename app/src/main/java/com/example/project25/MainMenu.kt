package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainMenu : AppCompatActivity(), View.OnClickListener {

    var monster : Int = 0
    private var lastPlayerX = 0f
    private var lastPlayerY = 0f

    private lateinit var homeButton: FrameLayout
    private lateinit var mainButton: FrameLayout
    private lateinit var profileButton: FrameLayout
    private lateinit var leaderboardButton: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        monster = intent.getIntExtra("monster", 0)
        lastPlayerX = intent.getFloatExtra("lastX", 0f)
        lastPlayerY = intent.getFloatExtra("lastY", 0f)

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
            R.id.fl_main -> {
                val intent = Intent(this,rute::class.java)
                intent.putExtra("monster", monster)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
                finish()
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