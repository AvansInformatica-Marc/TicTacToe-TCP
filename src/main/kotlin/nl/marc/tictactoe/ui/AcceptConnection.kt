package nl.marc.tictactoe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.network.sockets.*
import kotlinx.coroutines.launch
import nl.marc.tictactoe.domain.ConnectionCodes
import java.net.BindException

@Composable
fun AcceptConnection(socketBuilder: TcpSocketBuilder, onSocketAvailable: (ServerSocket, Socket) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var connectionCode by remember { mutableStateOf<String?>(null) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(style = MaterialTheme.typography.body1, text = "Connection code: ")
        SelectionContainer {
            Text(style = MaterialTheme.typography.body1, text = connectionCode ?: "loading...")
        }
    }

    if (connectionCode == null) {
        coroutineScope.launch {
            val (resolvedConnectionCode, serverSocket) = tryCreateSocket(socketBuilder)
            connectionCode = resolvedConnectionCode
            onSocketAvailable(serverSocket, serverSocket.accept())
        }
    }
}

suspend fun tryCreateSocket(socketBuilder: TcpSocketBuilder): Pair<String, ServerSocket> {
    val port = (0..6).random() * 100 + 5000
    val connectionCode = ConnectionCodes.getConnectionCode(port)

    return try {
        connectionCode to socketBuilder.bind(port = port)
    } catch (error: BindException) {
        tryCreateSocket(socketBuilder)
    }
}
