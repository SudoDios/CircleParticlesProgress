import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.util.concurrent.TimeUnit

@Composable
fun App() {

    var progress by remember { mutableStateOf(0.8f) }
    var time by remember { mutableStateOf(0L) }

    DisposableEffect(Unit) {
        val timer = object : CountDownTimer(30000,30) {
            override fun onTick(millis: Long) {
                progress = (millis * 100f / 30000f) / 100f
                time = millis
            }
            override fun onFinish() {
                time = 0L
            }
        }.start()
        onDispose {
            timer.cancel()
        }
    }

    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color(0xFF1b263b)),contentAlignment = Alignment.Center) {
            Box(Modifier.size(106.dp).clip(RoundedCornerShape(50)).background(Color(0xFFf4a261)), contentAlignment = Alignment.Center) {
                Text(
                    text = String.format("%d sec", TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))),
                    color = Color(0xFF1b263b)
                )
            }
            CircleParticlesProgress(
                modifier = Modifier.size(120.dp),
                progress = progress,
                thickness = 6.dp,
                particlesCount = 40,
                isBig = false,
                reverse = true,
                progressColor = Color(0xFFf4a261)
            )
        }
    }
}

fun main() = application {
    Window(title = "Circle Particles Progress",onCloseRequest = ::exitApplication) {
        App()
    }
}
