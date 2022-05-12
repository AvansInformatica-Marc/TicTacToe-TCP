package nl.marc.tictactoe.ui.model

fun interface OnApplicationClosed {
    suspend operator fun invoke()
}
