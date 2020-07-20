package pacman3d.maze

import three.js.Vector2

data class MazeCoordinates(var x: Int = 0, var y: Int = 0) {

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

    private fun toWorldPosition() = Vector2(-Maze.HALF_WIDTH + x * Maze.UNIT_SIZE, -Maze.HALF_LENGTH + y * Maze.UNIT_SIZE)

    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
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
}
