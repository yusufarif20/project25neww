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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

data class Uang(val id: Int, val value: Int, val drawableRes: Int)

class GameUang : AppCompatActivity(), View.OnTouchListener {
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
    private val targetValue = 5000
    private var lastPlayerX = 0f
    private var lastPlayerY = 0f
    private var lastRobotX = 0f
    private var lastRobotY = 0f

    // Tambahan untuk sistem hadiah
    private var currentHadiah = 0
    private var completedHadiah = mutableSetOf<Int>()

    // List objek yang akan ditampilkan secara dinamis
    private val allUangList = listOf(
        Uang(R.id.duaribu, 2000, R.drawable.duaribu),
        Uang(R.id.duaribu2, 2000, R.drawable.duaribu),
        Uang(R.id.limaribu, 5000, R.drawable.limaribu),
        Uang(R.id.limaratus, 500, R.drawable.limaratus),
        Uang(R.id.limaratus2, 500, R.drawable.limaratus),
        Uang(R.id.seribu, 1000, R.drawable.seribu),
        Uang(R.id.seribukoin, 1000, R.drawable.seribukoin),
        Uang(R.id.duaratus, 200, R.drawable.duaratus),
        Uang(R.id.sepuluhribu, 10000, R.drawable.sepuluhribu),
        Uang(R.id.duapuluhribu, 20000, R.drawable.duapuluhribu),
        Uang(R.id.limapuluhribu, 50000, R.drawable.limapuluhribu)
    )

    private val displayedUangViews = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_uang)

        // Inisialisasi views tetap
        textView = findViewById(R.id.TextView)
        keranjang = findViewById(R.id.keranjang)
        submitButton = findViewById(R.id.submit)

        // Ambil data dari intent
        initializeIntentData()

        // Setup objek dinamis
        setupDynamicObjects()

        // Initialize counter
        textView.text = "0"

        // Setup submit button
        setupSubmitButton()
    }

    private fun initializeIntentData() {
        lastPlayerX = intent.getFloatExtra("lastX", 0f)
        lastPlayerY = intent.getFloatExtra("lastY", 0f)
        lastRobotX = intent.getFloatExtra("lastRobotX", 0f)
        lastRobotY = intent.getFloatExtra("lastRobotY", 0f)

        currentHadiah = intent.getIntExtra("currentHadiah", 0)
        val completedHadiahString = intent.getStringExtra("completedHadiah") ?: ""
        completedHadiah = if (completedHadiahString.isEmpty()) {
            mutableSetOf()
        } else {
            completedHadiahString.split(",").map { it.toInt() }.toMutableSet()
        }
    }

    private fun setupDynamicObjects() {
        val parentLayout = findViewById<ConstraintLayout>(R.id.main)

        // Pilih 6-8 objek secara acak dari daftar lengkap
        val numberOfObjects = Random.nextInt(6, 9) // 6-8 objek
        val selectedUang = allUangList.shuffled().take(numberOfObjects)

        // Hapus semua ImageView yang ada sebelumnya (kecuali keranjang dan submit)
        clearExistingDynamicViews()

        selectedUang.forEachIndexed { index, uang ->
            createDynamicImageView(uang, index, parentLayout)
        }
    }

    private fun clearExistingDynamicViews() {
        displayedUangViews.forEach { imageView ->
            val parent = imageView.parent as? ConstraintLayout
            parent?.removeView(imageView)
        }
        displayedUangViews.clear()
        viewValues.clear()
        droppedViews.clear()
        counter = 0
    }

    private fun createDynamicImageView(uang: Uang, index: Int, parentLayout: ConstraintLayout) {
        val imageView = ImageView(this).apply {
            id = View.generateViewId() // Generate unique ID
            setImageResource(uang.drawableRes)
            scaleType = ImageView.ScaleType.FIT_CENTER

            // Set ukuran berdasarkan jenis uang
            val isKoin = uang.value in listOf(200, 500, 1000) // Koin: 200, 500, 1000
            layoutParams = if (isKoin) {
                ConstraintLayout.LayoutParams(
                    94.dpToPx(), // width koin
                    49.dpToPx()  // height koin
                )
            } else {
                ConstraintLayout.LayoutParams(
                    107.dpToPx(), // width kertas
                    57.dpToPx()   // height kertas
                )
            }

            setOnTouchListener(this@GameUang)
        }

        // Tambahkan ke parent
        parentLayout.addView(imageView)

        // Set posisi menggunakan constraint
        setConstraintPosition(imageView, parentLayout, index)

        // Simpan referensi
        displayedUangViews.add(imageView)
        viewValues[imageView.id] = uang.value
    }

    private fun setConstraintPosition(imageView: ImageView, parentLayout: ConstraintLayout, index: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(parentLayout)

        // Daftar posisi tetap berdasarkan bias constraint
        val positions = listOf(
            Pair(0.386f, 0.319f), // Posisi 1
            Pair(0.195f, 0.362f),  // Posisi 2
            Pair(0.192f, 0.286f), // Posisi 3
            Pair(0.265f, 0.295f),   // Posisi 4
            Pair(0.191f, 0.469f),    // Posisi 5
            Pair(0.351f, 0.498f),    // Posisi 6
            Pair(0.275f, 0.337f),    // Posisi 7
            Pair(0.235f, 0.275f)    // Posisi 8
        )

        val position = positions.getOrElse(index) { positions[index % positions.size] }

        constraintSet.connect(imageView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(imageView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(imageView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(imageView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

        constraintSet.setHorizontalBias(imageView.id, position.first)
        constraintSet.setVerticalBias(imageView.id, position.second)

        constraintSet.applyTo(parentLayout)
    }

    private fun setupSubmitButton() {
        submitButton.setOnClickListener {
            if (counter == targetValue) {
                // Tambahkan hadiah yang baru diselesaikan
                completedHadiah.add(currentHadiah)

                // Buat intent untuk kembali ke rute
                val intent = Intent(this, rute::class.java)

                var star = intent.getIntExtra("star", 0)
                var monster = intent.getIntExtra("monster", 0)
                var game_mode = intent.getIntExtra("game_mode", 0)

                star++

                addStarsValue()

                // Masukkan semua data yang diperlukan
                intent.putExtra("lastX", lastPlayerX)
                intent.putExtra("lastY", lastPlayerY)
                intent.putExtra("lastRobotX", lastRobotX)
                intent.putExtra("lastRobotY", lastRobotY)
                intent.putExtra("monster", monster)
                intent.putExtra("star", star)
                intent.putExtra("game_mode", game_mode)
                intent.putExtra("completedHadiah", completedHadiah.joinToString(","))

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Total harus tepat Rp. 5000",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Fungsi untuk restart game dengan objek baru
    private fun reshuffleObjects() {
        counter = 0
        textView.text = "0"
        droppedViews.clear()
        setupDynamicObjects()
    }

    // Extension function untuk konversi dp ke pixel
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
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