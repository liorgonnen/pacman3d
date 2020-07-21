package pacman3d.logic

import pacman3d.ext.sqr
import pacman3d.ext.truncate
import pacman3d.maze.Maze

class ActorPosition(var x: Double = 0.0, var y: Double = 0.0) {

    constructor(x: Int, y: Int) : this(x.toDouble() + 0.5, y.toDouble() + 0.5)

    val mazeX get() = x.toInt()

    val mazeY get() = y.toInt()

    val worldX get() = -Maze.HALF_WIDTH + x * Maze.UNIT_SIZE

    val worldY get() = -Maze.HALF_LENGTH + y * Maze.UNIT_SIZE

    val mazeIndex get() = Maze.indexOf(mazeX, mazeY)

    fun copy(other: ActorPosition) = apply {
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

    fun sqrDistanceFromDirectionTo(direction: Direction, other: ActorPosition): Int
        = (mazeX + direction.x - other.mazeX).sqr + (mazeY + direction.y - other.mazeY).sqr

    override fun toString() = "[${x.truncate(2)}, ${y.truncate(2)}] ($mazeIndex)]"
}