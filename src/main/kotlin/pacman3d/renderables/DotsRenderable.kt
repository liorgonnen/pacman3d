package pacman3d.renderables

import pacman3d.ext.plusAssign
import pacman3d.ext.toMeshLambertMaterial
import pacman3d.logic.Position
import pacman3d.maze.MazeConst
import pacman3d.entities.Dots
import pacman3d.entities.Maze.Companion.isDot
import pacman3d.entities.Maze.Companion.isDotOrEnergizer
import pacman3d.entities.World
import three.js.BoxGeometry
import three.js.Group
import three.js.Mesh
import three.js.SphereGeometry
import kotlin.collections.set

class DotsRenderable(private val dots: Dots) : Renderable {

    companion object {
        const val DOT_COLOR = 0xF5BCB2
        const val DOT_SIZE = 0.2 * MazeConst.UNIT_SIZE
        const val ENERGIZER_SIZE = 1.0 * MazeConst.UNIT_SIZE
        const val ENERGIZER_SEGMENTS = 16
        const val DOT_Y_POSITION = 0.5

        const val NUM_DOTS = 240
        const val NUM_ENERGIZERS = 4
        const val COUNT = NUM_DOTS + NUM_ENERGIZERS
    }

    private val dotGeometry = BoxGeometry(DOT_SIZE, DOT_SIZE, DOT_SIZE)
    private val dotMaterial = DOT_COLOR.toMeshLambertMaterial()

    private val energizerGeometry = SphereGeometry(ENERGIZER_SIZE / 2, ENERGIZER_SEGMENTS, ENERGIZER_SEGMENTS)

    private val group = Group()

    override val sceneObject = group

    private val mazeCoordinatesToDotMap = mutableMapOf<Int, Mesh>()

    override fun setup(world: World) {
        world.maze.forEachTile { maze, x, y ->
            if (maze[x, y].isDotOrEnergizer) {
                group += Mesh(if (maze[x, y].isDot) dotGeometry else energizerGeometry, dotMaterial).apply {
                    val pos = Position(x, y)
                    position.set(x = pos.worldX, z = pos.worldY, y = DOT_Y_POSITION)
                    mazeCoordinatesToDotMap[MazeConst.indexOf(x, y)] = this
                }
            }
        }
    }

    override fun update(world: World, time: Double) {
        dots.lastEatenIndex?.let { index ->
            mazeCoordinatesToDotMap.remove(index)?.let { group.remove(it) }
        }
    }
}