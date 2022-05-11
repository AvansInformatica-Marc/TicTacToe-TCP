package nl.marc.tictactoe.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class RemoteCommand {
    abstract val commandName: String

    @Serializable
    @SerialName("MarkCellCommand")
    data class MarkCellCommand(val cell: TicTacToeGame.Cells) : RemoteCommand() {
        override val commandName = "MarkCellCommand"
    }

    @Serializable
    @SerialName("QuitGameCommand")
    object QuitGameCommand : RemoteCommand() {
        override val commandName = "QuitGameCommand"
    }

    @Serializable
    @SerialName("RematchCommand")
    object RematchCommand : RemoteCommand() {
        override val commandName = "RematchCommand"
    }
}
