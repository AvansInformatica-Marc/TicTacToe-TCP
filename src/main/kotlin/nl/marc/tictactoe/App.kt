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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.marc.tictactoe.domain.TicTacToeGame
import nl.marc.tictactoe.ui.*

sealed class ConnectionState {
    object UNINITIALISED : ConnectionState()

    object DeviceIsPrimary : ConnectionState()

    object DeviceIsSecondary : ConnectionState()

    data class Connected(
        val socket: Socket,
        val serverSocket: ServerSocket? = null
    ) : ConnectionState()
}

@Composable
fun App(setOnCloseListener: (suspend () -> Unit) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var state by remember { mutableStateOf<ConnectionState>(ConnectionState.UNINITIALISED) }
    val selectorManager = remember { ActorSelectorManager(Dispatchers.IO) }
    val socketBuilder = remember { aSocket(selectorManager).tcp() }

    setOnCloseListener {
        withContext(Dispatchers.IO) {
            (state as? ConnectionState.Connected)?.apply {
                socket.close()
                serverSocket?.close()
            }
            selectorManager.close()
        }
    }

    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
    ) {
        Surface (
            color = MaterialTheme.colors.background
        ) {
            when(val currentState = state) {
                is ConnectionState.UNINITIALISED -> DeviceChoice {
                    state = if (it.isRemoteDevice) ConnectionState.DeviceIsSecondary else ConnectionState.DeviceIsPrimary
                }
                is ConnectionState.DeviceIsPrimary -> AcceptConnection(socketBuilder) { serverSocket, socket ->
                    state = ConnectionState.Connected(socket, serverSocket)
                }
                is ConnectionState.DeviceIsSecondary -> ConnectToRemoteDevice(socketBuilder) { socket ->
                    state = ConnectionState.Connected(socket)
                }
                is ConnectionState.Connected -> AppConnected(
                    if (currentState.serverSocket == null) TicTacToeGame.Player2 else TicTacToeGame.Player1,
                    currentState.socket.openReadChannel(),
                    currentState.socket.openWriteChannel(autoFlush = true)
                ) {
                    coroutineScope.launch(Dispatchers.IO) {
                        currentState.serverSocket?.close()
                        currentState.socket.close()
                    }
                    state = ConnectionState.UNINITIALISED
                }
            }
        }
    }
}

sealed interface GameState {
    object Running : GameState

    data class Completed(val winner: TicTacToeGame.Player?) : GameState
}

@Composable
fun AppConnected(
    player: TicTacToeGame.Player,
    readChannel: ByteReadChannel,
    writeChannel: ByteWriteChannel,
    onExit: () -> Unit
) {
    var hasInitialTurn by remember { mutableStateOf(player == TicTacToeGame.Player1) }
    var gameState by remember { mutableStateOf<GameState>(GameState.Running) }

    when(val state = gameState) {
        is GameState.Running -> Game(hasInitialTurn, TicTacToeGame(player), readChannel, writeChannel) {
            if (it is GameResult.GameEnded) {
                gameState = GameState.Completed(it.winner)
            } else {
                onExit()
            }
        }
        is GameState.Completed -> GameEnded(player, state.winner) {
            hasInitialTurn = !hasInitialTurn
            gameState = GameState.Running
        }
    }
}

@Composable
@Preview
private fun AppPreview() {
    App { }
}
