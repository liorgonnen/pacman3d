package pacman3d.state

import pacman3d.maze.Maze
import pacman3d.maze.Maze.DOT
import pacman3d.maze.Maze.EMPTY
import pacman3d.maze.Maze.PILL

class MazeState {

    private val Byte.isDot get() = this == DOT || this == PILL

    private val state = Maze.createDefaultState()

    private var dotsLeft = state.count { it.isDot }

    fun isValid(x: Int, y: Int) = state[y * Maze.WIDTH_UNITS + x] != EMPTY
}