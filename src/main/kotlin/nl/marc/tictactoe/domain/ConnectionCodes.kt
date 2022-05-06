package nl.marc.tictactoe.domain

import nl.marc.tictactoe.utils.TcpSocket
import java.math.BigInteger

object ConnectionCodes {
    private const val CONNECTION_CODE_RADIX = 36

    suspend fun getConnectionCode(port: Int): String {
        val address = BigInteger(1, TcpSocket.getIpAddressOfCurrentDevice().address)

        return "${address.toString(CONNECTION_CODE_RADIX)}-${port.toString(CONNECTION_CODE_RADIX)}"
    }

    fun getIpAndPort(connectionCode: String): Pair<String, Int> {
        val (address, port) = connectionCode.split("-")

        val ip = address.toLong(CONNECTION_CODE_RADIX)
        val ipAddress = (ip ushr 24 and 0xFF).toString() + '.' +
                (ip ushr 16 and 0xFF).toString() + '.' +
                (ip ushr 8 and 0xFF).toString() + '.' +
                (ip and 0xFF).toString()

        return ipAddress to port.toInt(CONNECTION_CODE_RADIX)
    }
}
