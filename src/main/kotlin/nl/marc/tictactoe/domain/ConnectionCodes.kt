package nl.marc.tictactoe.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.net.DatagramSocket
import java.net.InetAddress

object ConnectionCodes {
    private const val CONNECTION_CODE_RADIX = 36

    suspend fun getConnectionCode(port: Int): String {
        val address = BigInteger(1, getIpAddressOfCurrentDevice().address)

        return "${address.toString(CONNECTION_CODE_RADIX)}-${port.toString(CONNECTION_CODE_RADIX)}"
    }

    private suspend fun getIpAddressOfCurrentDevice(): InetAddress {
        return withContext(Dispatchers.IO) {
            DatagramSocket().use {
                it.connect(InetAddress.getByName("8.8.8.8"), 10002)
                it.localAddress
            }
        }
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
