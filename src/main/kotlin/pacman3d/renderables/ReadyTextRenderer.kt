package pacman3d.renderables

import pacman3d.assets.AssetLoader
import pacman3d.ext.*
import pacman3d.logic.Position
import pacman3d.maze.MazeConst
import pacman3d.entities.World
import three.js.Group
import three.js.Mesh

class ReadyTextRenderer : Renderable {

    companion object {
        private const val COLOR = 0xFFFF00
        private const val SIZE = MazeConst.UNIT_SIZE
        private const val THICKNESS = 1.0
        private const val TEXT = "READY!"
    }

    override val sceneObject = Group()

    override fun setup(world: World) = AssetLoader.onFontLoaded { font ->
        val textParams = TextParameters(font, SIZE, THICKNESS)

        sceneObject += Mesh(textGeometry(TEXT, textParams), COLOR.toMeshLambertMaterial()).apply {
            val pos = Position(14.0, 20.0)
            position.set(-boundingBox.max.x.toDouble() / 2 + pos.worldX, -boundingBox.min.y.toDouble(), pos.worldY)
            rotateX((-40).toRadians())
        }
    }

    override fun update(world: World, time: Double) = Unit
}