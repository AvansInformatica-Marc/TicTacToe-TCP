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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import nl.marc.tictactoe.data.TicTacToeCommandSocket
import nl.marc.tictactoe.domain.TicTacToeGame
import nl.marc.tictactoe.ui.*
import nl.marc.tictactoe.ui.model.ConnectedSockets
import nl.marc.tictactoe.ui.model.GameResult
import nl.marc.tictactoe.ui.model.GameState
import nl.marc.tictactoe.ui.model.OnApplicationClosed

@ExperimentalSerializationApi
@Composable
fun App(setOnCloseListener: (OnApplicationClosed) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var connection by remember { mutableStateOf<ConnectedSockets?>(null) }
    val selectorManager = remember { ActorSelectorManager(Dispatchers.IO) }
    val socketBuilder = remember { aSocket(selectorManager).tcp() }

    setOnCloseListener {
        withContext(Dispatchers.IO) {
            connection?.close()
            selectorManager.close()
        }
    }

    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
    ) {
        Surface (
            color = MaterialTheme.colors.background
        ) {
            when(val currentConnection = connection) {
                null -> DeviceConnection(socketBuilder) {
                    connection = it
                }
                else -> AppConnected(
                    if (currentConnection.serverSocket == null) TicTacToeGame.Player2 else TicTacToeGame.Player1,
                    TicTacToeCommandSocket.create(currentConnection.socket)
                ) {
                    coroutineScope.launch(Dispatchers.IO) {
                        currentConnection.close()
                    }
                    connection = null
                }
            }
        }
    }
}

@Composable
fun DeviceConnection(socketBuilder: TcpSocketBuilder, onConnected: (ConnectedSockets) -> Unit) {
    var isCurrentDevicePrimary by remember { mutableStateOf<Boolean?>(null) }

    when (isCurrentDevicePrimary) {
        null -> DeviceChoice {
            isCurrentDevicePrimary = !it.isRemoteDevice
        }
        true -> AcceptConnection(socketBuilder) { serverSocket, socket ->
            onConnected(ConnectedSockets(socket, serverSocket))
        }
        false -> ConnectToRemoteDevice(socketBuilder) { socket ->
            onConnected(ConnectedSockets(socket))
        }
    }
}

@Composable
fun AppConnected(
    player: TicTacToeGame.Player,
    commandSocket: TicTacToeCommandSocket,
    onExit: () -> Unit
) {
    var hasInitialTurn by remember { mutableStateOf(player == TicTacToeGame.Player1) }
    var gameState by remember { mutableStateOf<GameState>(GameState.Running) }

    when(val state = gameState) {
        is GameState.Running -> Game(hasInitialTurn, TicTacToeGame(player), commandSocket) {
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
