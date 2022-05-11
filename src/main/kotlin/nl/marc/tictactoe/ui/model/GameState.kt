package nl.marc.tictactoe.ui.model

import nl.marc.tictactoe.domain.TicTacToeGame

sealed interface GameState {
    object Running : GameState

    data class Completed(val winner: TicTacToeGame.Player?) : GameState
}
