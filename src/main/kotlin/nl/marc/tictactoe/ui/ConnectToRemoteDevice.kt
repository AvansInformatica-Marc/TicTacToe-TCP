package nl.marc.tictactoe.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nl.marc.tictactoe.domain.ConnectionCodes
import nl.marc.tictactoe.utils.TcpSocket

@Composable
fun ConnectToRemoteDevice(onSocketAvailable: (TcpSocket) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    RequestConnectionCode {
        coroutineScope.launch {
            val (ip, port) = ConnectionCodes.getIpAndPort(it)
            onSocketAvailable(TcpSocket.connectToRemoteSocket(ip, port))
        }
    }
}

@Composable
private fun RequestConnectionCode(onConnectionCodeAvailable: (String) -> Unit) {
    var connectionCode by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextField(
            value = connectionCode,
            onValueChange = {
                connectionCode = it
            },
            label = { Text("Connection code") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onConnectionCodeAvailable(connectionCode)
            }
        ) {
            Text("Connect")
        }
    }
}

@Composable
@Preview
private fun RequestConnectionCodePreview() {
    RequestConnectionCode {  }
}
