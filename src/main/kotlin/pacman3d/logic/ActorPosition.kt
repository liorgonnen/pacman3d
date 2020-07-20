package pacman3d.logic

import pacman3d.ext.absValue
import pacman3d.ext.sqr
import pacman3d.maze.Maze

class ActorPosition(var x: Double = 0.0, var y: Double = 0.0) {

    constructor(x: Int, y: Int) : this(x.toDouble() + 0.5, y.toDouble() + 0.5)

    val mazeX get() = x.toInt()

    val mazeY get() = y.toInt()

    val worldX get() = -Maze.HALF_WIDTH + x * Maze.UNIT_SIZE

    val worldY get() = -Maze.HALF_LENGTH + y * Maze.UNIT_SIZE

    val fractionX get() = x - mazeX

    val fractionY get() = y - mazeY

    val mazeIndex get() = Maze.indexOf(mazeX, mazeY)

    fun copy(other: ActorPosition) = apply {
        x = other.x
        y = other.y
    }

    fun move(direction: Direction, distance: Double = 1.0) = apply {
        x += distance * direction.x
        y += distance * direction.y
    }

    fun centerInTile() = apply {
        x = mazeX.toDouble() + 0.5
        y = mazeY.toDouble() + 0.5
    }

    fun sqrDistanceFromDirectionTo(direction: Direction, other: ActorPosition): Int
        = (mazeX + direction.x - other.mazeX).sqr + (mazeY + direction.y - other.mazeY).sqr

    fun allowedToTurn(threshold: Double): Boolean
        = (fractionX - 0.5).absValue < threshold && (fractionY - 0.5).absValue < threshold
}