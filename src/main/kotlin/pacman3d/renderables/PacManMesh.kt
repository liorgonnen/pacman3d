package pacman3d.renderables

import pacman3d.ext.*
import pacman3d.maze.MazeConst
import three.js.Group
import three.js.Mesh
import three.js.SphereBufferGeometry
import kotlin.math.PI

class PacManMesh : Group() {

    companion object {
        const val SIZE = 1.6 * MazeConst.UNIT_SIZE
        private const val MAX_MOUTH_ANGLE = 90.0 * PI / 180
        private const val SEGMENTS = 16

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

        internal val geometry = SphereBufferGeometry(
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

        private val outsideMaterial = 0xFFFE54.toMeshLambertMaterial().apply {
            morphTargets = true
            transparent = true
            materialSide = BackSide
        }

        private val insideMaterial = 0x887E29.toMeshLambertMaterial().apply {
            morphTargets = true
            transparent = true
            materialSide = FrontSide
        }
    }

    private var mouthOpenSpeed = 1.0
    private var mouthOpenInfluence = 0.0 // range: 0 - 1.0

    private val insideMesh = Mesh(geometry, insideMaterial)
    private val outsideMesh = Mesh(geometry, outsideMaterial)

    var opacity: Double
        set(value) {
            outsideMaterial.opacity = value
            insideMaterial.opacity = value
        }
        get() = outsideMaterial.opacity.toDouble()

    init {
        add(insideMesh, outsideMesh)
        rotateX(-HALF_PI)
    }

    fun updateMouthOpen(time: Double) {
        mouthOpenInfluence += time
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
    }
}