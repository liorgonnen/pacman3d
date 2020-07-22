package pacman3d.gameobjects

import pacman3d.ext.*
import pacman3d.logic.ActorPosition
import pacman3d.state.GameState
import three.js.Geometry
import three.js.Group
import three.js.Mesh

class Score(private val textParams: TextParameters) : GameObject() {

    companion object {
        private const val COLOR = 0xDFDFFF
    }

    private lateinit var digitGeometries: Array<Geometry>

    private val material = COLOR.toMeshLambertMaterial()

    override val sceneObject = Group().apply {
        val pos = ActorPosition(0.0, 0.0)
        position.set(pos.worldX, 0, pos.worldY)
        rotateX((-40).toRadians())
    }

    override fun setup(state: GameState) {
        digitGeometries = (0..9).map { textGeometry(it.toString(), textParams) }.toTypedArray()
    }

    override fun update(game: GameState, time: Double) = setScore(game.points)

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