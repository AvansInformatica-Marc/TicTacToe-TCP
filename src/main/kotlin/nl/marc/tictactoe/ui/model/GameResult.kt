package nl.marc.tictactoe.ui.model

import nl.marc.tictactoe.domain.TicTacToeGame

sealed interface GameResult {
    data class GameEnded(val winner: TicTacToeGame.Player?) : GameResult
    
    object GameQuit : GameResult
}
