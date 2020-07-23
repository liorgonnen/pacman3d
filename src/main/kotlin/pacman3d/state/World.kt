package pacman3d.state

import org.w3c.dom.events.KeyboardEvent
import pacman3d.ext.plusAssign
import pacman3d.gameobjects.GameEntity
import pacman3d.logic.Position
import pacman3d.logic.Direction.*
import pacman3d.logic.GhostId.*
import three.js.Scene

/**
 * Notes:
 * There are 244 dots overall.
 * 240 small dots x 10 points each
 * 4 energizer dots x 50 points each
 */
class World {

    val maze = Maze()

    val dots = Dots()

    val score = Score()

    val pacman = PacMan(
        initialDirection = RIGHT,
        initialPosition = Position(13.5, 26.5)
    )

    private val entities = arrayOf<GameEntity>(
        maze,
        dots,
        score,
        pacman,
        Ghost(
                id = Blinky,
                color = 0xFE0000,
                initialDirection = LEFT,
                initialPosition = Position(14.0, 14.0),
                scatterTargetTile = Position(26, 0)
        ),
        Ghost(
                id = Inky,
                color = 0x00D4D4,
                initialDirection = UP,
                initialPosition = Position(12.0, 17.0),
                scatterTargetTile = Position(27, 35),
        ),
        Ghost(
                id = Pinky,
                color = 0xFFBBFF,
                initialDirection = DOWN,
                initialPosition = Position(14.0, 18.0),
                scatterTargetTile = Position(1, 0)
        ),
        Ghost(
                id = Clyde,
                color = 0xFFB950,
                initialDirection = UP,
                initialPosition = Position(16.0, 17.0),
                scatterTargetTile = Position(0, 35)
        ),
    )

    init {
        setup()
    }

    private fun setup() {
        entities.forEach { it.setup(this) }
    }

    fun addRenderablesToScene(scene: Scene) {
        entities.forEach { scene += it.renderable }
    }

    fun update(time: Double) {
        entities.forEach { it.update(this, time) }

        entities.forEach { it.renderable.update(this, time) }

        // TODO: Create points game entity that has the score as its renderable

    }

    // TODO: This is just temporarily here to test things out.
    fun keyboardHandler(event: KeyboardEvent) {
//        when (event.keyCode) {
//            KEY_ARROW_UP ->  pacman.requestDirection(UP)
//            KEY_ARROW_DOWN -> pacman.requestDirection(DOWN)
//            KEY_ARROW_LEFT -> pacman.requestDirection(LEFT)
//            KEY_ARROW_RIGHT -> pacman.requestDirection(RIGHT)
//        }
    }
}