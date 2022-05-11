package nl.marc.tictactoe.ui.model

import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*

data class ConnectedSockets(
    val socket: Socket,
    val serverSocket: ServerSocket? = null
) : Closeable {
    override fun close() {
        socket.close()
        serverSocket?.close()
    }
}
