package pacman3d.state

import org.w3c.dom.events.KeyboardEvent
import pacman3d.KEY_ARROW_DOWN
import pacman3d.KEY_ARROW_LEFT
import pacman3d.KEY_ARROW_RIGHT
import pacman3d.KEY_ARROW_UP
import pacman3d.logic.Position
import pacman3d.logic.Direction.*
import pacman3d.logic.GhostId.*
import pacman3d.state.MazeState.Companion.isDot
import pacman3d.state.MazeState.Companion.isDotOrPill
import pacman3d.state.MazeState.Companion.isPill

/**
 * Notes:
 * There are 244 dots overall.
 * 240 small dots x 10 points each
 * 4 energizer dots x 50 points each
 */
class World {

    var points = 0
        private set

    val maze = MazeState()

    val pacman = PacmanState(
        initialDirection = RIGHT,
        initialPosition = Position(13.5, 26.5)
    )

    val ghosts = arrayOf(
        // Order matters
        GhostState(
            id = Blinky,
            initialDirection = LEFT,
            initialPosition = Position(14.0, 14.0),
            scatterTargetTile = Position(26, 0)
        ),
        GhostState(
            id = Inky,
            initialDirection = UP,
            initialPosition = Position(12.0, 17.0),
            scatterTargetTile = Position(27, 35),
        ),
        GhostState(
            id = Pinky,
            initialDirection = DOWN,
            initialPosition = Position(14.0, 18.0),
            scatterTargetTile = Position(1, 0)
        ),
        GhostState(
            id = Clyde,
            initialDirection = UP,
            initialPosition = Position(16.0, 17.0),
            scatterTargetTile = Position(0, 35)
        ),
    )

    init {
        setup()
    }

    private fun setup() {
        pacman.setup(this)
        ghosts.forEach { it.setup(this) }
    }

    /**
     * Holds the maze index of the dot or pill that's been eaten in the last update or null otherwise
     */
    var lastEatenDotIndex: Int? = null
        private set

    fun update(time: Double) {
        lastEatenDotIndex = null

        pacman.update(this, time)
        ghosts.forEach { it.update(this, time) }

        val tile = maze[pacman.position]
        if (tile.isDotOrPill) {
            if (tile.isDot) points += 10
            if (tile.isPill) points += 50
            maze.eatDot(pacman.position)

            lastEatenDotIndex = pacman.position.mazeIndex
        }
    }

    // TODO: This is just temporarily here to test things out.
    fun keyboardHandler(event: KeyboardEvent) {
        when (event.keyCode) {
            KEY_ARROW_UP ->  pacman.requestDirection(UP)
            KEY_ARROW_DOWN -> pacman.requestDirection(DOWN)
            KEY_ARROW_LEFT -> pacman.requestDirection(LEFT)
            KEY_ARROW_RIGHT -> pacman.requestDirection(RIGHT)
        }
    }
}