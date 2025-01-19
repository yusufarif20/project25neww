package com.example.project25

import Question
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameDadu : AppCompatActivity() {
    // Previous properties remain the same
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
                R.drawable.dadutiga,
                R.drawable.dadusatu,
                R.drawable.dadudua
            ),
            correctAnswerIndex = 0
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
            correctAnswerIndex = 1
        )
    )
    private var currentQuestionIndex = 0
    private var currentScore: Int = 0
    private var True: Int = 0
    private var False: Int = 0
    private var selectedAnswerIndex: Int? = null  // Add this to track selected answer
    private lateinit var questionBackground: ImageView
    private lateinit var answerImageViews: List<ImageView>
    private lateinit var framesWithBorders: List<Pair<FrameLayout, ImageView>>
    private lateinit var submitButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var nextButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Previous onCreate implementation remains the same until nextButton click listener
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_dadu)

        questionBackground = findViewById(R.id.splashBackground)
        submitButton = findViewById(R.id.submit)
        backButton = findViewById(R.id.back)
        nextButton = findViewById(R.id.next)

        submitButton.visibility = View.GONE

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

        nextButton.setOnClickListener {
            // Check answer and update score before moving to next question
            checkAnswerAndUpdateScore()

            // Reset selected answer
            selectedAnswerIndex = null

            // Reset borders and move to next question
            resetAllBorders()
            currentQuestionIndex = (currentQuestionIndex + 1) % questions.size

            if (currentQuestionIndex == questions.size - 1) {
                nextButton.visibility = View.GONE
                submitButton.visibility = View.VISIBLE
            }

            loadQuestion(currentQuestionIndex)
            updateBackButtonVisibility()
        }

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

            val intent = Intent(this, HasilQuiz::class.java)
            intent.putExtra("currentScore", currentScore)
            intent.putExtra("True", True)
            intent.putExtra("False", False)
            startActivity(intent)
            finish()
        }

        loadQuestion(currentQuestionIndex)
    }

    private fun checkAnswerAndUpdateScore() {
        selectedAnswerIndex?.let { selected ->
            val isCorrect = selected == questions[currentQuestionIndex].correctAnswerIndex
            if (isCorrect) {
                True += 1
                currentScore += 20
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

    private fun loadQuestion(index: Int) {
        val question = questions[index]
        questionBackground.setImageResource(question.backgroundImage)

        answerImageViews.forEachIndexed { i, imageView ->
            imageView.setImageResource(question.answers[i])
        }

        framesWithBorders.forEachIndexed { i, pair ->
            val frame = pair.first
            val border = pair.second

            border.visibility = View.GONE
            frame.setBackgroundResource(0)

            frame.setOnClickListener {
                // Reset all borders first
                resetAllBorders()

                // Show border for selected answer
                border.visibility = View.VISIBLE

                // Update selected answer index
                selectedAnswerIndex = i
            }
        }
    }
}