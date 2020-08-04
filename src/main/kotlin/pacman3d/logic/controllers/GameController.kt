package pacman3d.logic.controllers

import pacman3d.Sound
import pacman3d.SoundPlayer
import pacman3d.entities.AbsGameEntity
import pacman3d.entities.Maze
import pacman3d.entities.Maze.Companion.isDotOrEnergizer
import pacman3d.entities.Maze.Companion.isEnergizer
import pacman3d.entities.World
import pacman3d.ext.levelValue
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
 *
 * TODO:
 * The first ghost captured after an energizer has been eaten is always worth 200 points. Each additional ghost
 * captured from the same energizer will then be worth twice as many points as the one before it-400, 800, and 1,600
 * points, respectively. If all four ghosts are captured at all four energizers, an additional 12,000 points can be
 * earned on these earlier levels
 */
// TODO: Move this to a better location
class GameController {

    companion object {
        private const val VISUAL_EFFECT_TIME = 1.2
    }

    private val inkyDotLimit = arrayOf(30, 0)
    private val pinkyDotLimit = arrayOf(0)
    private val clydeDotLimit = arrayOf(60, 50, 0)

    private val world = World()

    private val level = 0

    private val stateMachine = GameStateMachine(world)

    private var timeElapsedSinceLastDotEaten = 0.0

    private var lastPacmanMazeIndex = 0

    private var pointsForEatenGhost = 200

    private val ghostBehaviorController = GhostBehaviorController(world)

    private var visualEffectTimer: Timer? = null

    private fun initLevel() = with (world) {
        inky.dotCounter.limit = inkyDotLimit.levelValue(level)
        pinky.dotCounter.limit = pinkyDotLimit.levelValue(level)
        clyde.dotCounter.limit = clydeDotLimit.levelValue(level)

        world.resetState()

        pacman.speed = 7.0
        ghosts.forEach { it.speed = 2.0 }
    }

    fun update(time: Double) {

        visualEffectTimer?.update(time)

        updateGameLogic(time)

        ghostBehaviorController.update(time)

        stateMachine.update(time)

        world.update(time)
    }

    private fun updateGameLogic(time: Double) = with (world) {

        if (maze[pacman.position].isDotOrEnergizer) handleDotEaten() else handleNoDotEaten(time)

        handleEatenGhosts()
    }

    private fun handleDotEaten() = with (world) {
        val mazeValue = maze[pacman.position]
        val isEnergizer = mazeValue.isEnergizer

        SoundPlayer.play(Sound.Chomp)

        // The first ghost captured after an energizer has been eaten is always worth 200 points
        if (isEnergizer) pointsForEatenGhost = 200

        timeElapsedSinceLastDotEaten = 0.0

        score += valueOf(mazeValue)
        dots.eat(pacman.position)
        maze.eatDot(pacman.position)

        ghostBehaviorController.onDotEaten(isEnergizer)
    }

    private fun handleNoDotEaten(time: Double) {
        timeElapsedSinceLastDotEaten += time
    }

    private fun handleEatenGhosts() = with (world) {
        ghosts.firstOrNull { it.state.isFrightened && it.position.mazeIndex == pacman.position.mazeIndex }?.let { eatenGhost ->
            ghostBehaviorController.onGhostEaten(eatenGhost)
            bonusPoints.show(pointsForEatenGhost, pacman.position)
            pointsForEatenGhost *= 2
            beginVisualEffect(pacman, eatenGhost)
        }
    }

    private fun beginVisualEffect(vararg participants: AbsGameEntity<*>) {
        world.onVisualEffectBegin()
        ghostBehaviorController.onVisualEffectBegin()

        visualEffectTimer = Timer(VISUAL_EFFECT_TIME) { endVisualEffect(participants) }

        participants.forEach { it.isVisible = false }
    }

    private fun endVisualEffect(participants: Array<out AbsGameEntity<*>>) {
        visualEffectTimer = null
        world.bonusPoints.hide()
        world.onVisualEffectEnd()
        ghostBehaviorController.onVisualEffectEnd()

        participants.forEach { it.isVisible = true }
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