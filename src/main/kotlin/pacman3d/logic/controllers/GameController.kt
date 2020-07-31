package pacman3d.logic.controllers

import pacman3d.Sound
import pacman3d.SoundPlayer
import pacman3d.entities.Ghost
import pacman3d.entities.Maze
import pacman3d.entities.Maze.Companion.isDotOrEnergizer
import pacman3d.entities.Maze.Companion.isEnergizer
import pacman3d.entities.World
import pacman3d.ext.levelValue
import pacman3d.logic.behaviors.InGhostHouseMovementStrategy
import pacman3d.logic.behaviors.LeaveGhostHouseMovementStrategy
import pacman3d.logic.states.GameStateMachine
import three.js.*

/**
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
// TODO: Move this to a better location
class GameController {

    private val inkyDotLimit = arrayOf(30, 0)
    private val pinkyDotLimit = arrayOf(0)
    private val clydeDotLimit = arrayOf(60, 50, 0)

    private val world = World()

    private val level = 0

    private val stateMachine = GameStateMachine(world)

    private var timeElapsedSinceLastDotEaten = 0.0

    private var lastPacmanMazeIndex = 0

    private val ghostBehaviorController = GhostBehaviorController(world)

    private fun initLevel() = with (world) {
        inky.dotCounter.limit = inkyDotLimit.levelValue(level)
        pinky.dotCounter.limit = pinkyDotLimit.levelValue(level)
        clyde.dotCounter.limit = clydeDotLimit.levelValue(level)

        world.resetState()
    }

    fun update(time: Double) {

        updateGameLogic(time)

        ghostBehaviorController.update(time)

        stateMachine.update(time)

        world.update(time)
    }

    private fun updateGameLogic(time: Double) = with (world) {

        val pacmanPosition = pacman.position
        val mazeValue = maze[pacmanPosition]
        val dotEaten = mazeValue.isDotOrEnergizer
        val isEnergizer = mazeValue.isEnergizer

        if (dotEaten) {
            SoundPlayer.play(Sound.Chomp)

            timeElapsedSinceLastDotEaten = 0.0

            score += valueOf(mazeValue)
            dots.eat(pacmanPosition)
            maze.eatDot(pacmanPosition)

            ghostBehaviorController.onDotEaten(isEnergizer)
        }
        else {
            timeElapsedSinceLastDotEaten += time
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

    private fun valueOf(mazeValue: Byte) = when (mazeValue) {
        Maze.DOT -> 10
        Maze.ENERGIZER -> 50
        else -> 0
    }
}