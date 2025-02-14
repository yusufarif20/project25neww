package com.example.project25

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast

class rute : AppCompatActivity() {
    private lateinit var player: ImageView
    private val stepSize = 105f
    private val walls = mutableListOf<ImageView>()
    private var monster: Int = 0

    // Boundaries
    private var minX = 0f
    private var maxX = 0f
    private var minY = 0f
    private var maxY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rute)

        player = findViewById(R.id.player)
        // Tambahkan semua tembok ke dalam list
        walls.add(findViewById(R.id.tempat))
        walls.add(findViewById(R.id.hadiah))
        walls.add(findViewById(R.id.hadiah2))
        walls.add(findViewById(R.id.goa))
        val hadiah = findViewById<ImageView>(R.id.hadiah)
        val hadiah2 = findViewById<ImageView>(R.id.hadiah2)
        // Tambahkan tembok lainnya jika ada

        monster = intent.getIntExtra("monster", 0)

        if (monster < 5) {
            hadiah.visibility = View.GONE
        }
        if (monster < 4) {
            hadiah2.visibility = View.GONE
        }


        val forward = findViewById<ImageView>(R.id.forward)
        val backward = findViewById<ImageView>(R.id.backward)
        val right = findViewById<ImageView>(R.id.right)
        val left = findViewById<ImageView>(R.id.left)

        setupBoundaries()

        forward.setOnClickListener { movePlayer("forward") }
        backward.setOnClickListener { movePlayer("backward") }
        right.setOnClickListener { movePlayer("right") }
        left.setOnClickListener { movePlayer("left") }
    }

    private fun setupBoundaries() {
        val containerView = findViewById<android.view.View>(R.id.main)
        containerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                containerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                minX = 0f
                maxX = (containerView.width - player.width).toFloat()
                minY = 0f
                maxY = (containerView.height - player.height).toFloat()
            }
        })
    }

    private fun willCollideWithObject(wall: ImageView, newX: Float, newY: Float): Boolean {
        val wallBounds = Rect()
        wall.getGlobalVisibleRect(wallBounds)

        val playerBounds = Rect()
        player.getGlobalVisibleRect(playerBounds)

        // Adjust player bounds based on proposed movement
        val adjustedPlayerBounds = Rect(
            (playerBounds.left + (newX - player.translationX)).toInt(),
            (playerBounds.top + (newY - player.translationY)).toInt(),
            (playerBounds.right + (newX - player.translationX)).toInt(),
            (playerBounds.bottom + (newY - player.translationY)).toInt()
        )

        return Rect.intersects(adjustedPlayerBounds, wallBounds)
    }

    private fun willCollideWithAnyWall(newX: Float, newY: Float): Boolean {
        return walls.any { wall ->
            willCollideWithObject(wall, newX, newY)
        }
    }

    private fun movePlayer(direction: String) {
        val newX = when (direction) {
            "right" -> player.translationX + stepSize
            "left" -> player.translationX - stepSize
            else -> player.translationX
        }

        val newY = when (direction) {
            "forward" -> player.translationY - stepSize
            "backward" -> player.translationY + stepSize
            else -> player.translationY
        }
        val hadiah = findViewById<ImageView>(R.id.hadiah)

        if (willCollideWithObject(hadiah, newX, newY)) {
            val intent = Intent(this, GameDadu::class.java)
            intent.putExtra("monster", monster)
            startActivity(intent)
            return
        }
        val hadiah2 = findViewById<ImageView>(R.id.hadiah2)

        if (willCollideWithObject(hadiah2, newX, newY)) {
            val intent = Intent(this, GameUang::class.java)
            intent.putExtra("monster", monster)
            startActivity(intent)
            return
        }
        val goa = findViewById<ImageView>(R.id.goa)

        if (willCollideWithObject(goa, newX, newY)) {
            val intent = Intent(this, HasilQuiz::class.java)
            intent.putExtra("monster", monster)
            startActivity(intent)
            return
        }

        if (isValidPosition(newX, newY) && !willCollideWithAnyWall(newX, newY)) {
            val animator = when (direction) {
                "forward" -> ObjectAnimator.ofFloat(player, "translationY", newY)
                "backward" -> ObjectAnimator.ofFloat(player, "translationY", newY)
                "right" -> ObjectAnimator.ofFloat(player, "translationX", newX)
                "left" -> ObjectAnimator.ofFloat(player, "translationX", newX)
                else -> null
            }

            animator?.apply {
                duration = 500
                start()
            }
        } else {
            Toast.makeText(this, "Tidak bisa bergerak ke arah tersebut!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidPosition(x: Float, y: Float): Boolean {
        val absoluteX = player.left + x
        val absoluteY = player.top + y

        return absoluteX >= minX &&
                absoluteX <= maxX &&
                absoluteY >= minY &&
                absoluteY <= maxY
    }
}