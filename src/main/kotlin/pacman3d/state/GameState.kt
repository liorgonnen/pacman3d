package pacman3d.state

import org.w3c.dom.events.KeyboardEvent
import pacman3d.KEY_ARROW_DOWN
import pacman3d.KEY_ARROW_LEFT
import pacman3d.KEY_ARROW_RIGHT
import pacman3d.KEY_ARROW_UP
import pacman3d.maze.Maze
import pacman3d.maze.MazeCoordinates
import pacman3d.state.Direction.*
import pacman3d.state.GhostId.*
import pacman3d.state.MazeState.Companion.EMPTY
import pacman3d.state.MazeState.Companion.isDot
import pacman3d.state.MazeState.Companion.isDotOrPill
import pacman3d.state.MazeState.Companion.isPill

/**
 * Notes:
 * There are 244 dots overall.
 * 240 small dots x 10 points each
 * 4 energizer dots x 50 points each
 */
class GameState {

    var points = 0
        private set

    val maze = MazeState()

    val pacman = PacmanState(maze, initialPosition = MazeCoordinates(14, 25))

    val ghosts = arrayOf(
        GhostState(id = Blinky, initialPosition = MazeCoordinates(14, 14)),
        GhostState(id = Inky, initialPosition = MazeCoordinates(12, 17)),
        GhostState(id = Pinky, initialPosition = MazeCoordinates(14, 17)),
        GhostState(id = Clyde, initialPosition = MazeCoordinates(16, 17)),
    )

    /**
     * Holds the maze index of the dot or pill that's been eaten in the last update or null otherwise
     */
    var lastEatenDotIndex: Int? = null
        private set

    fun update(time: Double) {
        lastEatenDotIndex = null

        pacman.update(time)

        val tile = maze[pacman.position]
        if (tile.isDotOrPill) {
            if (tile.isDot) points += 10
            if (tile.isPill) points += 50
            maze[pacman.position] = EMPTY
            lastEatenDotIndex = pacman.position.index
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