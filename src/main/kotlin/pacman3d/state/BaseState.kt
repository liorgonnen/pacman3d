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
    }

    protected val subStepPosition = Vector2(0, 0)

    val worldPosition = position.ml.add(subStepPosition)

    var direction: Direction = DOWN
        set(value) {
            if (field == value) return
            field = value
            if (value.isHorizontal) subStepPosition.y = 0 else subStepPosition.x = 0
            onDirectionChanged()
        }

    var speed = 5.0
        protected set

    open fun reset(gameState: GameState) = Unit

    open fun update(gameState: GameState, time: Double) {
        val mazePositionChanged = updatePosition(gameState.maze, time)
        if (mazePositionChanged) onMazePositionChanged(gameState, time)
    }

    protected open fun onMazePositionChanged(gameState: GameState, time: Double) = Unit

    protected open fun onDirectionChanged() = Unit

    protected open fun updatePosition(maze: MazeState, time: Double): Boolean {
        val distance = speed * time * direction.multiplier
        val isNextTileAvailable = maze.isTileValidInDirection(position, direction)
        var mazePositionChanged = false

        if (direction.isHorizontal) subStepPosition.x += distance else subStepPosition.y += distance

        when (direction) {
            UP -> {
                if (subStepPosition.y < SUBSTEP_CENTER) {
                    if (isNextTileAvailable) {
                        if (subStepPosition.y < SUBSTEP_MIN) {
                            position.y--
                            subStepPosition.y = SUBSTEP_MAX
                            mazePositionChanged = true
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
                            mazePositionChanged = true
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
                            mazePositionChanged = true
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
                            mazePositionChanged = true
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

        return mazePositionChanged
    }
}