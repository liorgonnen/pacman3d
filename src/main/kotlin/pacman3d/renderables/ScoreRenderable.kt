package pacman3d.renderables

import pacman3d.assets.AssetLoader
import pacman3d.ext.*
import pacman3d.logic.Position
import pacman3d.maze.MazeConst
import pacman3d.entities.Score
import pacman3d.entities.World
import three.js.Geometry
import three.js.Group
import three.js.Mesh

class ScoreRenderable(private val score: Score) : Renderable {

    companion object {
        private const val COLOR = 0xDFDFFF
        private const val SIZE = MazeConst.UNIT_SIZE
        private const val THICKNESS = 1.0
    }

    private lateinit var digitGeometries: Array<Geometry>

    private val material = COLOR.toMeshLambertMaterial()

    override val sceneObject = Group().apply {
        val pos = Position(0.0, 0.0)
        position.set(pos.worldX, 0, pos.worldY)
        rotateX((-40).toRadians())
    }

    override fun setup(world: World) = AssetLoader.onFontLoaded { font ->
        val textParams = TextParameters(font, SIZE, THICKNESS)
        digitGeometries = (0..9).map { textGeometry(it.toString(), textParams) }.toTypedArray()
    }

    override fun update(world: World, time: Double) {
        if (this::digitGeometries.isInitialized) setScore(score.points)
    }

    private fun setScore(points: Int) {
        var value = points
        var xPos = 0.0
        var power = 10

        while (power * 10 <= value) power *= 10

        sceneObject.apply { while (children.isNotEmpty()) remove(children[0]) }

        do {
            val digit = Mesh(digitGeometries[value / power], material).apply { position.x = xPos }
            value %= power
            power /= 10

            sceneObject.add(digit)

            xPos += digit.boundingBox.max.x.toDouble()
        }
        while (power > 0)
    }
}