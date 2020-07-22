package pacman3d.gameobjects

import pacman3d.ext.*
import pacman3d.logic.ActorPosition
import pacman3d.state.GameState
import three.js.*

class ReadyText(private val textParams: TextParameters) : GameObject() {

    companion object {
        const val COLOR = 0xFFFF00
    }

    override val sceneObject = Group()

    override fun setup(state: GameState) {
        sceneObject += Mesh(textGeometry("Ready!", textParams), COLOR.toMeshLambertMaterial()).apply {
            val pos = ActorPosition(14.0, 20.0)
            position.set(-boundingBox.max.x.toDouble() / 2 + pos.worldX, -boundingBox.min.y.toDouble(), pos.worldY)
            rotateX((-40).toRadians())
        }
    }

    override fun update(state: GameState, time: Double) = Unit
}