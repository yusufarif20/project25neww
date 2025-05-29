data class Question(
    val id: Int, // Tambahan ID unik
    val backgroundImage: Int,
    val answers: List<Int>,
    var correctAnswerIndex: Int
)
