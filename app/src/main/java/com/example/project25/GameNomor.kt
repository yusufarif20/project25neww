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
import kotlin.random.Random

class GameNomor : AppCompatActivity(), View.OnTouchListener {
    private var dX = 0f
    private var dY = 0f
    private var counter = 0
    private lateinit var keranjang: ImageView
    private lateinit var submitButton: ImageView
    private lateinit var soal1: ImageView
    private lateinit var soal2: ImageView
    private var droppedViews = mutableSetOf<ImageView>()
    private var viewValues = mutableMapOf<Int, Int>()
    private val maxObjects = 1
    private var isDraggable = true

    // Variabel untuk soal acak
    private var targetValue = 3
    private var angka1 = 1
    private var angka2 = 2

    private var lastPlayerX = 0f
    private var lastPlayerY = 0f
    private var lastRobotX = 0f
    private var lastRobotY = 0f

    // Tambahan untuk sistem hadiah
    private var currentHadiah = 0
    private var completedHadiah = mutableSetOf<Int>()

    // Array drawable untuk angka
    private val numberDrawables = mapOf(
        1 to R.drawable.nosatu,
        2 to R.drawable.nodua,
        3 to R.drawable.notiga,
        4 to R.drawable.noempat,
        5 to R.drawable.nolima,
        6 to R.drawable.noenam,
        7 to R.drawable.notuju
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_nomor)

        val satu = findViewById<ImageView>(R.id.satu)
        val dua = findViewById<ImageView>(R.id.dua)
        val tiga = findViewById<ImageView>(R.id.tiga)
        val empat = findViewById<ImageView>(R.id.empat)
        val lima = findViewById<ImageView>(R.id.lima)
        val enam = findViewById<ImageView>(R.id.enam)
        val tuju = findViewById<ImageView>(R.id.tuju)
        keranjang = findViewById(R.id.kotak)
        submitButton = findViewById(R.id.submit)
        soal1 = findViewById(R.id.soal1)
        soal2 = findViewById(R.id.soal2)

        // Generate soal acak
        generateRandomQuestion()

        // Ambil data dari intent
        lastPlayerX = intent.getFloatExtra("lastX", 0f)
        lastPlayerY = intent.getFloatExtra("lastY", 0f)
        lastRobotX = intent.getFloatExtra("lastRobotX", 0f)
        lastRobotY = intent.getFloatExtra("lastRobotY", 0f)
        val monster = intent.getIntExtra("monster", 0)
        var star = intent.getIntExtra("star", 0)
        var game_mode = intent.getIntExtra("game_mode", 0)

        // Ambil data hadiah
        currentHadiah = intent.getIntExtra("currentHadiah", 0)
        val completedHadiahString = intent.getStringExtra("completedHadiah") ?: ""
        completedHadiah = if (completedHadiahString.isEmpty()) {
            mutableSetOf()
        } else {
            completedHadiahString.split(",").map { it.toInt() }.toMutableSet()
        }

        // Set touch listeners
        satu.setOnTouchListener(this)
        dua.setOnTouchListener(this)
        tiga.setOnTouchListener(this)
        empat.setOnTouchListener(this)
        lima.setOnTouchListener(this)
        enam.setOnTouchListener(this)
        tuju.setOnTouchListener(this)

        // Initialize values for each view
        viewValues[R.id.satu] = 1
        viewValues[R.id.dua] = 2
        viewValues[R.id.tiga] = 3
        viewValues[R.id.empat] = 4
        viewValues[R.id.lima] = 5
        viewValues[R.id.enam] = 6
        viewValues[R.id.tuju] = 7

        submitButton.setOnClickListener {
            if (counter == targetValue) {
                // Tambahkan hadiah yang baru diselesaikan
                completedHadiah.add(currentHadiah)

                star++

                addStarsValue()

                val intent = Intent(this, rute::class.java)

                // Masukkan semua data yang diperlukan
                intent.putExtra("monster", monster)
                intent.putExtra("lastX", lastPlayerX)
                intent.putExtra("lastY", lastPlayerY)
                intent.putExtra("lastRobotX", lastRobotX)
                intent.putExtra("lastRobotY", lastRobotY)
                intent.putExtra("star", star)
                intent.putExtra("game_mode", game_mode)
                intent.putExtra("completedHadiah", completedHadiah.joinToString(","))

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Jawaban salah! Hasil penjumlahan $angka1 + $angka2 = $targetValue",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun generateRandomQuestion() {
        // Generate dua angka acak antara 1-7
        angka1 = Random.nextInt(1, 8) // 1-7
        angka2 = Random.nextInt(1, 8) // 1-7

        // Pastikan hasil penjumlahan tidak melebihi 7 (karena hanya ada angka 1-7)
        while (angka1 + angka2 > 7) {
            angka1 = Random.nextInt(1, 8)
            angka2 = Random.nextInt(1, 8)
        }

        // Hitung target jawaban
        targetValue = angka1 + angka2

        // Set drawable untuk soal1 dan soal2
        numberDrawables[angka1]?.let { soal1.setImageResource(it) }
        numberDrawables[angka2]?.let { soal2.setImageResource(it) }

        // Log untuk debugging
        println("Soal: $angka1 + $angka2 = $targetValue")
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
                        "Kotak jawaban sudah penuh! Kurangi objek terlebih dahulu",
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
                        droppedViews.add(imageView)
                    }
                } else if (!isViewOverlapping(imageView, keranjang) && droppedViews.contains(imageView)) {
                    counter -= value
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