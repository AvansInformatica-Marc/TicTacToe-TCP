package nl.marc.tictactoe.data

import io.ktor.utils.io.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.protobuf.ProtoBuf
import nl.marc.tictactoe.domain.RemoteCommand

@ExperimentalSerializationApi
class TicTacToeCommandSocketImpl(
    private val readChannel: ByteReadChannel,
    private val writeChannel: ByteWriteChannel,
    private val protobuf: ProtoBuf = ProtoBuf
) : TicTacToeCommandSocket {
    override suspend fun readCommand(): RemoteCommand? {
        val line = readChannel.readUTF8Line() ?: return RemoteCommand.QuitGameCommand

        return try {
            protobuf.decodeFromHexString<RemoteCommand>(line)
        } catch (error: RuntimeException) {
            null
        }
    }

    override suspend fun sendCommand(command: RemoteCommand) {
        writeChannel.writeStringUtf8(
            protobuf.encodeToHexString(command) + "\r\n"
        )
    }
}
