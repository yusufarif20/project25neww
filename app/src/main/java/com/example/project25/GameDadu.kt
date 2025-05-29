package com.example.project25

import Question
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GameDadu : AppCompatActivity() {
    // Previous properties remain the same
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var currentScore: Int = 0
    private var True: Int = 0
    private var False: Int = 0
    private var star: Int = 0
    private var selectedAnswerIndex: Int? = null
    private var lastPlayerX = 0f
    private var lastPlayerY = 0f
    private var lastRobotX = 0f
    private var lastRobotY = 0f

    // Tambahan untuk sistem hadiah
    private var currentHadiah = 0
    private var completedHadiah = mutableSetOf<Int>()

    private lateinit var auth: FirebaseAuth
    private lateinit var questionBackground: ImageView
    private lateinit var answerImageViews: List<ImageView>
    private lateinit var framesWithBorders: List<Pair<FrameLayout, ImageView>>
    private lateinit var submitButton: ImageView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_dadu)

        fetchCompletedQuestionsAndStartGame()

        // Initialize views
        questionBackground = findViewById(R.id.splashBackground)
        submitButton = findViewById(R.id.submit)
        backButton = findViewById(R.id.back)

        // Get data from intent
        lastPlayerX = intent.getFloatExtra("lastX", 0f)
        lastPlayerY = intent.getFloatExtra("lastY", 0f)
        lastRobotX = intent.getFloatExtra("lastRobotX", 0f)
        lastRobotY = intent.getFloatExtra("lastRobotY", 0f)
        var monster = intent.getIntExtra("monster", 0)
        var game_mode = intent.getIntExtra("game_mode", 0)
        star = intent.getIntExtra("star", 0)
        Log.d("GameDadu", "Nilai star yang diterima: $star")

        // Ambil data hadiah
        currentHadiah = intent.getIntExtra("currentHadiah", 0)
        val completedHadiahString = intent.getStringExtra("completedHadiah") ?: ""
        completedHadiah = if (completedHadiahString.isEmpty()) {
            mutableSetOf()
        } else {
            completedHadiahString.split(",").map { it.toInt() }.toMutableSet()
        }

        answerImageViews = listOf(
            findViewById(R.id.dadu1),
            findViewById(R.id.dadu2),
            findViewById(R.id.dadu3),
            findViewById(R.id.dadu4)
        )

        framesWithBorders = listOf(
            Pair(findViewById(R.id.frame_dadu1), findViewById(R.id.border_dadu1)),
            Pair(findViewById(R.id.frame_dadu2), findViewById(R.id.border_dadu2)),
            Pair(findViewById(R.id.frame_dadu3), findViewById(R.id.border_dadu3)),
            Pair(findViewById(R.id.frame_dadu4), findViewById(R.id.border_dadu4))
        )

        updateBackButtonVisibility()

        backButton.setOnClickListener {
            if (currentQuestionIndex > 0) {
                selectedAnswerIndex = null
                resetAllBorders()
                currentQuestionIndex--
                loadQuestion(currentQuestionIndex)
                updateBackButtonVisibility()
            }
        }

        submitButton.setOnClickListener {
            // Check final answer before submitting
            checkAnswerAndUpdateScore()

            // Tambahkan hadiah yang baru diselesaikan
            completedHadiah.add(currentHadiah)

            // Create intent for returning to route
            val intent = Intent(this, rute::class.java)
            // Add all necessary data to intent
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

        }
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val dbRef = FirebaseDatabase.getInstance().reference
            dbRef.child("users").child(uid).child("completedQuestions").get()
                .addOnSuccessListener { snapshot ->
                    val completedIndexes = snapshot.children.mapNotNull { it.key?.toIntOrNull() }.toSet()
                    questions = questions.filterIndexed { i, _ -> i !in completedIndexes }.shuffled()
                    loadQuestion(currentQuestionIndex)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memuat progress user", Toast.LENGTH_SHORT).show()
                    loadQuestion(currentQuestionIndex)
                }
        }

    }

    private fun fetchCompletedQuestionsAndStartGame() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(it.uid)

            userRef.get().addOnSuccessListener { snapshot ->
                val completedMap = snapshot.child("completedQuestions").value as? Map<String, Boolean>
                val completedIndexes = completedMap?.mapNotNull {
                    if (it.value == true) it.key.toIntOrNull() else null
                } ?: emptyList()

                // Filter hanya soal yang belum dijawab
                val allQuestions = listOf(
                    Question(
                        id = 1,
                        backgroundImage = R.drawable.layoutdadu,
                        answers = listOf(
                            R.drawable.dadutiga,
                            R.drawable.dadusatu,
                            R.drawable.daduempat,
                            R.drawable.daduenam
                        ),
                        correctAnswerIndex = 2
                    ),
                    Question(
                        id = 2,
                        backgroundImage = R.drawable.layoutdadudua,
                        answers = listOf(
                            R.drawable.dadulima,
                            R.drawable.dadusatu,
                            R.drawable.dadudua,
                            R.drawable.daduempat
                        ),
                        correctAnswerIndex = 1
                    ),
                    Question(
                        id = 3,
                        backgroundImage = R.drawable.layoutdadutiga,
                        answers = listOf(
                            R.drawable.dadulima,
                            R.drawable.dadutiga,
                            R.drawable.dadusatu,
                            R.drawable.dadudua
                        ),
                        correctAnswerIndex = 0
                    ),
                    Question(
                        id = 4,
                        backgroundImage = R.drawable.layoutdaduempat,
                        answers = listOf(
                            R.drawable.dadutiga,
                            R.drawable.daduempat,
                            R.drawable.dadudua,
                            R.drawable.dadusatu
                        ),
                        correctAnswerIndex = 0
                    ),
                    Question(
                        id = 5,
                        backgroundImage = R.drawable.layoutdadulima,
                        answers = listOf(
                            R.drawable.dadutiga,
                            R.drawable.dadudua,
                            R.drawable.daduenam,
                            R.drawable.daduempat
                        ),
                        correctAnswerIndex = 1
                    )
                )
                questions = allQuestions.filter { it.id !in completedIndexes }.shuffled()
                if (questions.isNotEmpty()) {
                    currentQuestionIndex = 0
                    loadQuestion(currentQuestionIndex)
                } else {
                    Toast.makeText(this, "Semua soal sudah dikerjakan!", Toast.LENGTH_LONG).show()
                    finish()
                }
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

    private fun checkAnswerAndUpdateScore() {
        star = intent.getIntExtra("star", 0)
        selectedAnswerIndex?.let { selected ->
            val isCorrect = selected == questions[currentQuestionIndex].correctAnswerIndex
            if (isCorrect) {
                star++
                True += 1
                currentScore += 20
                markQuestionAsCompleted(questions[currentQuestionIndex].id)
                addStarsValue()
            } else {
                False += 1
            }
        }
    }

    private fun updateBackButtonVisibility() {
        backButton.visibility = if (currentQuestionIndex > 0) View.VISIBLE else View.GONE
    }

    private fun resetAllBorders() {
        framesWithBorders.forEach { (frame, border) ->
            border.visibility = View.GONE
            frame.setBackgroundResource(0)
        }
    }

    private fun markQuestionAsCompleted(questionId: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val database = FirebaseDatabase.getInstance().reference
            database.child("users").child(uid).child("completedQuestions").child(questionId.toString())
                .setValue(true)
        }
    }

    private fun loadQuestion(index: Int) {
        val question = questions[index]
        questionBackground.setImageResource(question.backgroundImage)

        val shuffledAnswers = question.answers.mapIndexed { i, resId ->
            Pair(i, resId)
        }.shuffled()

        val newCorrectIndex = shuffledAnswers.indexOfFirst { it.first == question.correctAnswerIndex }
        question.correctAnswerIndex = newCorrectIndex

        answerImageViews.forEachIndexed { i, imageView ->
            imageView.setImageResource(shuffledAnswers[i].second)
        }

        framesWithBorders.forEachIndexed { i, pair ->
            val frame = pair.first
            val border = pair.second

            border.visibility = View.GONE
            frame.setBackgroundResource(0)

            frame.setOnClickListener {
                resetAllBorders()
                border.visibility = View.VISIBLE
                selectedAnswerIndex = i
            }
        }
    }

}