data class Question(
    val backgroundImage: Int, // Gambar latar belakang soal
    val answers: List<Int>,   // List gambar sebagai jawaban
    val correctAnswerIndex: Int // Index jawaban yang benar
)
