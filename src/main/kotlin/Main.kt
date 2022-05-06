import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import nl.marc.tictactoe.App

fun main() = application {
    val coroutineScope = rememberCoroutineScope()
    var onClose: (suspend () -> Unit)? = null

    Window(onCloseRequest = {
        coroutineScope.launch {
            onClose?.invoke()
            exitApplication()
        }
    }) {
        App {
            onClose = it
        }
    }
}
