package pacman3d.state

import pacman3d.ext.compareTo
import pacman3d.ext.minus
import pacman3d.ext.plus
import pacman3d.maze.Maze.Pos.setFromMazeCoordinates
import three.js.Vector2

sealed class Direction {
    object UP : Direction()
    object DOWN : Direction()
    object LEFT : Direction()
    object RIGHT : Direction()
}

class PacmanState {

    private val speed = 1.0

    internal var direction: Direction = Direction.RIGHT

    private val mazePosition = Vector2(13, 26)

    private val subStepPosition = Vector2(0.5, 0)

    val position = Vector2().setFromMazeCoordinates(mazePosition, subStepPosition)

    fun update(time: Double) {
        val distance = speed * time
        when (direction) {
            Direction.UP -> {
                subStepPosition.y -= distance
            }

            Direction.DOWN -> {
                subStepPosition.y += distance
            }

            Direction.LEFT -> {
                subStepPosition.x -= distance
            }

            Direction.RIGHT -> {
                subStepPosition.x += distance
                if (subStepPosition.x >= 0.5) {
                    //if (Maze.)
                }
            }
        }

        position.setFromMazeCoordinates(mazePosition, subStepPosition)
    }
}