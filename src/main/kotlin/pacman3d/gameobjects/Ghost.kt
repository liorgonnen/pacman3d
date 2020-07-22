package pacman3d.gameobjects

import pacman3d.ext.*
import pacman3d.gameobjects.Ghost.GazeDirection.LEFT
import pacman3d.gameobjects.Ghost.GazeDirection.RIGHT
import pacman3d.logic.Direction
import pacman3d.logic.GhostId
import pacman3d.logic.GhostId.Blinky
import pacman3d.logic.GhostId.Clyde
import pacman3d.logic.GhostId.Inky
import pacman3d.logic.GhostId.Pinky
import pacman3d.maze.Maze
import pacman3d.state.GameState
import three.js.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

sealed class Ghost(val id: GhostId, color: Int) : GameObject() {

    // TODO: Remove this in favor of [Direction]
    private sealed class GazeDirection(val multiplier: Double) {
        object LEFT : GazeDirection(-1.0)
        object RIGHT : GazeDirection(1.0)
    }

    companion object {
        // The percentage the wavy part of the ghost spans out of its entire height
        private const val WAVE_FRACTION = 0.1
        private const val HEAD_FRACTION = 0.5
        private const val EYE_FRACTION = 0.7

        private const val WIDTH = Maze.UNIT_SIZE * 1.6
        private const val HEIGHT = WIDTH * 1.2
        private const val RADIUS = WIDTH / 2.0
        private const val EYE_DEPTH = 0.01
        private const val EYE_HEIGHT = EYE_FRACTION * HEIGHT
        private const val EYE_WIDTH_RADIUS = RADIUS * 0.25
        private const val EYE_HEIGHT_RADIUS = EYE_WIDTH_RADIUS * 1.15
        private const val EYE_SPACING = RADIUS * 0.2
        private const val IRIS_RADIUS = EYE_WIDTH_RADIUS / 2

        private const val SLICES = 60
        private const val STACKS = 20
    }

    private val bodyGeometry = parametricGeometry(SLICES, STACKS) { u, v, p ->
        val a = u * TWO_PI
        when {
            // Lower-wavy part
            v < WAVE_FRACTION -> p.set(
                x = cos(a) * RADIUS,
                z = sin(a) * RADIUS,
                // We need to consistently go up in height, while simultaneously reducing
                // the sine amplitude to prevent from points from colliding
                y = (v + (WAVE_FRACTION - v) * sin(a * 8)) * HEIGHT
            )
            // Middle part, simply a tube
            v >= WAVE_FRACTION && v < HEAD_FRACTION -> p.set(
                x = cos(a) * RADIUS,
                z = sin(a) * RADIUS,
                y = v * HEIGHT
            )
            else -> {
                // Head part - upper half of a sphere
                val headHeight = 1.0 - HEAD_FRACTION
                val h = PI / 2 * (v - HEAD_FRACTION) / headHeight // Remap v to 0..PI/2
                p.set(
                    x = RADIUS * cos(h) * cos(a),
                    z = RADIUS * cos(h) * sin(a),
                    y = HEAD_FRACTION * HEIGHT + sin(h) * headHeight * HEIGHT
                )
            }
        }
    }

    private val bodyMaterial = color.toMeshLambertMaterial().apply { materialSide = DoubleSide }

    private val eyeballShape = Shape().apply {
        add(EllipseCurve(0, 0, EYE_WIDTH_RADIUS, EYE_HEIGHT_RADIUS, 0, TWO_PI, false, 0))
    }

    private val eyeballMaterial = 0xffffff.toMeshLambertMaterial()

    private val eyeballGeometry = ExtrudeGeometry(eyeballShape, ExtrudeOptions(steps = 10, depth = EYE_DEPTH, bevelEnabled = false))

    private val irisShape = Shape().apply {
        add(EllipseCurve(0, 0, IRIS_RADIUS, IRIS_RADIUS, 0, TWO_PI, false, 0))
    }

    private val irisMaterial = 0x1B1BFE.toMeshLambertMaterial()

    private val irisGeometry = ExtrudeGeometry(irisShape, ExtrudeOptions(steps = 10, depth = EYE_DEPTH / 2, bevelEnabled = false))

    private val rightIris = Mesh(irisGeometry, irisMaterial)

    private val leftIris = Mesh(irisGeometry, irisMaterial)

    override val sceneObject = Group().add(
            Mesh(bodyGeometry, bodyMaterial),
            Mesh(eyeballGeometry, eyeballMaterial).apply {
                position.set(
                    x = -(EYE_SPACING / 2 + EYE_WIDTH_RADIUS),
                    y = EYE_HEIGHT,
                    z = RADIUS
                )
            },
            Mesh(eyeballGeometry, eyeballMaterial).apply {
                position.set(
                    x = EYE_SPACING / 2 + EYE_WIDTH_RADIUS,
                    y = EYE_HEIGHT,
                    z = RADIUS
                )
            },
            leftIris,
            rightIris,
    )

    private fun look(direction: GazeDirection) {
        val center = EYE_SPACING / 2 + EYE_WIDTH_RADIUS
        val gazeDistance = (EYE_WIDTH_RADIUS - IRIS_RADIUS) * direction.multiplier
        leftIris.position.set(center + gazeDistance, EYE_HEIGHT, RADIUS + EYE_DEPTH)
        rightIris.position.set(-center + gazeDistance, EYE_HEIGHT, RADIUS + EYE_DEPTH)
    }

    override fun setup(state: GameState) {
        look(RIGHT)
        val ghostState = state.ghosts[id.ordinal]
        sceneObject.position.set(ghostState.position.worldX, WAVE_FRACTION * HEIGHT, ghostState.position.worldY)
    }

    override fun update(state: GameState, time: Double) {
        val ghost = state.ghosts[id.ordinal]
        sceneObject.position.x = ghost.position.worldX
        sceneObject.position.z = ghost.position.worldY

        if (ghost.direction == Direction.LEFT) look(LEFT)
        if (ghost.direction == Direction.RIGHT) look(RIGHT)
    }
}

class Blinky : Ghost(id = Blinky, color = 0xFE0000)

class Pinky : Ghost(id = Pinky, color = 0xFFBBFF)

class Inky : Ghost(id = Inky, color = 0x00D4D4)

class Clyde : Ghost(id = Clyde, color = 0xFFB950)