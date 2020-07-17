package pacman3d.state

import pacman3d.maze.MazeCoordinates
import three.js.Vector2

abstract class BaseState(internal val position: MazeCoordinates) {

    protected val subStepPosition = Vector2(0, 0)

    val worldPosition = position.ml.add(subStepPosition)
}