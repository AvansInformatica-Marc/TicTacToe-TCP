package nl.marc.tictactoe

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.marc.tictactoe.domain.TicTacToeGame
import nl.marc.tictactoe.ui.*

sealed class AppState {
    object UNINITIALISED : AppState()

    object DeviceIsPrimary : AppState()

    object DeviceIsSecondary : AppState()

    data class GameRunning(
        val player: TicTacToeGame.Player,
        val readChannel: ByteReadChannel,
        val writeChannel: ByteWriteChannel,
        val hasInitialTurn: Boolean
    ) : AppState()

    data class GameEnded(
        val player: TicTacToeGame.Player,
        val winner: TicTacToeGame.Player?,
        val readChannel: ByteReadChannel,
        val writeChannel: ByteWriteChannel,
        val hasInitialTurn: Boolean
    ) : AppState()
}

@Composable
fun App(setOnCloseListener: (suspend () -> Unit) -> Unit) {
    var state by remember { mutableStateOf<AppState>(AppState.UNINITIALISED) }
    val selectorManager = remember { ActorSelectorManager(Dispatchers.IO) }
    val socketBuilder = remember { aSocket(selectorManager).tcp() }


    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
    ) {
        Surface (
            color = MaterialTheme.colors.background
        ) {
            when(val currentState = state) {
                is AppState.UNINITIALISED -> DeviceChoice {
                    state = if (it.isRemoteDevice) AppState.DeviceIsSecondary else AppState.DeviceIsPrimary
                }
                is AppState.DeviceIsPrimary -> AcceptConnection(socketBuilder) { serverSocket, socket ->
                    val readChannel = socket.openReadChannel()
                    val writeChannel = socket.openWriteChannel(autoFlush = true)
                    state = AppState.GameRunning(TicTacToeGame.Player1, readChannel, writeChannel, true)
                    setOnCloseListener {
                        withContext(Dispatchers.IO) {
                            socket.close()
                            serverSocket.close()
                            selectorManager.close()
                        }
                    }
                }
                is AppState.DeviceIsSecondary -> ConnectToRemoteDevice(socketBuilder) { socket ->
                    val readChannel = socket.openReadChannel()
                    val writeChannel = socket.openWriteChannel(autoFlush = true)
                    state = AppState.GameRunning(TicTacToeGame.Player2, readChannel, writeChannel, false)
                    setOnCloseListener {
                        withContext(Dispatchers.IO) {
                            socket.close()
                            selectorManager.close()
                        }
                    }
                }
                is AppState.GameRunning -> Game(currentState.hasInitialTurn, currentState.player, currentState.readChannel, currentState.writeChannel) {
                    state = AppState.GameEnded(currentState.player, it, currentState.readChannel, currentState.writeChannel, currentState.hasInitialTurn)
                }
                is AppState.GameEnded -> GameEnded(currentState.player, currentState.winner) {
                    state = AppState.GameRunning(currentState.player, currentState.readChannel, currentState.writeChannel, !currentState.hasInitialTurn)
                }
            }
        }
    }
}

@Composable
@Preview
private fun AppPreview() {
    App { }
}
