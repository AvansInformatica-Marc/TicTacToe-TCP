package nl.marc.tictactoe.utils

class SuspendingLazy<T>(private val create: suspend () -> T) {
    var value: T? = null
        private set

    suspend fun get(): T {
        return value ?: create().also {
            value = it
        }
    }
}
