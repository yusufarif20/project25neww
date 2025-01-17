package com.example.project25

import Question
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameDadu : AppCompatActivity() {

    private val questions = listOf(
        Question(
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
            backgroundImage = R.drawable.layoutdadutiga,
            answers = listOf(
                R.drawable.dadulima,
                R.drawable.dadusatu,
                R.drawable.dadudua,
                R.drawable.daduempat
            ),
            correctAnswerIndex = 1
        ),
        Question(
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
            backgroundImage = R.drawable.layoutdadulima,
            answers = listOf(
                R.drawable.dadutiga,
                R.drawable.dadudua,
                R.drawable.daduenam,
                R.drawable.daduempat
            ),
            correctAnswerIndex = 0
        )
    )
    private var currentQuestionIndex = 0
    private var currentScore: Int = 0
    private lateinit var questionBackground: ImageView
    private lateinit var answerImageViews: List<ImageView>
    private lateinit var framesWithBorders: List<Pair<FrameLayout, ImageView>>
    private lateinit var backButton: ImageView
    private lateinit var nextButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_dadu)

        // Inisialisasi background
        questionBackground = findViewById(R.id.splashBackground)

        // Inisialisasi tombol
        backButton = findViewById(R.id.back)
        nextButton = findViewById(R.id.next)

        // Inisialisasi ImageView untuk jawaban
        answerImageViews = listOf(
            findViewById(R.id.dadu1),
            findViewById(R.id.dadu2),
            findViewById(R.id.dadu3),
            findViewById(R.id.dadu4)
        )

        // Inisialisasi framesWithBorders
        framesWithBorders = listOf(
            Pair(findViewById(R.id.frame_dadu1), findViewById(R.id.border_dadu1)),
            Pair(findViewById(R.id.frame_dadu2), findViewById(R.id.border_dadu2)),
            Pair(findViewById(R.id.frame_dadu3), findViewById(R.id.border_dadu3)),
            Pair(findViewById(R.id.frame_dadu4), findViewById(R.id.border_dadu4))
        )

        // Set initial back button visibility
        updateBackButtonVisibility()

        nextButton.setOnClickListener {
            // Reset semua border saat pindah ke pertanyaan berikutnya
            resetAllBorders()

            currentQuestionIndex = (currentQuestionIndex + 1) % questions.size
            loadQuestion(currentQuestionIndex)
            updateBackButtonVisibility()
        }

        backButton.setOnClickListener {
            if (currentQuestionIndex > 0) {
                resetAllBorders()
                currentQuestionIndex--
                loadQuestion(currentQuestionIndex)
                updateBackButtonVisibility()
            }
        }

        loadQuestion(currentQuestionIndex)
    }

    private fun updateBackButtonVisibility() {
        // Tampilkan tombol back hanya jika berada di pertanyaan ke-2 atau lebih
        backButton.visibility = if (currentQuestionIndex > 0) View.VISIBLE else View.GONE
    }

    private fun resetAllBorders() {
        framesWithBorders.forEach { (frame, border) ->
            border.visibility = View.GONE
            frame.setBackgroundResource(0)
        }
    }

    private fun loadQuestion(index: Int) {
        val question = questions[index]

        // Ganti gambar latar belakang
        questionBackground.setImageResource(question.backgroundImage)

        // Update gambar pilihan jawaban
        answerImageViews.forEachIndexed { i, imageView ->
            imageView.setImageResource(question.answers[i])
        }

        // Atur gambar pilihan jawaban dan logika klik
        framesWithBorders.forEachIndexed { i, pair ->
            val frame = pair.first
            val border = pair.second

            // Reset border
            border.visibility = View.GONE
            frame.setBackgroundResource(0)

            // Atur klik untuk pilihan jawaban
            frame.setOnClickListener {
                val isCorrect = i == question.correctAnswerIndex

                // Reset semua border terlebih dahulu
                resetAllBorders()

                // Tampilkan border untuk pilihan yang dipilih
                border.visibility = View.VISIBLE

                // Tambahkan poin jika benar
                if (isCorrect) {
                    currentScore += 10
                }
            }
        }
    }
}
