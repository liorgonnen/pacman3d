package pacman3d.dots

import pacman3d.ext.toMeshPhongMaterial
import pacman3d.maze.Maze
import pacman3d.maze.Maze.DOT_SIZE
import pacman3d.maze.Maze.Pos.mm
import pacman3d.primitives.GameObject
import pacman3d.state.GameState
import three.js.BoxGeometry
import three.js.Group
import three.js.InstancedMesh
import three.js.Matrix4

class Dots : GameObject() {

    companion object {
        const val NUM_DOTS = 240
        const val NUM_PILLS = 4
        const val COUNT = NUM_DOTS + NUM_PILLS
    }

    private val dotGeometry = BoxGeometry(DOT_SIZE, DOT_SIZE, DOT_SIZE)

    private val dotMaterial = 0xffffff.toMeshPhongMaterial()

    private val dotMesh = InstancedMesh(dotGeometry, dotMaterial, NUM_DOTS)

    override val sceneObject = Group().apply {
        add(dotMesh)
    }

    override fun setup(state: GameState) {
        var index = 0
        state.maze.forEachTile { maze, x, y ->
            if (maze.isDot(x, y)) {
                val position = Maze.Pos[x, y].mm
                dotMesh.setMatrixAt(index++, Matrix4().apply { makeTranslation(position.x, 0.5, position.y) })
            }
        }
    }

    override fun update(state: GameState) {

    }
}