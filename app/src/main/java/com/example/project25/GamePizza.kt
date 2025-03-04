package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Rect
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GamePizza : AppCompatActivity(), View.OnTouchListener {
    private var dX = 0f
    private var dY = 0f
    private var counter = 0
    private lateinit var textView: TextView
    private lateinit var keranjang: ImageView
    private lateinit var submitButton: ImageView
    private var droppedViews = mutableSetOf<ImageView>()
    private var viewValues = mutableMapOf<Int, Int>()
    private val maxObjects = 4
    private var isDraggable = true
    private val targetValue = 4
    private var lastPlayerX = 0f
    private var lastPlayerY = 0f
    private var lastRobotX = 0f
    private var lastRobotY = 0f

    // Tambahan untuk sistem hadiah
    private var currentHadiah = 0
    private var completedHadiah = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_pizza)

        // Inisialisasi view
        textView = findViewById(R.id.TextView)
        val pizza1 = findViewById<ImageView>(R.id.pizza1)
        val pizza2 = findViewById<ImageView>(R.id.pizza2)
        val pizza3 = findViewById<ImageView>(R.id.pizza3)
        val pizza4 = findViewById<ImageView>(R.id.pizza4)
        keranjang = findViewById(R.id.keranjang)
        submitButton = findViewById(R.id.submit)

        // Ambil data dari intent
        lastPlayerX = intent.getFloatExtra("lastX", 0f)
        lastPlayerY = intent.getFloatExtra("lastY", 0f)
        lastRobotX = intent.getFloatExtra("lastRobotX", 0f)
        lastRobotY = intent.getFloatExtra("lastRobotY", 0f)
        var monster = intent.getIntExtra("monster", 0)
        var star = intent.getIntExtra("star", 0)

        // Ambil data hadiah
        currentHadiah = intent.getIntExtra("currentHadiah", 0)
        val completedHadiahString = intent.getStringExtra("completedHadiah") ?: ""
        completedHadiah = if (completedHadiahString.isEmpty()) {
            mutableSetOf()
        } else {
            completedHadiahString.split(",").map { it.toInt() }.toMutableSet()
        }

        // Set touch listeners
        pizza1.setOnTouchListener(this)
        pizza2.setOnTouchListener(this)
        pizza3.setOnTouchListener(this)
        pizza4.setOnTouchListener(this)

        // Initialize values for each view
        viewValues[R.id.pizza1] = 1
        viewValues[R.id.pizza2] = 1
        viewValues[R.id.pizza3] = 1
        viewValues[R.id.pizza4] = 1

        // Initialize counter
        textView.text = "0"

        submitButton.setOnClickListener {
            if (counter == targetValue) {
                // Tambahkan hadiah yang baru diselesaikan
                completedHadiah.add(currentHadiah)

                star++

                addStarsValue()

                // Buat intent untuk kembali ke rute
                val intent = Intent(this, rute::class.java)
                monster -= 1

                intent.putExtra("lastX", lastPlayerX)
                intent.putExtra("lastY", lastPlayerY)
                intent.putExtra("lastRobotX", lastRobotX)
                intent.putExtra("lastRobotY", lastRobotY)
                intent.putExtra("monster", monster)
                intent.putExtra("star", star)
                intent.putExtra("completedHadiah", completedHadiah.joinToString(","))

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Pecahan Belum Benar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addStarsValue() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            val uid = user.uid
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference

            val userRef = database.child("users").child(uid).child("stars")

            userRef.get().addOnSuccessListener { snapshot ->
                val currentStars = snapshot.getValue(Int::class.java) ?: 0
                userRef.setValue(currentStars + 1)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Berhasil Klaim poin", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        println("Gagal menambahkan stars: ${e.message}")
                    }
            }.addOnFailureListener { e ->
                println("Gagal mengambil data stars: ${e.message}")
            }
        } else {
            println("User belum login")
        }

    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val imageView = view as ImageView

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (droppedViews.size >= maxObjects && !droppedViews.contains(imageView)) {
                    Toast.makeText(
                        this,
                        "Keranjang sudah penuh! Kurangi objek terlebih dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                dX = view.x - event.rawX
                dY = view.y - event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (droppedViews.contains(imageView) || droppedViews.size < maxObjects) {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
            }
            MotionEvent.ACTION_UP -> {
                val value = viewValues[view.id] ?: 0

                if (isViewOverlapping(imageView, keranjang)) {
                    if (!droppedViews.contains(imageView) && droppedViews.size < maxObjects) {
                        counter += value
                        textView.text = counter.toString()
                        droppedViews.add(imageView)
                    }
                } else if (!isViewOverlapping(imageView, keranjang) && droppedViews.contains(imageView)) {
                    counter -= value
                    textView.text = counter.toString()
                    droppedViews.remove(imageView)
                }
            }
        }
        return true
    }

    private fun isViewOverlapping(view1: ImageView, view2: ImageView): Boolean {
        val rect1 = Rect()
        val rect2 = Rect()

        view1.getGlobalVisibleRect(rect1)
        view2.getGlobalVisibleRect(rect2)

        return Rect.intersects(rect1, rect2)
    }
}