package pacman3d.maze

import three.js.Vector2

data class MazeCoordinates(var x: Int = 0, var y: Int = 0) {

    // Top
    private val TM = Vector2(MazeConst.HALF_UNIT_SIZE, 0)
    private val TR = Vector2(MazeConst.UNIT_SIZE, 0)

    // Middle
    private val ML = Vector2(0, MazeConst.HALF_UNIT_SIZE)
    private val MM = Vector2(MazeConst.HALF_UNIT_SIZE, MazeConst.HALF_UNIT_SIZE)
    private val MR = Vector2(MazeConst.UNIT_SIZE, MazeConst.HALF_UNIT_SIZE)

    // Bottom
    private val BL = Vector2(0, MazeConst.UNIT_SIZE)
    private val BM = Vector2(MazeConst.HALF_UNIT_SIZE, MazeConst.UNIT_SIZE)
    private val BR = Vector2(MazeConst.UNIT_SIZE, MazeConst.UNIT_SIZE)

    private fun toWorldPosition() = Vector2(-MazeConst.HALF_WIDTH + x * MazeConst.UNIT_SIZE, -MazeConst.HALF_LENGTH + y * MazeConst.UNIT_SIZE)

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
