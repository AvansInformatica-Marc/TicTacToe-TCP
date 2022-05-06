package nl.marc.tictactoe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.marc.tictactoe.domain.TicTacToeGame

@Composable
fun GameEnded(player: TicTacToeGame.Player, winner: TicTacToeGame.Player?, onRematch: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(when (winner) {
            null -> "It's tied"
            player -> "You won!"
            else -> "You lost..."
        })

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onRematch()
            }
        ) {
            Text("Rematch!")
        }
    }
}
