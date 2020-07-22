package pacman3d.gameobjects

import pacman3d.ext.TextParameters
import pacman3d.ext.plusAssign
import pacman3d.maze.Maze
import pacman3d.state.GameState
import three.js.Font
import three.js.FontLoader
import three.js.Group

class TextObjects : GameObject() {

    companion object {
        const val FONT = "fonts/emulogic.json"
        const val SIZE = Maze.UNIT_SIZE
        const val THICKNESS = 1.0
    }

    private lateinit var textObjects: Array<GameObject>

    override val sceneObject = Group()

    override fun setup(state: GameState) {
        FontLoader().load(FONT, onLoad = { font -> deferredSetup(font, state) })
    }

    override fun update(state: GameState, time: Double) {
        if (this::textObjects.isInitialized) textObjects.forEach { it.update(state, time) }
    }

    private fun deferredSetup(font: Font, state: GameState) {
        val textParams = TextParameters(font, SIZE, THICKNESS)

        textObjects = arrayOf(
            ReadyText(textParams),
            Score(textParams),
        )

        textObjects.forEach {
            it.setup(state)
            sceneObject += it
        }
    }
}