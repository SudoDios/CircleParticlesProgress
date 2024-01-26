import java.util.*

abstract class CountDownTimer(private var millisInFuture : Long, private var countDownInterval : Long) {

    private var mStopTimeInFuture = 0L

    abstract fun onTick(millis: Long)
    abstract fun onFinish()

    private var timer = Timer()

    @Synchronized
    fun start(): CountDownTimer {
        if (millisInFuture <= 0) {
            onFinish()
            return this
        }
        mStopTimeInFuture = System.currentTimeMillis() + millisInFuture
        timer.schedule(object : TimerTask() {
            override fun run() {
                val millisLeft = mStopTimeInFuture - System.currentTimeMillis()
                if (millisLeft <= 0) {
                    onFinish()
                    timer.cancel()
                } else {
                    onTick(millisLeft)
                }
            }
        },0L,countDownInterval)
        return this
    }

    @Synchronized
    fun cancel() {
        timer.cancel()
    }

}