package pacman3d.logic.controllers

class Timer(timeoutInSeconds: Double, private val onTimeout: () -> Unit) {

    private var timerDone = false

    private val isIndefinite = timeoutInSeconds <= 0

    var timeLeft = timeoutInSeconds.also { require(!timeoutInSeconds.isNaN()) }
        private set

    var isPaused = false
        private set

    fun update(time: Double) {
        if (isIndefinite || isPaused) return

        if (timeLeft > 0) timeLeft -= time

        if (timeLeft <= 0 && !timerDone) {
            timerDone = true
            onTimeout()
        }
    }

    fun pause() { isPaused = true }

    fun resume() { isPaused = false }
}