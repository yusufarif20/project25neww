package com.example.project25

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import android.os.Handler
import android.os.Looper

class rute : AppCompatActivity() {
    private lateinit var player: ImageView
    private val stepSize = 10f
    private val walls = mutableListOf<ImageView>()
    private var monster: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private var isMoving = false
    private var currentDirection = ""
    private val moveDelay = 50L

    // Boundaries
    private var minX = 0f
    private var maxX = 0f
    private var minY = 0f
    private var maxY = 0f

    private val moveRunnable = object : Runnable {
        override fun run() {
            if (isMoving) {
                movePlayer(currentDirection)
                handler.postDelayed(this, moveDelay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rute)

        player = findViewById(R.id.player)
        // Tambahkan semua tembok ke dalam list
        walls.add(findViewById(R.id.tempat))
        walls.add(findViewById(R.id.tempat2))
        walls.add(findViewById(R.id.tempat3))
        walls.add(findViewById(R.id.tempat4))
        walls.add(findViewById(R.id.tempat5))
        walls.add(findViewById(R.id.tempat6))
        walls.add(findViewById(R.id.tempat7))
        walls.add(findViewById(R.id.tempat8))
        walls.add(findViewById(R.id.tempat9))
        walls.add(findViewById(R.id.tempat10))
        walls.add(findViewById(R.id.tempat11))
        walls.add(findViewById(R.id.tempat12))
        walls.add(findViewById(R.id.tempat13))
        walls.add(findViewById(R.id.tempat14))
        walls.add(findViewById(R.id.tempat15))
        walls.add(findViewById(R.id.tempat16))
        walls.add(findViewById(R.id.tempat17))
        walls.add(findViewById(R.id.tempat18))
        walls.add(findViewById(R.id.tempat19))
        walls.add(findViewById(R.id.tempat20))
        walls.add(findViewById(R.id.tempat21))
        walls.add(findViewById(R.id.tempat22))
        walls.add(findViewById(R.id.tempat23))
        walls.add(findViewById(R.id.tempat24))
        walls.add(findViewById(R.id.tempat25))
        walls.add(findViewById(R.id.tempat26))
        walls.add(findViewById(R.id.wall1))
        walls.add(findViewById(R.id.wall2))
        walls.add(findViewById(R.id.wall3))
        walls.add(findViewById(R.id.wall4))
        walls.add(findViewById(R.id.hadiah))
        walls.add(findViewById(R.id.hadiah2))
        walls.add(findViewById(R.id.hadiah3))
        walls.add(findViewById(R.id.goa))

        monster = intent.getIntExtra("monster", 0)

        val hadiah = findViewById<ImageView>(R.id.hadiah)
        val hadiah2 = findViewById<ImageView>(R.id.hadiah2)
        val hadiah3 = findViewById<ImageView>(R.id.hadiah3)

        if (monster < 4) {
            hadiah.visibility = View.GONE
        }
        if (monster < 3) {
            hadiah2.visibility = View.GONE
        }
        if (monster < 5) {
            hadiah3.visibility = View.GONE
        }

        val forward = findViewById<ImageView>(R.id.forward)
        val backward = findViewById<ImageView>(R.id.backward)
        val right = findViewById<ImageView>(R.id.right)
        val left = findViewById<ImageView>(R.id.left)

        setupBoundaries()

        val touchListener = View.OnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isMoving = true
                    currentDirection = when (view.id) {
                        R.id.forward -> "forward"
                        R.id.backward -> "backward"
                        R.id.right -> "right"
                        R.id.left -> "left"
                        else -> ""
                    }
                    handler.post(moveRunnable)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isMoving = false
                    handler.removeCallbacks(moveRunnable)
                    true
                }
                else -> false
            }
        }

        forward.setOnTouchListener(touchListener)
        backward.setOnTouchListener(touchListener)
        right.setOnTouchListener(touchListener)
        left.setOnTouchListener(touchListener)
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
            isMoving = false
            handler.removeCallbacks(moveRunnable)
            val intent = Intent(this, GameDadu::class.java)
            intent.putExtra("monster", monster)
            startActivity(intent)
            return
        }

        val hadiah2 = findViewById<ImageView>(R.id.hadiah2)

        if (willCollideWithObject(hadiah2, newX, newY)) {
            isMoving = false
            handler.removeCallbacks(moveRunnable)
            val intent = Intent(this, GameUang::class.java)
            intent.putExtra("monster", monster)
            startActivity(intent)
            return
        }

        val hadiah3 = findViewById<ImageView>(R.id.hadiah3)

        if (willCollideWithObject(hadiah3, newX, newY)) {
            isMoving = false
            handler.removeCallbacks(moveRunnable)
            val intent = Intent(this, GamePizza::class.java)
            intent.putExtra("monster", monster)
            startActivity(intent)
            return
        }

        val goa = findViewById<ImageView>(R.id.goa)

        if (willCollideWithObject(goa, newX, newY)) {
            isMoving = false
            handler.removeCallbacks(moveRunnable)
            val intent = Intent(this, HasilQuiz::class.java)
            intent.putExtra("monster", monster)
            startActivity(intent)
            return
        }

        if (isValidPosition(newX, newY) && !willCollideWithAnyWall(newX, newY)) {
            player.translationX = newX
            player.translationY = newY
        } else {
            isMoving = false
            handler.removeCallbacks(moveRunnable)
            Toast.makeText(this, "Tidak bisa bergerak ke arah tersebut!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBoundaries() {
        val containerView = findViewById<View>(R.id.main)
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

    private fun isValidPosition(x: Float, y: Float): Boolean {
        val absoluteX = player.left + x
        val absoluteY = player.top + y

        return absoluteX >= minX &&
                absoluteX <= maxX &&
                absoluteY >= minY &&
                absoluteY <= maxY
    }
}