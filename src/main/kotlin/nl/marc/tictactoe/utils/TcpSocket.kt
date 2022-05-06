package nl.marc.tictactoe.utils

import kotlinx.coroutines.*
import java.io.Closeable
import java.net.*

class TcpSocket private constructor(
    private val socket: Socket,
    private val onClosed: (() -> Unit)? = null
) : Closeable {
    class SuspendingLazy<T>(private val create: suspend () -> T) {
        var property: T? = null
            private set

        suspend fun get(): T {
            return property ?: create().also {
                property = it
            }
        }
    }

    val outputStream = SuspendingLazy {
        withContext(Dispatchers.IO) {
            socket.getOutputStream()
        }
    }

    val inputStream = SuspendingLazy {
        withContext(Dispatchers.IO) {
            socket.getInputStream()
        }
    }

    val writer = SuspendingLazy {
        outputStream.get().bufferedWriter()
    }

    val reader = SuspendingLazy {
        inputStream.get().bufferedReader()
    }

    suspend fun readLine(): String? {
        return withContext(Dispatchers.IO) {
            try {
                reader.get().readLine()
            } catch (error: SocketException) {
                null
            }
        }
    }

    suspend fun writeLine(line: String) {
        withContext(Dispatchers.IO) {
            writer.get().also {
                it.appendLine(line)
                it.flush()
            }
        }
    }

    suspend fun closeSuspending() {
        withContext(Dispatchers.IO) {
            close()
        }
    }

    override fun close() {
        reader.property?.close()
        writer.property?.close()
        outputStream.property?.close()
        inputStream.property?.close()
        socket.close()
        onClosed?.invoke()
    }

    companion object {
        suspend fun createSocket(port: Int): TcpSocket {
            return withContext(Dispatchers.IO) {
                val serverSocket = ServerSocket(port)
                TcpSocket(serverSocket.accept()) {
                    launch(Dispatchers.IO) {
                        serverSocket.close()
                    }
                }
            }
        }

        suspend fun connectToRemoteSocket(host: String, port: Int): TcpSocket {
            return withContext(Dispatchers.IO) {
                TcpSocket(Socket(host, port))
            }
        }

        suspend fun getIpAddressOfCurrentDevice(): InetAddress {
            return withContext(Dispatchers.IO) {
                DatagramSocket().use {
                    it.connect(InetAddress.getByName("8.8.8.8"), 10002)
                    it.localAddress
                }
            }
        }
    }
}
