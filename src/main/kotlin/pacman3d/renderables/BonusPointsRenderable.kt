package pacman3d.renderables

import pacman3d.assets.AssetLoader
import pacman3d.entities.World
import pacman3d.ext.*
import pacman3d.logic.Position
import pacman3d.maze.MazeConst
import three.js.Group
import three.js.Mesh
import three.js.Object3D

class BonusPointsRenderable : Renderable {

    companion object {
        private const val SIZE = MazeConst.UNIT_SIZE * 0.8
        private const val THICKNESS = 0.1
        private const val COLOR = 0x20CAC7

        private val MATERIAL = COLOR.toMeshLambertMaterial()

        private val POINT_VALUES = arrayOf(
            100,    // Level 1 Cherries
            200,    // 1st eaten ghost
            300,    // Level 2 Strawberry
            400,    // 2nd eaten ghost
            500,    // Levels 3,4 Peach
            700,    // Levels 5,6 Apple
            800,    // 3rd eaten ghost
            1000,   // Levels 7,8 Grapes
            1600,   // 4th eaten ghost
            2000,   // Level 9,10 Galaxian
            3000,   // Levels 11,12 Bell
            5000,   // Levels 13+ Key
        )
    }

    private var currentActivePoints: Int? = null

    private lateinit var pointsObjects: Map<Int, Mesh>

    override val sceneObject = Group().apply { visible = false }

    override fun setup(world: World) = AssetLoader.onFontLoaded { font ->
        val textParams = TextParameters(font, SIZE, THICKNESS)
        pointsObjects = POINT_VALUES.map { points -> points to createPointsMesh(points, textParams) }.toMap()

        sceneObject += pointsObjects.values
    }

    private fun createPointsMesh(points: Int, textParams: TextParameters)
        = Mesh(textGeometry(points.toString(), textParams), MATERIAL).apply { visible = false }

    override fun update(world: World, time: Double) = Unit

    fun show(points: Int, where: Position) {
        if (!this::pointsObjects.isInitialized) return

        currentActivePoints?.let { pointsObjects.getValue(it).visible = false }
        currentActivePoints = points

        pointsObjects.getValue(points).apply {
            //position.set(where.x - boundingBox.max.x.toDouble() / 2, -boundingBox.min.y.toDouble(), where.y)
            position.set(where.x, 2.0, where.y)
            visible = true
        }

        sceneObject.visible = true
    }

    fun hide() {
        if (!this::pointsObjects.isInitialized) return

        currentActivePoints?.let { pointsObjects.getValue(it).visible = false }
        currentActivePoints = null
        sceneObject.visible = false
    }
}