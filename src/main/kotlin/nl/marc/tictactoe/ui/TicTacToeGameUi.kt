package nl.marc.tictactoe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import kotlinx.coroutines.launch
import nl.marc.tictactoe.data.TicTacToeCommandSocket
import nl.marc.tictactoe.domain.RemoteCommand
import nl.marc.tictactoe.domain.TicTacToeGame
import nl.marc.tictactoe.ui.model.GameResult

@Composable
fun Game(hasInitialTurn: Boolean, game: TicTacToeGame, commandSocket: TicTacToeCommandSocket, onGameEnded: (GameResult) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var hasTurn by remember { mutableStateOf(hasInitialTurn) }

    TicTacToeGrid(hasTurn, game) {
        hasTurn = false
        game.markCell(it, true)
        coroutineScope.launch {
            commandSocket.sendCommand(RemoteCommand.MarkCellCommand(it))
        }
    }

    if (!hasTurn) {
        coroutineScope.launch {
            val command = commandSocket.readCommand()
            if (command is RemoteCommand.QuitGameCommand) {
                onGameEnded(GameResult.GameQuit)
            } else if (command is RemoteCommand.MarkCellCommand) {
                game.markCell(command.cell, false)
                hasTurn = true
            }
        }
    }

    if (!game.isGameRunning) {
        onGameEnded(GameResult.GameEnded(game.winner))
    }
}

@Composable
fun TicTacToeGrid(
    hasTurn: Boolean,
    game: TicTacToeGame,
    onCellClaimed: (TicTacToeGame.Cells) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(style = MaterialTheme.typography.h5, text = if(hasTurn) "It's your turn" else "Waiting for other player...")

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.TL, onCellClaimed)

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.TC, onCellClaimed)

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.TR, onCellClaimed)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.ML, onCellClaimed)

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.MC, onCellClaimed)

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.MR, onCellClaimed)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.BL, onCellClaimed)

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.BC, onCellClaimed)

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.BR, onCellClaimed)
        }
    }
}

@Composable
fun TicTacToeCell(
    hasTurn: Boolean,
    game: TicTacToeGame,
    cell: TicTacToeGame.Cells,
    onClaim: (TicTacToeGame.Cells) -> Unit
) {
    val claimedBy = game.cells[cell]
    BoxWithConstraints {
        val modifier = Modifier
            .width(min(maxWidth / 4, 80.dp))
            .height(min(maxHeight / 4, 80.dp))
            .align(Alignment.Center)

        if (claimedBy == null) {
            Button(
                onClick = {
                    onClaim(cell)
                },
                enabled = hasTurn,
                modifier = modifier
            ) {
                Text("claim")
            }
        } else {
            Box(modifier, contentAlignment = Alignment.Center) {
                Text(style = MaterialTheme.typography.h6, text = claimedBy.mark, textAlign = TextAlign.Center)
            }
        }
    }
}
