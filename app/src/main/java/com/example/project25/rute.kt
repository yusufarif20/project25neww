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
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class rute : AppCompatActivity() {
    companion object {
        const val TAG = "RuteGame"
    }
    private lateinit var player: ImageView
    private lateinit var robot: ImageView
    private val stepSize = 10f
    private val walls = mutableListOf<ImageView>()
    private var monster: Int = 0
    private var game_mode: Int = 0
    private var star: Int = 0
    private var hartaid: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private var isMoving = false
    private var currentDirection = ""
    private val moveDelay = 50L
    private var lastPlayerX = 0f
    private var lastPlayerY = 0f
    private var lastRobotX = 0f
    private var lastRobotY = 0f
    private var hasShownToast = false
    private var currentRotation = 0f
    private val robotHandler = Handler(Looper.getMainLooper())
    private val robotMoveDelay = 3000L // 3 detik
    private val robotStepSize = 10f
    private var isRobotMoving = false
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String


    private val robotMoveRunnable = object : Runnable {
        override fun run() {
            if (isRobotMoving) {
                moveRobot()
                robotHandler.postDelayed(this, moveDelay) // Gerak terus setiap 50ms
            }
        }
    }

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
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            uid = user.uid
            database = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        } else {
            // User belum login, handle error atau arahkan login
            Toast.makeText(this, "Anda harus login untuk menyimpan progress", Toast.LENGTH_SHORT).show()
            finish()
        }
        loadGameProgress()
        game_mode = intent.getIntExtra("game_mode", 1)
        if (game_mode == 1) {
            setContentView(R.layout.activity_rute)
        } else {
            setContentView(R.layout.activity_rute2)
        }

        val menuButton: ImageView = findViewById(R.id.menu)
        val dropdownContainer: LinearLayout = findViewById(R.id.dropdownContainer)

        menuButton.setOnClickListener {
            dropdownContainer.visibility = if (dropdownContainer.visibility == View.GONE)
                View.VISIBLE else View.GONE
        }

        player = findViewById(R.id.player)
        robot = findViewById(R.id.robot)

        // Memulai gerakan robot setelah 3 detik
        robotHandler.postDelayed({
            isRobotMoving = true
            robotMoveRunnable.run()
        }, robotMoveDelay)

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
        walls.add(findViewById(R.id.hadiah6))
        walls.add(findViewById(R.id.goa))
        walls.add(findViewById(R.id.harta))

        lastPlayerX = intent.getFloatExtra("lastX", 0f)
        lastPlayerY = intent.getFloatExtra("lastY", 0f)
        lastRobotX = intent.getFloatExtra("lastRobotX", 0f)
        lastRobotY = intent.getFloatExtra("lastRobotY", 0f)
        monster = intent.getIntExtra("monster", 0)
        Log.d("GameDadu", "Nilai star yang diterima: $star")

        val completedString = intent.getStringExtra("completedHadiah") ?: ""
        if (completedString.isNotEmpty()) {
            completedHadiah.addAll(completedString.split(",").map { it.toInt() })
        }

        val home = findViewById<ImageView>(R.id.home)

        player.translationX = lastPlayerX
        player.translationY = lastPlayerY
        robot.translationX = lastRobotX
        robot.translationY = lastRobotY

        val hadiah = findViewById<ImageView>(R.id.hadiah)
        val hadiah2 = findViewById<ImageView>(R.id.hadiah2)
        val hadiah3 = findViewById<ImageView>(R.id.hadiah3)
        val hadiah4 = findViewById<ImageView>(R.id.hadiah4)
        val hadiah5 = findViewById<ImageView>(R.id.hadiah5)
        val hadiah6 = findViewById<ImageView>(R.id.hadiah6)

        hadiah.visibility = if (monster < 4 || 1 in completedHadiah) View.GONE else View.VISIBLE
        hadiah2.visibility = if (monster < 3 || 2 in completedHadiah) View.GONE else View.VISIBLE
        hadiah3.visibility = if (monster < 5 || 3 in completedHadiah) View.GONE else View.VISIBLE
        hadiah4.visibility = if (monster < 2 || 4 in completedHadiah) View.GONE else View.VISIBLE
        hadiah5.visibility = if (monster < 2 || 5 in completedHadiah) View.GONE else View.VISIBLE
        hadiah6.visibility = if (monster < 2 || 6 in completedHadiah) View.GONE else View.VISIBLE

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
                        R.id.forward -> {
                            rotatePlayer(180f)
                            rotateRobot(180f)
                            "forward"
                        }
                        R.id.backward -> {
                            rotatePlayer(0f)
                            rotateRobot(0f)
                            "backward"
                        }
                        R.id.right -> {
                            rotatePlayer(-90f)
                            rotateRobot(-90f)
                            "right"
                        }
                        R.id.left -> {
                            rotatePlayer(90f)
                            rotateRobot(90f)
                            "left"
                        }
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

        listenUserStars()

        home.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            intent.putExtra("lastX", lastPlayerX)
            intent.putExtra("lastY", lastPlayerY)
            intent.putExtra("monster", monster)
            intent.putExtra("star", star)
            startActivity(intent)
            finish()
        }
    }

    private fun saveCompletedHadiah() {
        val hadiahString = completedHadiah.joinToString(",")
        database.child("gameProgress").child("completedHadiah").setValue(hadiahString)
            .addOnSuccessListener {
                Log.d(TAG, "CompletedHadiah berhasil disimpan: $hadiahString")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Gagal menyimpan completedHadiah: ${e.message}")
            }
    }


    private fun saveGameProgress() {
        val progress = mapOf(
            "lastPlayerX" to player.translationX,
            "lastPlayerY" to player.translationY,
            "lastRobotX" to robot.translationX,
            "lastRobotY" to robot.translationY,
            "monster" to monster,
            "star" to star,
            "completedHadiah" to completedHadiah.joinToString(",")
        )

        database.child("gameProgress").setValue(progress)
            .addOnSuccessListener {
                Log.d(TAG, "Progress game berhasil disimpan")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Gagal menyimpan progress game: ${e.message}")
            }
    }

    private fun listenUserStars() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val bintang = findViewById<TextView>(R.id.bintang)

        if (user != null) {
            val uid = user.uid
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            val starsRef = database.child("users").child(uid).child("stars")

            starsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val stars = snapshot.getValue(Int::class.java) ?: 0
                    Log.d("Get Stars", "Done + Stars didapat $stars")
                    star = stars
                    bintang.text = stars.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Get Stars", "Failed")
                    println("Gagal membaca stars: ${error.message}")
                }
            })
        } else {
            Log.d("Get Stars", "Done")
            println("User belum login")
        }
    }

    private fun rotatePlayer(angle: Float) {
        // Menggunakan animasi untuk rotasi agar lebih smooth
        val rotationAnimator = ObjectAnimator.ofFloat(player, "rotation", angle)
        rotationAnimator.duration = 300 // Durasi animasi dalam milidetik
        rotationAnimator.start()

        // Simpan sudut rotasi untuk digunakan nanti jika diperlukan
        currentRotation = angle

        // Log untuk debugging
        Log.d(TAG, "Player rotated to: $angle degrees")
    }

    private fun rotateRobot(angle: Float) {
        // Menggunakan animasi untuk rotasi agar lebih smooth
        val rotationAnimator = ObjectAnimator.ofFloat(robot, "rotation", angle)
        rotationAnimator.duration = 300 // Durasi animasi dalam milidetik
        rotationAnimator.start()

        // Simpan sudut rotasi untuk digunakan nanti jika diperlukan
        currentRotation = angle

        // Log untuk debugging
        Log.d(TAG, "Robot rotated to: $angle degrees")
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
            handleHadiahCollision(newX, newY, 1, GameDadu::class.java, star)
            return
        }

        // Cek tabrakan dengan hadiah2
        val hadiah2 = findViewById<ImageView>(R.id.hadiah2)
        if (willCollideWithObject(hadiah2, newX, newY) && hadiah2.visibility == View.VISIBLE) {
            handleHadiahCollision(newX, newY, 2, GameUang::class.java, star)
            return
        }

        // Cek tabrakan dengan hadiah3
        val hadiah3 = findViewById<ImageView>(R.id.hadiah3)
        if (willCollideWithObject(hadiah3, newX, newY) && hadiah3.visibility == View.VISIBLE) {
            handleHadiahCollision(newX, newY, 3, GamePizza::class.java, star)
            return
        }
        // Cek tabrakan dengan hadiah4
        val hadiah4 = findViewById<ImageView>(R.id.hadiah4)
        if (willCollideWithObject(hadiah4, newX, newY) && hadiah4.visibility == View.VISIBLE) {
            handleHadiahCollision(newX, newY, 4, GameBentuk::class.java, star)
            return
        }
        val hadiah5 = findViewById<ImageView>(R.id.hadiah5)
        if (willCollideWithObject(hadiah5, newX, newY) && hadiah5.visibility == View.VISIBLE) {
            handleHadiahCollision(newX, newY, 5, GameNomor::class.java, star)
            return
        }
        val hadiah6 = findViewById<ImageView>(R.id.hadiah6)
        if (willCollideWithObject(hadiah6, newX, newY) && hadiah6.visibility == View.VISIBLE) {
            handleHadiahCollision(newX, newY, 6, GameBuah::class.java, star)
            return
        }

        val harta= findViewById<ImageView>(R.id.harta)
        val bintang = findViewById<TextView>(R.id.bintang)
        if (willCollideWithObject(harta, newX, newY)) {
                if (hartaid == 0) { 
                    if (star >= 6) {
                        walls.remove(harta)
                        harta.visibility = View.GONE
                        hartaid++ // Tambahkan hartaid agar player tidak bisa mengambil lagi
                        star += 3
                        bintang.text = star.toString()
                        isMoving = false
                        handler.removeCallbacks(moveRunnable)
                        hasShownToast = false // Reset agar toast bisa muncul lagi jika perlu
                    } else {
                        if (!hasShownToast) {  // Pastikan toast hanya muncul sekali
                            Toast.makeText(this, "Bintang anda belum cukup untuk membuka kotak harta karun", Toast.LENGTH_LONG).show()
                            hasShownToast = true // Set agar tidak muncul lagi
                        }
                    }
                } else {
                    Toast.makeText(this, "Anda sudah mengambil harta karun", Toast.LENGTH_LONG).show()
                }
                return
        }


        val goa = findViewById<ImageView>(R.id.goa)
        if (willCollideWithObject(goa, newX, newY)) {
            isMoving = false
            handler.removeCallbacks(moveRunnable)
            val intent = Intent(this, GameGelud::class.java)
            intent.putExtra("monster", monster)
            intent.putExtra("completedHadiah", completedHadiah.joinToString(","))
            intent.putExtra("star", star)
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

    private fun handleHadiahCollision(newX: Float, newY: Float, hadiahId: Int, gameClass: Class<*>, star: Int) {
        isMoving = false
        handler.removeCallbacks(moveRunnable)
        saveCompletedHadiah()
        lastPlayerX = newX
        lastPlayerY = newY
        lastRobotX = robot.translationX
        lastRobotY = robot.translationY

        val intent = Intent(this, gameClass)
        intent.putExtra("lastRobotX", lastRobotX)
        intent.putExtra("lastRobotY", lastRobotY)
        intent.putExtra("lastX", lastPlayerX)
        intent.putExtra("lastY", lastPlayerY)
        intent.putExtra("monster", monster)
        intent.putExtra("star", star)
        intent.putExtra("game_mode", game_mode)
        intent.putExtra("currentHadiah", hadiahId)
        intent.putExtra("completedHadiah", completedHadiah.joinToString(","))
        startActivity(intent)
        completedHadiah.add(hadiahId)
        saveGameProgress()

    }
    private fun loadGameProgress() {
        database.child("gameProgress").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val completedString = snapshot.child("completedHadiah").getValue(String::class.java) ?: ""
                if (completedString.isNotEmpty()) {
                    completedHadiah.clear()
                    completedHadiah.addAll(completedString.split(",").map { it.toInt() })
                    Log.d(TAG, "CompletedHadiah loaded: $completedHadiah")
                }
                updateHadiahVisibility()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Gagal load progress: ${error.message}")
            }
        })
    }
    private fun updateHadiahVisibility() {
        val hadiahViews = listOf(
            findViewById<ImageView>(R.id.hadiah),
            findViewById<ImageView>(R.id.hadiah2),
            findViewById<ImageView>(R.id.hadiah3),
            findViewById<ImageView>(R.id.hadiah4),
            findViewById<ImageView>(R.id.hadiah5),
            findViewById<ImageView>(R.id.hadiah6)
        )
        val monsterThresholds = listOf(4, 3, 5, 2, 2, 2)

        hadiahViews.forEachIndexed { index, hadiahView ->
            val idHadiah = index + 1
            hadiahView.visibility = if (monster < monsterThresholds[index] || idHadiah in completedHadiah) {
                View.GONE
            } else {
                View.VISIBLE
            }
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

    private fun moveRobot() {
        // Gunakan posisi player sebagai target dasar
        val playerX = player.translationX
        val playerY = player.translationY

        // Hitung arah pergerakan berdasarkan posisi terakhir pemain
        val offset = 50f // Jarak mendahului
        val targetX = when (currentDirection) {
            "right" -> playerX + offset
            "left" -> playerX - offset
            else -> playerX
        }

        val targetY = when (currentDirection) {
            "forward" -> playerY - offset
            "backward" -> playerY + offset
            else -> playerY
        }

        val currentX = robot.translationX
        val currentY = robot.translationY

        // Hitung jarak ke target
        val distanceX = targetX - currentX
        val distanceY = targetY - currentY

        // Hitung jarak total
        val totalDistance = Math.sqrt((distanceX * distanceX + distanceY * distanceY).toDouble())

        // Jika robot sudah cukup dekat ke target, tidak perlu bergerak lebih jauh
        if (totalDistance <= robotStepSize * 2) return

        // Normalisasi vektor arah
        val dirX = distanceX / totalDistance.toFloat()
        val dirY = distanceY / totalDistance.toFloat()

        // Hitung posisi baru dengan percepatan (robot lebih cepat)
        val newX = currentX + dirX * (robotStepSize * 1.5f)
        val newY = currentY + dirY * (robotStepSize * 1.5f)

        // Periksa tabrakan dengan dinding sebelum bergerak
        if (!willCollideWithAnyWall(newX, newY)) {
            robot.translationX = newX
            robot.translationY = newY
        } else {
            // Jika akan menabrak dinding, coba bergerak hanya pada satu sumbu
            if (!willCollideWithAnyWall(newX, currentY)) {
                robot.translationX = newX
            } else if (!willCollideWithAnyWall(currentX, newY)) {
                robot.translationY = newY
            }
        }
    }


    private fun checkPlayerRobotCollision() {
        val robotBounds = Rect()
        robot.getGlobalVisibleRect(robotBounds)

        val playerBounds = Rect()
        player.getGlobalVisibleRect(playerBounds)
    }
}