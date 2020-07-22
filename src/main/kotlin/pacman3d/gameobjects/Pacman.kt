package pacman3d.gameobjects

import pacman3d.ext.*
import pacman3d.logic.Direction.*
import pacman3d.maze.Maze
import pacman3d.state.GameState
import three.js.Group
import three.js.Mesh
import three.js.SphereBufferGeometry
import kotlin.math.PI

class Pacman : GameObject() {

    companion object {
        private const val SIZE = 1.6 * Maze.UNIT_SIZE
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
    }

    private val insideMaterial = 0x887E29.toMeshLambertMaterial().apply {
        morphTargets = true
        materialSide = FrontSide
    }

    private val insideMesh = Mesh(geometry, insideMaterial)
    private val outsideMesh = Mesh(geometry, outsideMaterial)

    override val sceneObject = Group().add(insideMesh, outsideMesh).apply {
        rotateX(-HALF_PI)
    }

    override fun setup(state: GameState) {
        sceneObject.position.set(state.pacman.position.worldX, SIZE / 2, state.pacman.position.worldY)
    }

    override fun update(state: GameState, time: Double) = with (state.pacman) {
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

        sceneObject.position.x = position.worldX
        sceneObject.position.z = position.worldY

        sceneObject.setRotationFromAxisAngle(Y_AXIS, when (state.pacman.currentDirection) {
            DOWN -> 1.5 * PI
            RIGHT -> 0
            UP -> PI / 2
            LEFT -> PI
        })
    }
}