import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.security.SecureRandom
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun CircleParticlesProgress(
    modifier: Modifier = Modifier,
    progressColor : Color = Color.Black,
    thickness : Dp = 5.dp,
    isBig : Boolean = false,
    reverse : Boolean = false,
    particlesCount : Int = 38,
    progress : Float
) {

    val particle by remember { mutableStateOf(Particles(particlesCount, isBig)) }
    var paint by remember { mutableStateOf(Paint()) }
    var invalidator by remember { mutableStateOf(0) }

    LaunchedEffect(progressColor,thickness) {
        paint = Paint().apply {
            color = progressColor
            strokeWidth = thickness.value
            style = PaintingStyle.Stroke
            strokeCap = StrokeCap.Round
        }
    }

    Canvas(modifier = modifier) {
        drawIntoCanvas {
            it.drawArc(0f,0f, right = size.width,size.height,-90f, sweepAngle = if (reverse) -progress * 360f else progress * 360f,false,paint)
            particle.draw(it,paint, size,if (reverse) -progress * 360f else progress * 360f,1f)
            invalidator++
        }
    }

}

private class Particles(count : Int, private var big : Boolean = false) {

    private var lastAnimationTime: Long = 0
    private var random = SecureRandom()

    private class Particle {
        var x: Float = 0f
        var y: Float = 0f
        var vx: Float = 0f
        var vy: Float = 0f
        var velocity: Float = 0f
        var alpha: Float = 0f
        var lifeTime: Float = 0f
        var currentTime: Float = 0f
    }

    private val particles = ArrayList<Particle>()
    private val freeParticles = ArrayList<Particle>()

    init {
        for (a in 0 until count) {
            freeParticles.add(Particle())
        }
    }

    private fun updateParticles(dt: Long) {
        var count = particles.size
        var a = 0
        while (a < count) {
            val particle = particles[a]
            if (particle.currentTime >= particle.lifeTime) {
                if (freeParticles.size < count) {
                    freeParticles.add(particle)
                }
                particles.removeAt(a)
                a--
                count--
                a++
                continue
            }
            particle.alpha = 1.0f - (particle.currentTime / particle.lifeTime)
            particle.x += particle.vx * particle.velocity * dt / 200.0f
            particle.y += particle.vy * particle.velocity * dt / 200.0f
            particle.currentTime += dt
            a++
        }
    }

    private var hasLast = false
    private var lastCx = 0f
    private var lastCy = 0f

    fun draw(canvas: Canvas, particlePaint: Paint, rect: Size, radProgress: Float, alpha: Float) {
        val count = particles.size
        for (a in 0 until count) {
            val particle = particles[a]
            particlePaint.alpha = (particle.alpha * alpha)
            canvas.drawPoints(PointMode.Points, listOf(Offset(particle.x, particle.y)),particlePaint)
        }

        val vx = sin(Math.PI / 180.0 * (radProgress - 90))
        val vy = -cos(Math.PI / 180.0 * (radProgress - 90))
        val rad: Float = rect.width / 2
        val cx = (-vy * rad + rect.center.x).toFloat()
        val cy = (vx * rad + rect.center.y).toFloat()
        val subCount = clamp(freeParticles.size / 12)
        for (a in 0 until subCount) {
            var newParticle: Particle
            if (freeParticles.isNotEmpty()) {
                newParticle = freeParticles[0]
                freeParticles.removeAt(0)
            } else {
                newParticle = Particle()
            }

            if (big && hasLast) {
                newParticle.x = lerp(lastCx, cx, (a + 1) / subCount.toFloat())
                newParticle.y = lerp(lastCy, cy, (a + 1) / subCount.toFloat())
            } else {
                newParticle.x = cx
                newParticle.y = cy
            }

            var angle: Double = (Math.PI / 180.0) * (random.nextInt(140) - 70)
            if (angle < 0) {
                angle += Math.PI * 2
            }
            newParticle.vx = (vx * cos(angle) - vy * sin(angle)).toFloat()
            newParticle.vy = (vx * sin(angle) + vy * cos(angle)).toFloat()

            newParticle.alpha = 1.0f
            newParticle.currentTime = 0f

            if (big) {
                newParticle.lifeTime = 400f + random.nextInt(200)
                newParticle.velocity = 19.0f + random.nextFloat() * 20.0f
            } else {
                newParticle.lifeTime = 200f + random.nextInt(100)
                newParticle.velocity = 12.0f + random.nextFloat() * 4.0f
            }
            particles.add(newParticle)
        }
        hasLast = true
        lastCx = cx
        lastCy = cy

        val newTime = System.currentTimeMillis()
        val dt = min(20.0, (newTime - lastAnimationTime).toDouble()).toLong()
        updateParticles(dt)
        lastAnimationTime = newTime
    }

    private fun clamp(value: Int): Int {
        return max(min(value.toDouble(), 3.0), 1.0).toInt()
    }
    private fun lerp(a: Float, b: Float, f: Float): Float {
        return a + f * (b - a)
    }

}