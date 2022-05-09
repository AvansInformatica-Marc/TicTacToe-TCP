package nl.marc.tictactoe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import nl.marc.tictactoe.domain.ConnectionCodes
import nl.marc.tictactoe.utils.TcpSocket
import java.math.BigInteger

@Composable
fun AcceptConnection(onSocketAvailable: (TcpSocket) -> Unit) {
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
        coroutineScope.launch {
            val port = 5010 + (0..80).random()
            connectionCode = ConnectionCodes.getConnectionCode(port)
            onSocketAvailable(TcpSocket.createSocket(port))
        }
    }
}
