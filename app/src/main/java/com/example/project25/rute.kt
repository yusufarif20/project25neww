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
import android.util.Log

class rute : AppCompatActivity() {
    companion object {
        private const val TAG = "RuteGame"
    }
    private lateinit var player: ImageView
    private val stepSize = 10f
    private val walls = mutableListOf<ImageView>()
    private var monster: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private var isMoving = false
    private var currentDirection = ""
    private val moveDelay = 50L
    private var lastPlayerX = 0f
    private var lastPlayerY = 0f

    private val completedHadiah = mutableSetOf<Int>()

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
        walls.add(findViewById(R.id.hadiah4))
        walls.add(findViewById(R.id.hadiah5))
        walls.add(findViewById(R.id.goa))

        lastPlayerX = intent.getFloatExtra("lastX", 0f)
        lastPlayerY = intent.getFloatExtra("lastY", 0f)
        monster = intent.getIntExtra("monster", 0)

        val completedString = intent.getStringExtra("completedHadiah") ?: ""
        if (completedString.isNotEmpty()) {
            completedHadiah.addAll(completedString.split(",").map { it.toInt() })
        }

        val home = findViewById<ImageView>(R.id.home)

        player.translationX = lastPlayerX
        player.translationY = lastPlayerY

        val hadiah = findViewById<ImageView>(R.id.hadiah)
        val hadiah2 = findViewById<ImageView>(R.id.hadiah2)
        val hadiah3 = findViewById<ImageView>(R.id.hadiah3)
        val hadiah4 = findViewById<ImageView>(R.id.hadiah4)
        val hadiah5 = findViewById<ImageView>(R.id.hadiah5)

        // Hadiah 1 (monster < 4)
        hadiah.visibility = if (monster < 4 || 1 in completedHadiah) View.GONE else View.VISIBLE

        // Hadiah 2 (monster < 3)
        hadiah2.visibility = if (monster < 3 || 2 in completedHadiah) View.GONE else View.VISIBLE

        // Hadiah 3 (monster < 5)
        hadiah3.visibility = if (monster < 5 || 3 in completedHadiah) View.GONE else View.VISIBLE

        // Hadiah 4 (monster < 2)
        hadiah4.visibility = if (monster < 2 || 4 in completedHadiah) View.GONE else View.VISIBLE

        hadiah5.visibility = if (monster < 2 || 5 in completedHadiah) View.GONE else View.VISIBLE

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

        home.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            intent.putExtra("lastX", lastPlayerX)
            intent.putExtra("lastY", lastPlayerY)
            intent.putExtra("monster", monster)
            startActivity(intent)
            finish()
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

        Log.d(TAG, "Moving player - Direction: $direction, NewX: $newX, NewY: $newY")

        // Cek tabrakan dengan hadiah1
        val hadiah = findViewById<ImageView>(R.id.hadiah)
        if (willCollideWithObject(hadiah, newX, newY) && hadiah.visibility == View.VISIBLE) {
            handleHadiahCollision(newX, newY, 1, GameDadu::class.java)
            return
        }

        // Cek tabrakan dengan hadiah2
        val hadiah2 = findViewById<ImageView>(R.id.hadiah2)
        if (willCollideWithObject(hadiah2, newX, newY) && hadiah2.visibility == View.VISIBLE) {
            handleHadiahCollision(newX, newY, 2, GameUang::class.java)
            return
        }

        // Cek tabrakan dengan hadiah3
        val hadiah3 = findViewById<ImageView>(R.id.hadiah3)
        if (willCollideWithObject(hadiah3, newX, newY) && hadiah3.visibility == View.VISIBLE) {
            handleHadiahCollision(newX, newY, 3, GamePizza::class.java)
            return
        }

        // Cek tabrakan dengan hadiah4
        val hadiah4 = findViewById<ImageView>(R.id.hadiah4)
        if (willCollideWithObject(hadiah4, newX, newY) && hadiah4.visibility == View.VISIBLE) {
            handleHadiahCollision(newX, newY, 4, GameBentuk::class.java)
            return
        }

        val hadiah5 = findViewById<ImageView>(R.id.hadiah5)
        if (willCollideWithObject(hadiah5, newX, newY) && hadiah5.visibility == View.VISIBLE) {
            handleHadiahCollision(newX, newY, 5, GameNomor::class.java)
            return
        }

        val goa = findViewById<ImageView>(R.id.goa)
        if (willCollideWithObject(goa, newX, newY)) {
            isMoving = false
            handler.removeCallbacks(moveRunnable)
            val intent = Intent(this, HasilQuiz::class.java)
            intent.putExtra("monster", monster)
            intent.putExtra("completedHadiah", completedHadiah.joinToString(","))
            startActivity(intent)
            return
        }

        if (isValidPosition(newX, newY) && !willCollideWithAnyWall(newX, newY)) {
            player.translationX = newX
            player.translationY = newY
        } else {
            isMoving = false
            handler.removeCallbacks(moveRunnable)
        }
    }

    private fun handleHadiahCollision(newX: Float, newY: Float, hadiahId: Int, gameClass: Class<*>) {
        isMoving = false
        handler.removeCallbacks(moveRunnable)
        lastPlayerX = newX
        lastPlayerY = newY

        val intent = Intent(this, gameClass)
        intent.putExtra("lastX", lastPlayerX)
        intent.putExtra("lastY", lastPlayerY)
        intent.putExtra("monster", monster)
        intent.putExtra("currentHadiah", hadiahId)
        intent.putExtra("completedHadiah", completedHadiah.joinToString(","))
        startActivity(intent)
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