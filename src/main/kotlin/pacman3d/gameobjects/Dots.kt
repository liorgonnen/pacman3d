package pacman3d.gameobjects

import pacman3d.ext.toMeshLambertMaterial
import pacman3d.ext.toMeshPhongMaterial
import pacman3d.maze.Maze
import pacman3d.maze.MazeCoordinates
import pacman3d.state.GameState
import pacman3d.state.MazeState.Companion.isDot
import pacman3d.state.MazeState.Companion.isDotOrPill
import three.js.*

class Dots : GameObject() {

    companion object {
        const val DOT_SIZE = 0.2 * Maze.UNIT_SIZE
        const val PILL_SIZE = 1.0 * Maze.UNIT_SIZE
        const val DOT_Y_POSITION = 0.5

        const val NUM_DOTS = 240
        const val NUM_PILLS = 4
        const val COUNT = NUM_DOTS + NUM_PILLS
    }

    private val dotGeometry = BoxGeometry(DOT_SIZE, DOT_SIZE, DOT_SIZE)
    private val dotMaterial = 0xF5BCB2.toMeshLambertMaterial()
    private val dotMesh = InstancedMesh(dotGeometry, dotMaterial, NUM_DOTS)

    private val pillGeometry = SphereGeometry(PILL_SIZE / 2, 16, 16)
    private val pillMaterial = 0xF5BCB2.toMeshLambertMaterial()
    private val pillMesh = InstancedMesh(pillGeometry, pillMaterial, NUM_PILLS)

    override val sceneObject = Group().apply { add(dotMesh, pillMesh) }

    override fun setup(state: GameState) {
        var dotIndex = 0
        var pillIndex = 0
        state.maze.forEachTile { maze, x, y ->
            if (maze[x, y].isDotOrPill) {
                val position = MazeCoordinates(x, y).mm
                val matrix = Matrix4().makeTranslation(position.x, DOT_Y_POSITION, position.y)
                if (maze[x, y].isDot) dotMesh.setMatrixAt(dotIndex++, matrix) else pillMesh.setMatrixAt(pillIndex++, matrix)
            }
        }
    }

    override fun update(state: GameState, time: Double) {

    }
}