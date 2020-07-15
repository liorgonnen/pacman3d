package pacman3d.state

import org.w3c.dom.events.KeyboardEvent
import pacman3d.KEY_ARROW_DOWN
import pacman3d.KEY_ARROW_LEFT
import pacman3d.KEY_ARROW_RIGHT
import pacman3d.KEY_ARROW_UP
import pacman3d.state.Direction.*

/**
 * Notes:
 * There are 244 dots overall.
 * 240 small dots x 10 points each
 * 4 energizer dots x 50 points each
 */
class GameState {

    val maze = MazeState()

    val pacman = PacmanState(maze)

    fun update(time: Double) {
        pacman.update(time)
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