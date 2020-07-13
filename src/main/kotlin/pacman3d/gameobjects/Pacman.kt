package pacman3d.gameobjects

import pacman3d.ext.plus
import pacman3d.ext.toMeshPhongMaterial
import pacman3d.maze.Maze
import pacman3d.maze.Maze.Pos.mr
import pacman3d.maze.Maze.Pos.toVector3
import pacman3d.state.GameState
import three.js.Mesh
import three.js.SphereBufferGeometry
import kotlin.math.PI

class Pacman : GameObject() {

    companion object {
        private const val SIZE = 1.6 * Maze.UNIT_SIZE
        private const val MAX_MOUTH_ANGLE = 60.0 * PI / 180
    }

    private val mouthOpenGeometry = SphereBufferGeometry(
        radius = SIZE / 2,
        widthSegments = 30,
        heightSegments = 30,
        phiStart = MAX_MOUTH_ANGLE / 2,
        phiLength = 2 * PI - MAX_MOUTH_ANGLE,
        thetaStart = PI,
        thetaLength = PI,
    )

    private val geometry = SphereBufferGeometry(
            radius = SIZE / 2,
            widthSegments = 30,
            heightSegments = 30,
            phiStart = 0,
            phiLength = 2 * PI,
            thetaStart = PI,
            thetaLength = PI,
    ).apply {
        morphAttributes.asDynamic().position = arrayOf(mouthOpenGeometry.getAttribute("position"))
    }

    private var mouthOpenDirection = 0.15
    private var mouthOpenInfluence = 0.0 // range: 0 - 1.0

    override val sceneObject = Mesh(geometry, 0xFFFE54.toMeshPhongMaterial().apply { morphTargets = true })

    override fun setup(state: GameState) {
        sceneObject.position.copy(Maze.Pos[13, 26].mr.toVector3(SIZE))
    }

    override fun update(state: GameState) {
        mouthOpenInfluence += mouthOpenDirection
        if (mouthOpenInfluence >= 1.0) {
            mouthOpenInfluence = 1.0
            mouthOpenDirection = -mouthOpenDirection
        }
        else if (mouthOpenInfluence <= 0) {
            mouthOpenInfluence = 0.0
            mouthOpenDirection = -mouthOpenDirection
        }

        sceneObject.morphTargetInfluences[0] = mouthOpenInfluence
    }
}