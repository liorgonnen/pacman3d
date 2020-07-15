package pacman3d.gameobjects

import pacman3d.ext.Z_AXIS
import pacman3d.ext.toMeshPhongMaterial
import pacman3d.maze.Maze
import pacman3d.state.Direction.*
import pacman3d.state.GameState
import three.js.Mesh
import three.js.SphereBufferGeometry
import kotlin.math.PI

class Pacman : GameObject() {

    companion object {
        private const val SIZE = 1.0 * Maze.UNIT_SIZE
        private const val MAX_MOUTH_ANGLE = 60.0 * PI / 180
        private const val SEGMENTS = 30
    }

    private val mouthOpenGeometry = SphereBufferGeometry(
        radius = SIZE / 2,
        widthSegments = SEGMENTS,
        heightSegments = SEGMENTS,
        phiStart = MAX_MOUTH_ANGLE / 2,
        phiLength = 2 * PI - MAX_MOUTH_ANGLE,
        thetaStart = PI,
        thetaLength = PI,
    )

    private val geometry = SphereBufferGeometry(
            radius = SIZE / 2,
            widthSegments = SEGMENTS,
            heightSegments = SEGMENTS,
            phiStart = 0,
            phiLength = 2 * PI,
            thetaStart = PI,
            thetaLength = PI,
    ).apply {
        morphAttributes.asDynamic().position = arrayOf(mouthOpenGeometry.getAttribute("position"))
    }

    private var mouthOpenSpeed = 0.15
    private var mouthOpenInfluence = 0.0 // range: 0 - 1.0

    override val sceneObject = Mesh(geometry, 0xFFFE54.toMeshPhongMaterial().apply {
        morphTargets = true

        // TODO: Can I import constants correctly?
        //asDynamic()["side"] = 2
    })

    override fun setup(state: GameState) {
        sceneObject.position.set(state.pacman.worldPosition.x, SIZE, state.pacman.worldPosition.y)
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

        sceneObject.morphTargetInfluences[0] = mouthOpenInfluence

        sceneObject.position.x = worldPosition.x
        sceneObject.position.z = worldPosition.y

        sceneObject.setRotationFromAxisAngle(Z_AXIS, when (state.pacman.direction) {
            DOWN -> 1.5 * PI
            RIGHT -> 0
            UP -> PI / 2
            LEFT -> PI
        })
    }
}