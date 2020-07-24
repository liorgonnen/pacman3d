package pacman3d.logic.states

import pacman3d.entities.Ghost
import pacman3d.entities.Maze
import pacman3d.entities.Maze.Companion.isDotOrPill
import pacman3d.entities.World
import pacman3d.logic.behaviors.InGhostHouse
import pacman3d.logic.behaviors.LeaveGhostHouse
import three.js.Scene

// TODO: Move this to a better location
class GameController {
    /**
     * //
     * For each ghost:
     * - Dot counter with a dot limit
     * If preferred ghost counter reaches limit, ghost leaves house immediately. Dot counter stops (not reset)
     *
     * Dot limit:
     * - Pinky: 0 (always) - Leaves immediately as level begins
     * - Inky: Level 1 - 30, Level 2 - 0, Level 3 - 0
     * - Clyde: Level 1 - 60, Level 2 - 50, Level 3 - 0
     *
     * if (life is lost) - use a system-level dot counter instead of ghost-individual dot counter
     * This counter is enabled and set to zero when a life is lost
     * Pinky released when counter = 7
     * Inky released when counter = 17
     * if (Clyde in house && counter == 32) disableCounter() -> Switch back to ghost-individual counters
     */

    private val inkyDotLimit = arrayOf(30, 0)
    private val pinkyDotLimit = arrayOf(0)
    private val clydeDotLimit = arrayOf(60, 50, 0)

    private fun <T> Array<T>.levelValue(level: Int) = this[minOf(level, size - 1)]

    private val world = World()

    private val level = 0

    private val stateMachine = GameStateMachine(world)

    private var timeElapsedSinceLastDotEaten = 0.0

    private fun initLevel() = with (world) {
        inky.dotCounter.limit = inkyDotLimit.levelValue(level)
        pinky.dotCounter.limit = pinkyDotLimit.levelValue(level)
        clyde.dotCounter.limit = clydeDotLimit.levelValue(level)

        world.resetState()
    }

    fun update(time: Double) {

        updateGameLogic(time)

        stateMachine.update(time)

        world.update(time)
    }

    private fun updateGameLogic(time: Double) = with (world) {

        val pacmanPosition = pacman.position
        val mazeValue = maze[pacmanPosition]
        val dotEaten = mazeValue.isDotOrPill

        nextGhostToLeaveGhostHouse?.let { ghost ->
            if (dotEaten) {
                timeElapsedSinceLastDotEaten = 0.0
                ghost.dotCounter.count++
                if (ghost.dotCounter.reachedLimit) ghost.setMode(LeaveGhostHouse, world)
                dots.lastEatenIndex = pacmanPosition.mazeIndex
                score += valueOf(mazeValue)
                maze.eatDot(pacmanPosition)
            } else {
                timeElapsedSinceLastDotEaten += time
            }
        }
    }

    fun start() {

        // TODO: Until I properly handle levels
        initLevel()

        stateMachine.start()
    }

    fun setup(scene: Scene) {
        world.addRenderablesToScene(scene)
    }

    /**
     * Order of preferred ghost to leave house: Pinky, then Inky, and then Clyde
     */
    private val nextGhostToLeaveGhostHouse: Ghost? get()
        = world.preferredOrderToLeaveHouse.firstOrNull { it.mode == InGhostHouse }

    private fun valueOf(mazeValue: Byte) = when (mazeValue) {
        Maze.DOT -> 10
        Maze.PILL -> 50
        else -> 0
    }
}