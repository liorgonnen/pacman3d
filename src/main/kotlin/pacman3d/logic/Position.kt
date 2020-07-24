package pacman3d.logic

import pacman3d.ext.sqr
import pacman3d.ext.truncate
import pacman3d.maze.MazeConst
import kotlin.math.sqrt

class Position(var x: Double = 0.0, var y: Double = 0.0) {

    constructor(x: Int, y: Int) : this(x.toDouble() + 0.5, y.toDouble() + 0.5)

    val mazeX get() = x.toInt()

    val mazeY get() = y.toInt()

    val worldX get() = -MazeConst.HALF_WIDTH + x * MazeConst.UNIT_SIZE

    val worldY get() = -MazeConst.HALF_LENGTH + y * MazeConst.UNIT_SIZE

    val mazeIndex get() = MazeConst.indexOf(mazeX, mazeY)

    fun copy(other: Position) = apply {
        x = other.x
        y = other.y
    }

    fun move(direction: Direction, distance: Double = 1.0) = apply {
        x += distance * direction.x
        y += distance * direction.y
    }

    fun centerX() = apply { x = mazeX.toDouble() + 0.5 }

    fun centerY() = apply { y = mazeY.toDouble() + 0.5 }

    fun correctPosition(direction: Direction) {
        if (direction.isHorizontal) x = (mazeX.toDouble() + 0.5) else y = (mazeY.toDouble() + 0.5)
    }

    fun distanceTo(other: Position): Double
        = sqrt((mazeX - other.mazeX).toDouble().sqr + (mazeY - other.mazeY).toDouble().sqr)

    fun sqrDistanceFromDirectionTo(direction: Direction, other: Position): Int
        = (mazeX + direction.x - other.mazeX).sqr + (mazeY + direction.y - other.mazeY).sqr

    override fun toString() = "[${x.truncate(2)}, ${y.truncate(2)}] ($mazeIndex)]"
}