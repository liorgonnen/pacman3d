package pacman3d.gameobjects

import pacman3d.ext.*
import pacman3d.logic.Position
import pacman3d.state.World
import three.js.Group
import three.js.Mesh

class ReadyText(private val textParams: TextParameters) : Renderable {

    companion object {
        const val COLOR = 0xFFFF00
    }

    override val sceneObject = Group()

    override fun setup(world: World) {
        sceneObject += Mesh(textGeometry("Ready!", textParams), COLOR.toMeshLambertMaterial()).apply {
            val pos = Position(14.0, 20.0)
            position.set(-boundingBox.max.x.toDouble() / 2 + pos.worldX, -boundingBox.min.y.toDouble(), pos.worldY)
            rotateX((-40).toRadians())
        }
    }

    override fun update(world: World, time: Double) = Unit
}