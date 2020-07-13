package pacman3d.dots

import pacman3d.ext.toMeshPhongMaterial
import pacman3d.maze.Maze
import pacman3d.maze.Maze.DOT_SIZE
import pacman3d.maze.Maze.PILL_SIZE
import pacman3d.maze.Maze.Pos.mm
import pacman3d.primitives.GameObject
import pacman3d.state.GameState
import three.js.*

class Dots : GameObject() {

    companion object {
        const val NUM_DOTS = 240
        const val NUM_PILLS = 4
        const val COUNT = NUM_DOTS + NUM_PILLS
    }

    private val dotGeometry = BoxGeometry(DOT_SIZE, DOT_SIZE, DOT_SIZE)
    private val dotMaterial = 0xF5BCB2.toMeshPhongMaterial()
    private val dotMesh = InstancedMesh(dotGeometry, dotMaterial, NUM_DOTS)

    private val pillGeometry = SphereGeometry(PILL_SIZE / 2, 16, 16)
    private val pillMaterial = 0xF5BCB2.toMeshPhongMaterial()
    private val pillMesh = InstancedMesh(pillGeometry, pillMaterial, NUM_PILLS)

    override val sceneObject = Group().apply { add(dotMesh, pillMesh) }

    override fun setup(state: GameState) {
        var dotIndex = 0
        var pillIndex = 0
        state.maze.forEachTile { maze, x, y ->
            if (maze.isDotOrPill(x, y)) {
                val position = Maze.Pos[x, y].mm
                val matrix = Matrix4().makeTranslation(position.x, 0.6, position.y)
                if (maze.isDot(x, y)) dotMesh.setMatrixAt(dotIndex++, matrix) else pillMesh.setMatrixAt(pillIndex++, matrix)
            }
        }
    }

    override fun update(state: GameState) {

    }
}