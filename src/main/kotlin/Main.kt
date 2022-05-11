import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import nl.marc.tictactoe.App
import nl.marc.tictactoe.ui.model.OnApplicationClosed

@ExperimentalSerializationApi
fun main() = application {
    val coroutineScope = rememberCoroutineScope()
    var onClose: OnApplicationClosed? = null

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
