package pacman3d.state

import pacman3d.maze.MazeCoordinates


enum class GhostId {
    Blinky,
    Pinky,
    Inky,
    Clyde
}

class GhostState(val id: GhostId, initialPosition: MazeCoordinates) : BaseState(initialPosition) {

}