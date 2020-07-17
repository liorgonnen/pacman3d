package pacman3d.state

import pacman3d.ext.compareTo
import pacman3d.ext.plus
import pacman3d.logic.Direction
import pacman3d.logic.Direction.*
import pacman3d.maze.MazeCoordinates
import pacman3d.maze.setFromMazeCoordinates
import three.js.Vector2

abstract class BaseState(internal val position: MazeCoordinates) {

    companion object {
        private const val SUBSTEP_MIN = -0.5
        private const val SUBSTEP_MAX = 0.5
        private const val SUBSTEP_CENTER = 0.0

        // Cornering is the technique of moving the joystick in the direction one wishes to go well before reaching the
        // center of a turn, ensuring Pac-Man will take the turn as quickly as possible.
        //
        private const val CORNERING_THRESHOLD = 0.1 // Must be between 0 - SUBSTEP_MAX
    }

    protected val subStepPosition = Vector2(0, 0)

    val worldPosition = position.ml.add(subStepPosition)

    var direction: Direction = DOWN

    var speed = 5.0
        protected set

    open fun update(gameState: GameState, time: Double) {
        updatePosition(gameState.maze, time)
    }

    protected open fun updatePosition(maze: MazeState, time: Double) {
        val distance = speed * time * direction.multiplier
        val isNextTileAvailable = maze.isTileValidInDirection(position, direction)

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

        // TODO: Move this to the game object so that the state is completely decoupled from any rendering specifics
        worldPosition.setFromMazeCoordinates(position, subStepPosition)
    }
}