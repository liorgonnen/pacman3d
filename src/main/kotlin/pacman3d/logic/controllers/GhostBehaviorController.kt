package pacman3d.logic.controllers

import pacman3d.entities.Updatable
import pacman3d.entities.World
import pacman3d.ext.levelValue

class GhostBehaviorController(private val world: World) {

    companion object {
        private const val INDEFINITE = 0.0
        private const val ONE_FRAME = 1.0 / 60
    }

    private enum class Behavior {
        Chase,
        Scatter,
        Frightened,
    }

    private val scatterChaseTimeTable = arrayOf(
        /* Level 1    */ arrayOf(7.0, 20.0, 7.0, 20.0, 5.0, 20.0, 5.0, INDEFINITE),
        /* Levels 2-4 */ *Array(3) { arrayOf(7.0, 20.0, 7.0, 20.0, 5.0, 1033.0, ONE_FRAME, INDEFINITE) },
        /* Levels 5+  */ arrayOf(5.0, 20.0, 5.0, 20.0, 5.0, 1037.0, ONE_FRAME, INDEFINITE),
    )

    private val frightenedTime  = arrayOf(6, 5, 4, 3, 2, 5, 2, 2, 1, 5, 2, 1, 1, 3, 1, 1, 0, 1, 0)
    private val numberOfFlashes = arrayOf(5, 5, 5, 5, 5, 5, 5, 5, 3, 5, 5, 3, 3, 5, 3, 3, 0, 3, 0)

    private lateinit var scatterChaseTimer: Timer

    private var frightenedTimer: Timer? = null

    private var previousBehavior: Behavior = Behavior.Scatter

    private var currentBehavior: Behavior = Behavior.Scatter

    private var timeTableIndex = 0

    var gameLevel = 0
        set(value) {
            field = value
            timeTableIndex = 0
        }

    init {
        onScatterChaseTimerElapsed()
    }

    fun setFrightened(world: World) {
        val time = frightenedTime.levelValue(gameLevel)
        if (time == 0) return

        scatterChaseTimer.pause()
        frightenedTimer = Timer(time.toDouble()) {
            frightenedTimer = null
            scatterChaseTimer.resume()
        }

        world.ghosts.forEach { ghost ->
            //if (ghost.modeit.setMode()
        }
    }

    private fun onScatterChaseTimerElapsed() {
        val time = scatterChaseTimeTable.levelValue(gameLevel)[timeTableIndex++]
        scatterChaseTimer = Timer(time, ::onScatterChaseTimerElapsed)
    }

    fun update(time: Double) {
        scatterChaseTimer.update(time)
        frightenedTimer?.update(time)
    }

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
}