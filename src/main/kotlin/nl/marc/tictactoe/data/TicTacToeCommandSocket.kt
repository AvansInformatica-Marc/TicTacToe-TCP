package nl.marc.tictactoe.data

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import nl.marc.tictactoe.domain.RemoteCommand

interface TicTacToeCommandSocket {
    suspend fun readCommand(): RemoteCommand?
    suspend fun sendCommand(command: RemoteCommand)

    companion object {
        @ExperimentalSerializationApi
        fun create(socket: Socket, protobuf: ProtoBuf = ProtoBuf) = create(
            socket.openReadChannel(),
            socket.openWriteChannel(autoFlush = true),
            protobuf
        )

        @ExperimentalSerializationApi
        fun create(readChannel: ByteReadChannel, writeChannel: ByteWriteChannel, protobuf: ProtoBuf = ProtoBuf) : TicTacToeCommandSocket {
            require(writeChannel.autoFlush)
            return TicTacToeCommandSocketImpl(readChannel, writeChannel, protobuf)
        }

        fun createEmptyCommandSocketForTesting() : TicTacToeCommandSocket {
            return object : TicTacToeCommandSocket {
                override suspend fun readCommand() = null

                override suspend fun sendCommand(command: RemoteCommand) {}
            }
        }
    }
}
