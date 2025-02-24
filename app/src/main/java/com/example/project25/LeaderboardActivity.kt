package com.example.project25

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LeaderboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leaderboard)

        // Setup title text view
        val textView = findViewById<TextView>(R.id.papanPeringkat)
        textView.paint.style = Paint.Style.STROKE
        textView.paint.strokeWidth = 8f
        textView.setTextColor(Color.parseColor("#F0A04B"))
        textView.text = "Papan Peringkat"

        textView.post {
            textView.paint.style = Paint.Style.FILL
            textView.setTextColor(Color.WHITE)
        }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewLeaderboard)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create dummy data
        val dummyData = listOf(
            LeaderboardItem("Budi", 95),
            LeaderboardItem("Ani", 90),
            LeaderboardItem("Tono", 85)
        )

        // Set adapter with dummy data
        recyclerView.adapter = LeaderboardAdapter(dummyData)
    }
}

// Data class for leaderboard items
data class LeaderboardItem(
    val name: String,
    val score: Int
)

// Adapter class
class LeaderboardAdapter(private val items: List<LeaderboardItem>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.nameTextView)
        val scoreText: TextView = view.findViewById(R.id.scoreTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameText.text = item.name
        holder.scoreText.text = item.score.toString()
    }

    override fun getItemCount() = items.size
}