package pacman3d.gameobjects

import pacman3d.ext.TextParameters
import pacman3d.ext.plusAssign
import pacman3d.maze.Maze
import pacman3d.state.World
import three.js.Font
import three.js.FontLoader
import three.js.Group

class TextObjects : Renderable {

    companion object {
        const val FONT = "fonts/emulogic.json"
        const val SIZE = Maze.UNIT_SIZE
        const val THICKNESS = 1.0
    }

    private lateinit var textObjects: Array<Renderable>

    override val sceneObject = Group()

    override fun setup(world: World) {
        FontLoader().load(FONT, onLoad = { font -> deferredSetup(font, world) })
    }

    override fun update(world: World, time: Double) {
        if (this::textObjects.isInitialized) textObjects.forEach { it.update(world, time) }
    }

    private fun deferredSetup(font: Font, world: World) {
        val textParams = TextParameters(font, SIZE, THICKNESS)

        textObjects = arrayOf(
            ReadyText(textParams),
            Score(textParams),
        )

        textObjects.forEach {
            it.setup(world)
            sceneObject += it
        }
    }
}