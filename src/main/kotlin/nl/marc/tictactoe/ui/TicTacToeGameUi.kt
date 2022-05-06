package nl.marc.tictactoe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import kotlinx.coroutines.launch
import nl.marc.tictactoe.domain.TicTacToeGame
import nl.marc.tictactoe.utils.TcpSocket

@Composable
fun Game(hasInitialTurn: Boolean, player: TicTacToeGame.Player, connection: TcpSocket, onGameEnded: (TicTacToeGame.Player?) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val game = remember { TicTacToeGame(player) }
    var hasTurn by remember { mutableStateOf(hasInitialTurn) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(if(hasTurn) "It's your turn" else "Waiting for other player...")

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.TL) {
                game.markCell(it, true)
                hasTurn = false
                coroutineScope.launch {
                    connection.writeLine(it.name)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.TC) {
                game.markCell(it, true)
                hasTurn = false
                coroutineScope.launch {
                    connection.writeLine(it.name)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.TR) {
                game.markCell(it, true)
                hasTurn = false
                coroutineScope.launch {
                    connection.writeLine(it.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.ML) {
                game.markCell(it, true)
                hasTurn = false
                coroutineScope.launch {
                    connection.writeLine(it.name)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.MC) {
                game.markCell(it, true)
                hasTurn = false
                coroutineScope.launch {
                    connection.writeLine(it.name)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.MR) {
                game.markCell(it, true)
                hasTurn = false
                coroutineScope.launch {
                    connection.writeLine(it.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.BL) {
                game.markCell(it, true)
                hasTurn = false
                coroutineScope.launch {
                    connection.writeLine(it.name)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.BC) {
                game.markCell(it, true)
                hasTurn = false
                coroutineScope.launch {
                    connection.writeLine(it.name)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            TicTacToeCell(hasTurn, game, TicTacToeGame.Cells.BR) {
                game.markCell(it, true)
                hasTurn = false
                coroutineScope.launch {
                    connection.writeLine(it.name)
                }
            }
        }
    }

    if (!hasTurn) {
        coroutineScope.launch {
            val line = connection.readLine()?.trim()?.uppercase()
            if (line == null) {
                onGameEnded(null)
            } else {
                game.markCell(TicTacToeGame.Cells.valueOf(line), false)
                hasTurn = true
            }
        }
    }

    if (!game.isGameRunning) {
        onGameEnded(game.winner)
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
                Text(claimedBy.mark, textAlign = TextAlign.Center)
            }
        }
    }
}
