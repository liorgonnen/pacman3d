package pacman3d.logic.controllers

class Timer(timeoutInSeconds: Double, private val onTimeout: () -> Unit) {

    private var timerDone = false

    private val isIndefinite = timeoutInSeconds <= 0

    private var timeLeft = timeoutInSeconds * 1000.0

    private var isPaused = false

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