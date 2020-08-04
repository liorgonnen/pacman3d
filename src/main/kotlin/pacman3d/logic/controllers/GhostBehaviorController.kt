package pacman3d.logic.controllers

import pacman3d.entities.Ghost
import pacman3d.entities.World
import pacman3d.ext.levelValue
import pacman3d.logic.Direction
import pacman3d.logic.Direction.LEFT
import pacman3d.logic.GhostState
import pacman3d.logic.GhostState.*

/**
 * Ghosts are forced to reverse direction by the system anytime the mode changes from:
 * - chase-to-scatter
 * - chase-to-frightened
 * - scatter-to-chase
 * - scatter-to-frightened.
 *
 * Ghosts do not reverse direction when changing back from frightened to chase or scatter modes.
 */
class GhostBehaviorController(private val world: World) {

    companion object {
        private const val INDEFINITE = 0.0
        private const val ONE_FRAME = 1.0 / 60
    }

    private val scatterChaseTimeTable = arrayOf(
        /* Level 1    */ arrayOf(7.0, 20.0, 7.0, 20.0, 5.0, 20.0, 5.0, INDEFINITE),
        /* Levels 2-4 */ *Array(3) { arrayOf(7.0, 20.0, 7.0, 20.0, 5.0, 1033.0, ONE_FRAME, INDEFINITE) },
        /* Levels 5+  */ arrayOf(5.0, 20.0, 5.0, 20.0, 5.0, 1037.0, ONE_FRAME, INDEFINITE),
    )

    private val frightenedTime  = arrayOf(6, 5, 4, 3, 2, 5, 2, 2, 1, 5, 2, 1, 1, 3, 1, 1, 0, 1, 0)
    private val numberOfFlashes = arrayOf(5, 5, 5, 5, 5, 5, 5, 5, 3, 5, 5, 3, 3, 5, 3, 3, 0, 3, 0)

    // TODO: Reset this timer when a life is lost
    private var scatterChaseTimer: Timer

    private var frightenedTimer: Timer? = null

    private var globalChaseScatterState: GhostState = Scatter

    // The direction to which the ghost turns upon leaving the ghost house
    private var ghostExitDirection: Direction = LEFT

    private var timeTableIndex = 0

    var gameLevel = 0
        set(value) {
            field = value
            timeTableIndex = 0
        }

    init {
        scatterChaseTimer = createNewScatterChaseTimer()
    }

    fun onDotEaten(isEnergizer: Boolean) {
        if (isEnergizer) setFrightened()

        nextGhostToLeaveGhostHouse?.let { ghost ->
            ghost.dotCounter.count++
            if (ghost.dotCounter.reachedLimit) ghost.state = LeaveGhostHouse
        }
    }

    fun onGhostEaten(ghost: Ghost) {
        ghost.state = Eaten
    }

    fun onVisualEffectBegin() {
        scatterChaseTimer.pause()
        frightenedTimer?.pause()
    }

    fun onVisualEffectEnd() {
        scatterChaseTimer.resume()
        frightenedTimer?.resume()
    }

    /**
     * Order of preferred ghost to leave house: Pinky, then Inky, and then Clyde
     */
    private val nextGhostToLeaveGhostHouse: Ghost? get()
        = world.preferredOrderToLeaveHouse.firstOrNull { it.state == InGhostHouse }

    private fun setFrightened() {
        val time = frightenedTime.levelValue(gameLevel)

        if (time == 0) return // The level may not support a frightened time at all

        scatterChaseTimer.pause()
        frightenedTimer = Timer(time.toDouble(), onTimeout = ::onFrightenedTimerElapsed)

        switchAllGhostsToState(Frightened)
    }

    private fun onFrightenedTimerElapsed() {
        frightenedTimer = null
        scatterChaseTimer.resume()

        switchAllGhostsToState(globalChaseScatterState, reverseDirection = false)
    }

    private fun createNewScatterChaseTimer(): Timer {
        val time = scatterChaseTimeTable.levelValue(gameLevel)[timeTableIndex++]
        return Timer(time, ::onScatterChaseTimerElapsed)
    }

    private fun onScatterChaseTimerElapsed() {
        scatterChaseTimer = createNewScatterChaseTimer()

        flipChaseScatterState()
        flipGhostExitDirection()
    }

    fun update(time: Double) {
        scatterChaseTimer.update(time)
        frightenedTimer?.update(time)

        forEachGhost { ghost ->
            if (ghost.state == LeaveGhostHouse && ghost.hasReachedTarget) {
                ghost.state = globalChaseScatterState
                ghost.mandatoryDirectionOverride = ghostExitDirection
            }
        }

        // The only time blinky is in the ghost house is if he's captured by pacman, and he
        // immediately turns around and leaves once revived
        if (world.blinky.state == InGhostHouse) world.blinky.state = LeaveGhostHouse
    }

    private fun flipChaseScatterState() {
        globalChaseScatterState = if (globalChaseScatterState == Chase) Scatter else Chase

        switchAllGhostsToState(globalChaseScatterState)
    }

    private fun flipGhostExitDirection() { ghostExitDirection = ghostExitDirection.oppositeDirection }

    private fun switchAllGhostsToState(newState: GhostState, reverseDirection: Boolean = true) = forEachGhost { ghost ->
        if (ghost.state.canSwitchToState(newState)) {
            if (reverseDirection) ghost.reverseDirection()
            ghost.state = newState
        }
    }

    private inline fun forEachGhost(action: (Ghost) -> Unit) = world.ghosts.forEach(action)
}