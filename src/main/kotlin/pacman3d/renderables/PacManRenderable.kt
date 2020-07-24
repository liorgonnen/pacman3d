package pacman3d.renderables

import pacman3d.ext.*
import pacman3d.logic.Direction
import pacman3d.maze.MazeConst
import pacman3d.entities.PacMan
import pacman3d.entities.World
import three.js.Group
import three.js.Mesh
import three.js.SphereBufferGeometry
import kotlin.math.PI

class PacManRenderable(private val entity: PacMan) : Renderable {

    companion object {
        private const val SIZE = 1.6 * MazeConst.UNIT_SIZE
        private const val MAX_MOUTH_ANGLE = 90.0 * PI / 180
        private const val SEGMENTS = 16
    }

    private val mouthOpenGeometry = SphereBufferGeometry(
        radius = SIZE / 2,
        widthSegments = SEGMENTS,
        heightSegments = SEGMENTS,
        phiStart = MAX_MOUTH_ANGLE / 2,
        phiLength = TWO_PI - MAX_MOUTH_ANGLE,
        thetaStart = PI,
        thetaLength = PI,
    ).apply {
        rotateX(-HALF_PI)
        computeVertexNormals()
    }

    private val geometry = SphereBufferGeometry(
            radius = SIZE / 2,
            widthSegments = SEGMENTS,
            heightSegments = SEGMENTS,
            phiStart = 0,
            phiLength = TWO_PI,
            thetaStart = PI,
            thetaLength = PI,
    ).apply {
        morphAttributes.asDynamic().position = arrayOf(mouthOpenGeometry.getAttribute("position"))
        rotateX(-HALF_PI)
        computeVertexNormals()
    }

    private var mouthOpenSpeed = 0.15
    private var mouthOpenInfluence = 0.0 // range: 0 - 1.0

    private val outsideMaterial = 0xFFFE54.toMeshLambertMaterial().apply {
        morphTargets = true
        materialSide = BackSide
        transparent = true
    }

    private val insideMaterial = 0x887E29.toMeshLambertMaterial().apply {
        morphTargets = true
        materialSide = FrontSide
        transparent = true
    }

    private val insideMesh = Mesh(geometry, insideMaterial)
    private val outsideMesh = Mesh(geometry, outsideMaterial)

    override val sceneObject = Group().add(insideMesh, outsideMesh).apply {
        rotateX(-HALF_PI)
    }

    override fun setup(world: World) {
        sceneObject.position.set(entity.position.worldX, SIZE / 2, entity.position.worldY)
    }

    override fun update(world: World, time: Double) = with (entity) {
        mouthOpenInfluence += mouthOpenSpeed
        if (mouthOpenInfluence >= 1.0) {
            mouthOpenInfluence = 1.0
            mouthOpenSpeed = -mouthOpenSpeed
        }
        else if (mouthOpenInfluence <= 0) {
            mouthOpenInfluence = 0.0
            mouthOpenSpeed = -mouthOpenSpeed
        }

        insideMesh.morphTargetInfluences[0] = mouthOpenInfluence
        outsideMesh.morphTargetInfluences[0] = mouthOpenInfluence


        val opacity = when {
            position.x < 2.0 -> position.x - 1
            position.x >= MazeConst.WIDTH_UNITS - 2 -> MazeConst.WIDTH_UNITS - 1 - position.x
            else -> 1.0
        }

        if (opacity != 1.0) console.log(opacity)

        outsideMaterial.opacity = opacity
        insideMaterial.opacity = opacity

        sceneObject.position.x = position.worldX
        sceneObject.position.z = position.worldY

        sceneObject.setRotationFromAxisAngle(Y_AXIS, when (entity.currentDirection) {
            Direction.DOWN -> 1.5 * PI
            Direction.RIGHT -> 0
            Direction.UP -> PI / 2
            Direction.LEFT -> PI
        })
    }
}