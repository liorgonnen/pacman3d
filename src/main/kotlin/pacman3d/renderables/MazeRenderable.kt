package pacman3d.renderables

import pacman3d.ext.HALF_PI
import pacman3d.ext.toMeshLambertMaterial
import pacman3d.maze.MazeConst
import pacman3d.maze.MazeGeometryBuilder
import pacman3d.entities.World
import three.js.Group
import three.js.Mesh
import three.js.PlaneGeometry

class MazeRenderable : Renderable {

    private val maze = Mesh(MazeGeometryBuilder().build(), 0x151FCD.toMeshLambertMaterial())

    private val plane = Mesh(PlaneGeometry(MazeConst.VISIBLE_WIDTH, MazeConst.EFFECTIVE_LENGTH, 5), 0.toMeshLambertMaterial()).apply {
        rotateX(-HALF_PI)
    }

    override val sceneObject = Group().add(plane, maze)

    override fun setup(world: World) = Unit
}