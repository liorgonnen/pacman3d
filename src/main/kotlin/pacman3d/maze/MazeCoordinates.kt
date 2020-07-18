package pacman3d.maze

import pacman3d.ZERO2
import pacman3d.ext.sqr
import pacman3d.ext.sqrt
import pacman3d.logic.Direction
import pacman3d.logic.Direction.*
import three.js.Vector2
import three.js.Vector3

data class MazeCoordinates(var x: Int = 0, var y: Int = 0) {

    val index get() = Maze.indexOf(x, y)

    // Top
    private val TM = Vector2(Maze.HALF_UNIT_SIZE, 0)
    private val TR = Vector2(Maze.UNIT_SIZE, 0)

    // Middle
    private val ML = Vector2(0, Maze.HALF_UNIT_SIZE)
    private val MM = Vector2(Maze.HALF_UNIT_SIZE, Maze.HALF_UNIT_SIZE)
    private val MR = Vector2(Maze.UNIT_SIZE, Maze.HALF_UNIT_SIZE)

    // Bottom
    private val BL = Vector2(0, Maze.UNIT_SIZE)
    private val BM = Vector2(Maze.HALF_UNIT_SIZE, Maze.UNIT_SIZE)
    private val BR = Vector2(Maze.UNIT_SIZE, Maze.UNIT_SIZE)

    fun toWorldPosition() = Vector2(-Maze.HALF_WIDTH + x * Maze.UNIT_SIZE, -Maze.HALF_LENGTH + y * Maze.UNIT_SIZE)

    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun copy(other: MazeCoordinates) = apply {
        x = other.x
        y = other.y
    }

    val tl get() = toWorldPosition()
    val tm get() = toWorldPosition().add(TM)
    val tr get() = toWorldPosition().add(TR)

    val ml get() = toWorldPosition().add(ML)
    val mm get() = toWorldPosition().add(MM)
    val mr get() = toWorldPosition().add(MR)

    val bl get() = toWorldPosition().add(BL)
    val bm get() = toWorldPosition().add(BM)
    val br get() = toWorldPosition().add(BR)

    fun move(direction: Direction) = apply {
        when (direction) {
            UP -> set(x, y - 1)
            DOWN -> set(x, y + 1)
            LEFT -> set(x - 1, y)
            RIGHT -> set(x + 1, y)
        }
    }

    fun sqrDistanceFromDirectionTo(direction: Direction, other: MazeCoordinates)
        = (x + direction.x - other.x).sqr + (y + direction.y - other.y).sqr
}

/**
 * Map Maze 2D coordinates in maze size units to 3D space
 * Sets only the x and z components for a 3D vector. The y component is left unchanged
 * @param mazeCoordinates
 */
fun Vector3.setFromMazeCoordinates(mazeCoordinates: MazeCoordinates, y: Double = 0.0) = let {
    it.x = -Maze.HALF_WIDTH + mazeCoordinates.x * Maze.UNIT_SIZE + Maze.HALF_UNIT_SIZE
    it.y = y
    it.z = -Maze.HALF_LENGTH + mazeCoordinates.y * Maze.UNIT_SIZE + Maze.HALF_UNIT_SIZE
}

/**
 * Set this Vector2 to world coordinates represented by the given [mazeCoordinates]
 * The values will be shifted by the [subStep] value.
 *
 * Since maze coordinates represent the center of a tile, subStep coordinates should be in the range of
 * -0.5..0.5
 *
 * @param mazeCoordinates
 * @param subStep
 */
fun Vector2.setFromMazeCoordinates(mazeCoordinates: MazeCoordinates, subStep: Vector2 = ZERO2) = apply {
    x = -Maze.HALF_WIDTH + (mazeCoordinates.x + subStep.x.toDouble() + 0.5) * Maze.UNIT_SIZE
    y = -Maze.HALF_LENGTH + (mazeCoordinates.y + subStep.y.toDouble() + 0.5) * Maze.UNIT_SIZE
}
