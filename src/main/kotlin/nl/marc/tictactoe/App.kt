package nl.marc.tictactoe

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import nl.marc.tictactoe.domain.TicTacToeGame
import nl.marc.tictactoe.ui.*
import nl.marc.tictactoe.utils.TcpSocket

sealed class AppState {
    object UNINITIALISED : AppState()

    object DeviceIsPrimary : AppState()

    object DeviceIsSecondary : AppState()

    data class GameRunning(val player: TicTacToeGame.Player, val connection: TcpSocket, val hasInitialTurn: Boolean) : AppState()

    data class GameEnded(val player: TicTacToeGame.Player, val winner: TicTacToeGame.Player?, val connection: TcpSocket, val hasInitialTurn: Boolean) : AppState()
}

@Composable
fun App(setOnCloseListener: (suspend () -> Unit) -> Unit) {
    MaterialTheme {
        var state by remember { mutableStateOf<AppState>(AppState.UNINITIALISED) }

        when(val currentState = state) {
            is AppState.UNINITIALISED -> DeviceChoice {
                state = if (it.isRemoteDevice) AppState.DeviceIsSecondary else AppState.DeviceIsPrimary
            }
            is AppState.DeviceIsPrimary -> AcceptConnection {
                state = AppState.GameRunning(TicTacToeGame.Player1, it, true)
                setOnCloseListener {
                    it.closeSuspending()
                }
            }
            is AppState.DeviceIsSecondary -> ConnectToRemoteDevice {
                state = AppState.GameRunning(TicTacToeGame.Player2, it, false)
            }
            is AppState.GameRunning -> Game(currentState.hasInitialTurn, currentState.player, currentState.connection) {
                state = AppState.GameEnded(currentState.player, it, currentState.connection, currentState.hasInitialTurn)
            }
            is AppState.GameEnded -> GameEnded(currentState.player, currentState.winner) {
                state = AppState.GameRunning(currentState.player, currentState.connection, !currentState.hasInitialTurn)
            }
        }
    }
}

@Composable
@Preview
private fun AppPreview() {
    App { }
}
