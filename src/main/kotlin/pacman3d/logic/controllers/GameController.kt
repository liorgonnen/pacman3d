package pacman3d.logic.controllers

import kotlinx.browser.document
import org.w3c.dom.events.KeyboardEvent
import pacman3d.*
import pacman3d.entities.AbsGameEntity
import pacman3d.entities.GameStateBanner
import pacman3d.entities.Maze.Companion.isDot
import pacman3d.entities.Maze.Companion.isDotOrEnergizer
import pacman3d.entities.Maze.Companion.isEnergizer
import pacman3d.entities.World
import pacman3d.ext.levelValue
import pacman3d.logic.Direction
import pacman3d.logic.controllers.GameState.Playing
import pacman3d.logic.controllers.GameState.WaitingForPlayer
import three.js.Scene

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

private enum class GameState {
    WaitingForPlayer,
    Playing,
}

class GameController {

    companion object {
        private const val VISUAL_EFFECT_TIME = 1.2

        private const val KEY_ARROW_LEFT = 37
        private const val KEY_ARROW_RIGHT = 39
        private const val KEY_ARROW_UP = 38
        private const val KEY_ARROW_DOWN = 40

        private val ALLOWED_KEYS = arrayOf(
            KEY_ARROW_LEFT,
            KEY_ARROW_RIGHT,
            KEY_ARROW_UP,
            KEY_ARROW_DOWN
        )
    }

    private val inkyDotLimit = arrayOf(30, 0)
    private val pinkyDotLimit = arrayOf(0)
    private val clydeDotLimit = arrayOf(60, 50, 0)

    private val world = World()

    private val level = 0

    private var gameState = WaitingForPlayer

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
        ghosts.forEach { it.speed = 5.0 }

        waitForPlayer()
    }

    private fun waitForPlayer() {
        gameState = WaitingForPlayer

        world.gameStateBanner.show(GameStateBanner.READY)
        world.setPacmanAndGhostsActive(false)
    }

    fun update(time: Double) {

        // When an effect is in progress we don't update the game logic
        visualEffectTimer?.let { timer ->
            timer.update(time)
            world.update(time)
            return
        }

        updateGameLogic(time)

        ghostBehaviorController.update(time)

        world.update(time)
    }

    private fun updateGameLogic(time: Double) {
        with (world) {
            if (maze[pacman.position].isDotOrEnergizer) handleDotEaten() else handleNoDotEaten(time)

            ghosts.firstOrNull { it.position.mazeIndexEquals(pacman.position) }?.also { ghost ->
                when {
                    // Handle eaten ghosts
                    ghost.state.isFrightened -> {
                        ghostBehaviorController.onGhostEaten(ghost)
                        bonusPoints.show(pointsForEatenGhost, pacman.position)
                        pointsForEatenGhost *= 2
                        beginVisualEffect(pacman, ghost)
                        return
                    }

                    // Handle captured pacman
                    ghost.state.canEatPacman -> {
                        pacman.resetState(world)
                        beginVisualEffect(pacman, ghost)
                        return
                    }
                }
            }
        }
    }

    private fun handleDotEaten() = with (world) {
        val mazeValue = maze[pacman.position]
        val isEnergizer = mazeValue.isEnergizer

        SoundPlayer.play(Sound.Chomp)

        // The first ghost captured after an energizer has been eaten is always worth 200 points
        if (isEnergizer) pointsForEatenGhost = 200

        timeElapsedSinceLastDotEaten = 0.0

        score += valueOf(mazeValue)

        dots.onDotEaten(pacman.position)
        maze.onDotEaten(pacman.position)
        ghostBehaviorController.onDotEaten(isEnergizer)
    }

    private fun handleNoDotEaten(time: Double) {
        timeElapsedSinceLastDotEaten += time
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

        document.onkeydown = ::handleKeyboardEvent
    }

    private fun handleKeyboardEvent(event: KeyboardEvent) {

        if (!ALLOWED_KEYS.contains(event.keyCode)) return

        if (gameState == WaitingForPlayer) {
            gameState = Playing

            world.gameStateBanner.isVisible = false
            world.setPacmanAndGhostsActive(true)
        }

        with (world.pacman) {
            when (event.keyCode) {
                KEY_ARROW_UP -> requestDirection(Direction.UP)
                KEY_ARROW_DOWN -> requestDirection(Direction.DOWN)
                KEY_ARROW_LEFT -> requestDirection(Direction.LEFT)
                KEY_ARROW_RIGHT -> requestDirection(Direction.RIGHT)
            }
        }
    }

    fun setup(scene: Scene) {
        world.addRenderablesToScene(scene)
    }

    private fun valueOf(mazeValue: Byte) = when {
        mazeValue.isDot -> 10
        mazeValue.isEnergizer -> 50
        else -> 0
    }
}