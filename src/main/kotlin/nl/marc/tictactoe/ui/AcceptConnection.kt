package nl.marc.tictactoe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.network.sockets.*
import kotlinx.coroutines.launch
import nl.marc.tictactoe.domain.ConnectionCodes

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
        Text(style = MaterialTheme.typography.body1, text = connectionCode ?: "loading...")
    }

    if (connectionCode == null) {
        val port = 5010 + (0..80).random()
        coroutineScope.launch {
            connectionCode = ConnectionCodes.getConnectionCode(port)

            val serverSocket = socketBuilder.bind(port = port)
            onSocketAvailable(serverSocket, serverSocket.accept())
        }
    }
}
