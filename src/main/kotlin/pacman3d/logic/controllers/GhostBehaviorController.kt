package pacman3d.logic.controllers

import pacman3d.entities.Ghost
import pacman3d.entities.World
import pacman3d.ext.levelValue
import pacman3d.logic.Direction
import pacman3d.logic.Direction.LEFT
import pacman3d.logic.GameData
import pacman3d.logic.GameData.CLYDE_DOT_LIMIT
import pacman3d.logic.GameData.FRIGHTENED_TIME
import pacman3d.logic.GameData.INKY_DOT_LIMIT
import pacman3d.logic.GameData.LEAVE_GHOST_HOUSE_TIMES
import pacman3d.logic.GameData.PINKY_DOT_LIMIT
import pacman3d.logic.GameData.SCATTER_CHASE_TIME_TABLE
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

    // TODO: Reset this timer when a life is lost
    private var scatterChaseTimer: Timer

    private var frightenedTimer: Timer? = null

    private var globalChaseScatterState: GhostState = Scatter

    // The direction to which the ghost turns upon leaving the ghost house
    private var ghostExitDirection: Direction = LEFT

    private var gameLevel = 0

    private var timeElapsedSinceLastDotEaten = 0.0

    private var timeUntilNextGhostLeavesHouse = 0

    init {
        scatterChaseTimer = createNewScatterChaseTimer()
    }

    fun initLevel(level: Int) = with (world) {
        gameLevel = level
        inky.dotCounter.limit = INKY_DOT_LIMIT.levelValue(level)
        pinky.dotCounter.limit = PINKY_DOT_LIMIT.levelValue(level)
        clyde.dotCounter.limit = CLYDE_DOT_LIMIT.levelValue(level)

        timeUntilNextGhostLeavesHouse = LEAVE_GHOST_HOUSE_TIMES.levelValue(level)

        ghosts.forEach { it.speed = 5.0 }
    }

    fun onDotEaten(isEnergizer: Boolean) {
        if (isEnergizer) setFrightened()

        nextGhostToLeaveGhostHouse?.let { ghost ->
            ghost.dotCounter.count++
            if (ghost.dotCounter.reachedLimit) ghost.state = LeaveGhostHouse
        }

        timeElapsedSinceLastDotEaten = 0.0
    }

    fun onNoDotEaten(time: Double) {
        timeElapsedSinceLastDotEaten += time

        // Anytime Pac-Man avoids eating dots long enough for the timer to reach its limit, the most-preferred ghost
        // waiting in the ghost house (if any) is forced to leave immediately and the timer is reset to zero
        if (timeElapsedSinceLastDotEaten >= timeUntilNextGhostLeavesHouse) {
            timeElapsedSinceLastDotEaten = 0.0
            nextGhostToLeaveGhostHouse?.let { ghost -> ghost.state = LeaveGhostHouse }
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
        val time = FRIGHTENED_TIME.levelValue(gameLevel)

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
        val time = SCATTER_CHASE_TIME_TABLE.levelValue(gameLevel)[gameLevel++]
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

        forEachGhost {  ghost ->
            with (ghost) {
                if (state == LeaveGhostHouse && hasReachedTarget) {
                    state = globalChaseScatterState
                    mandatoryDirectionOverride = ghostExitDirection
                }
                // An eaten ghost that reaches back to the ghost house is resurrected at immediately goes back out
                else if (state == Eaten && hasReachedTarget) {
                    state = LeaveGhostHouse
                }
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