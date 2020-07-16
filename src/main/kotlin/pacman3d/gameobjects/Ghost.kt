package pacman3d.gameobjects

import pacman3d.ext.*
import pacman3d.maze.Maze
import pacman3d.state.GameState
import three.js.Mesh
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Ghost : GameObject() {

    companion object {
        private const val WIDTH = Maze.UNIT_SIZE * 1.2 * 5
        private const val HEIGHT = WIDTH
        private const val RADIUS = WIDTH / 2.0

        // The percentage the wavy part of the ghost spans out of its entire height
        private const val WAVE_FRACTION = 0.1
        private const val HEAD_FRACTION = 0.5

        private const val SLICES = 30
        private const val STACKS = 20
    }

    private val geometry = parametricGeometry(SLICES, STACKS) { u, v, p ->
        val a = u * 2 * PI
        when {
            // Lower-wavy part
            v < WAVE_FRACTION -> p.set(
                x = cos(a) * RADIUS,
                z = sin(a) * RADIUS,
                y = HEIGHT * WAVE_FRACTION * sin(a * 4)
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

    override val sceneObject = Mesh(geometry, 0xff0000.toMeshLambertMaterial().apply { asDynamic()["side"] = 2 }).apply {
        position.set(0, 5, 0)
    }

    override fun setup(state: GameState) {

    }

    override fun update(state: GameState, time: Double) {
    
    }
}