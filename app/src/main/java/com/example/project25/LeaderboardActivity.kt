package com.example.project25

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private val leaderboardList = mutableListOf<LeaderboardItem>()

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
        recyclerView = findViewById(R.id.recyclerViewLeaderboard)
        recyclerView.layoutManager = LinearLayoutManager(this)
        leaderboardAdapter = LeaderboardAdapter(leaderboardList)
        recyclerView.adapter = leaderboardAdapter

        // Ambil data dari Firebase
        loadLeaderboardData()
    }

    private fun loadLeaderboardData() {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                leaderboardList.clear() // Hapus data lama

                for (userSnapshot in snapshot.children) {
                    val name = userSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val star = userSnapshot.child("star").getValue(Int::class.java) ?: 0
                    leaderboardList.add(LeaderboardItem(name, star))
                }

                // Urutkan berdasarkan skor tertinggi
                leaderboardList.sortByDescending { it.score }
                leaderboardAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Gagal mengambil data leaderboard", error.toException())
            }
        })
    }
}


// Data class for leaderboard items
data class LeaderboardItem(
    val name: String,
    val score: Int
)

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
