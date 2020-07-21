package pacman3d.gameobjects

import pacman3d.ext.*
import pacman3d.logic.ActorPosition
import pacman3d.maze.Maze
import pacman3d.state.GameState
import three.js.*

class ReadyText : GameObject() {

    companion object {
        const val FONT = "fonts/emulogic.json"
    }

    init {
        FontLoader().apply { load(FONT, onLoad = ::onFontLoaded) }
    }

    override val sceneObject = Group()

    override fun setup(state: GameState) = Unit

    override fun update(state: GameState, time: Double) = Unit

    private fun onFontLoaded(font: Font) {
        sceneObject += Mesh(createTextGeometry("Ready!", font, Maze.UNIT_SIZE, 1.0), 0xffff00.toMeshPhongMaterial()).apply {
            val pos = ActorPosition(14.0, 20.0)
            position.set(-boundingBox.max.x.toDouble() / 2 + pos.worldX, -boundingBox.min.y.toDouble(), pos.worldY)
            rotateX((-40).toRadians())
        }
    }

    private fun createTextGeometry(text: String, font: Font, size: Double, thickness: Double)
        = TextGeometry(text, TextParameters(font, size, thickness)).apply {
            computeFaceNormals()
            computeFlatVertexNormals()
            computeBoundingBox()
        }
}