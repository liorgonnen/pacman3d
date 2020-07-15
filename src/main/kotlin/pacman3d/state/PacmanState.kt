package pacman3d.state

import pacman3d.ext.absValue
import pacman3d.ext.compareTo
import pacman3d.ext.plus
import pacman3d.maze.MazeCoordinates
import pacman3d.maze.setFromMazeCoordinates
import pacman3d.state.Direction.*
import pacman3d.state.MazeState.Companion.isValid
import three.js.Vector2

sealed class Direction(val multiplier: Double) {
    object UP : Direction(-1.0)
    object DOWN : Direction(1.0)
    object LEFT : Direction(-1.0)
    object RIGHT : Direction(1.0)

    val isHorizontal get() = this == LEFT || this == RIGHT
    val isVertical get() = this == UP || this == DOWN
}

class PacmanState(val maze: MazeState) {

    companion object {
        private const val SUBSTEP_MIN = -0.5
        private const val SUBSTEP_MAX = 0.5
        private const val SUBSTEP_CENTER = 0.0

        // Cornering is the technique of moving the joystick in the direction one wishes to go well before reaching the
        // center of a turn, ensuring Pac-Man will take the turn as quickly as possible.
        //
        private const val CORNERING_THRESHOLD = 0.2 // Must be between 0 - SUBSTEP_MAX
    }

    private val speed = 2.0

    private val position = MazeCoordinates(13, 26)

    private val subStepPosition = Vector2(0.0, 0)

    private var requestedDirection: Direction = DOWN

    var direction: Direction = DOWN

    val worldPosition = position.mm.add(subStepPosition)

    private fun isInCorneringThreshold(threshold: Double) =
        if (direction.isHorizontal) subStepPosition.y.absValue <= threshold
        else subStepPosition.x.absValue <= threshold

    internal fun requestDirection(requestedDirection: Direction) {
        this.requestedDirection = requestedDirection

        if (requestedDirection == direction) return

        // When the player is pressing the arrow key at real time, we allow them to do pre-turns
        // and post-turns
        maybeUpdateDirection(CORNERING_THRESHOLD)
    }

    private fun isTileValidInDirection(direction: Direction) = when (direction) {
        UP -> maze[position.x, position.y - 1].isValid
        DOWN -> maze[position.x, position.y + 1].isValid
        LEFT -> maze[position.x - 1, position.y].isValid
        RIGHT -> maze[position.x + 1, position.y].isValid
    }

    private fun maybeUpdateDirection(corneringThreshold: Double) {
        if (direction == requestedDirection ||
            !isInCorneringThreshold(corneringThreshold) ||
            !isTileValidInDirection(requestedDirection)) return

        direction = requestedDirection
        if (direction.isHorizontal) subStepPosition.y = 0 else subStepPosition.x = 0
    }

    fun update(time: Double) {
        // If the player pressed the arrow keys way ahead of time, we don't allow any cornering
        maybeUpdateDirection(0.0)

        val distance = speed * time * direction.multiplier
        val isNextTileAvailable = isTileValidInDirection(direction)

        if (direction.isHorizontal) subStepPosition.x += distance else subStepPosition.y += distance

        when (direction) {
            UP -> {
                if (subStepPosition.y < SUBSTEP_CENTER) {
                    if (isNextTileAvailable) {
                        if (subStepPosition.y < SUBSTEP_MIN) {
                            position.y--
                            subStepPosition.y = SUBSTEP_MAX
                        }
                    }
                    else {
                        subStepPosition.y = SUBSTEP_CENTER
                    }
                }
            }

            DOWN -> {
                if (subStepPosition.y > SUBSTEP_CENTER) {
                    if (isNextTileAvailable) {
                        if (subStepPosition.y > SUBSTEP_MAX) {
                            position.y++
                            subStepPosition.y = SUBSTEP_MIN
                        }
                    }
                    else {
                        subStepPosition.y = SUBSTEP_CENTER
                    }
                }
            }

            LEFT -> {
                if (subStepPosition.x < SUBSTEP_CENTER) {
                    if (isNextTileAvailable) {
                        if (subStepPosition.x < SUBSTEP_MIN) {
                            position.x--
                            subStepPosition.x = SUBSTEP_MAX
                        }
                    }
                    else {
                        subStepPosition.x = SUBSTEP_CENTER
                    }
                }
            }

            RIGHT -> {
                if (subStepPosition.x > SUBSTEP_CENTER) {
                    if (isNextTileAvailable) {
                        if (subStepPosition.x > SUBSTEP_MAX) {
                            position.x++
                            subStepPosition.x = SUBSTEP_MIN
                        }
                    }
                    else {
                        subStepPosition.x = SUBSTEP_CENTER
                    }
                }
            }
        }

        // TODO: Move this to the Pacman game object so that the state is completely decoupled from any rendering
        // specifics
        worldPosition.setFromMazeCoordinates(position, subStepPosition)
    }
}