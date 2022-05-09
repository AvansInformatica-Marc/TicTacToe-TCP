package nl.marc.tictactoe.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.marc.tictactoe.domain.IsRemoteDevice

@Composable
fun DeviceChoice(setPlayer: (IsRemoteDevice) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(onClick = {
            setPlayer(IsRemoteDevice(false))
        }) {
            Text("Connect to this device")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(onClick = {
            setPlayer(IsRemoteDevice(true))
        }) {
            Text("Connect to remote device")
        }
    }
}

@Composable
@Preview
private fun DeviceChoicePreview() {
    DeviceChoice {  }
}
