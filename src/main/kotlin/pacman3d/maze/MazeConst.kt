package pacman3d.maze

import kotlin.math.absoluteValue

object MazeConst {

    fun indexOf(x: Int, y: Int) = y * WIDTH_UNITS + (x.absoluteValue % WIDTH_UNITS)

    const val WIDTH_UNITS = 30
    const val VISIBLE_WIDTH_UNITS = 28 // The tunnel at each side of the maze is not visible
    const val LENGTH_UNITS = 36

    const val FIRST_EFFECTIVE_LINE = 3
    const val LAST_EFFECTIVE_LINE = 33

    const val WALL_HEIGHT = 2.0
    const val WALL_THICKNESS = 0.2
    const val HALF_WALL_THICKNESS = WALL_THICKNESS / 2

    const val UNIT_SIZE = 1.0

    const val HALF_UNIT_SIZE = UNIT_SIZE / 2.0

    const val VISIBLE_WIDTH = VISIBLE_WIDTH_UNITS * UNIT_SIZE
    const val WIDTH = WIDTH_UNITS * UNIT_SIZE
    const val LENGTH = LENGTH_UNITS * UNIT_SIZE
    const val EFFECTIVE_LENGTH = (LAST_EFFECTIVE_LINE - FIRST_EFFECTIVE_LINE + 1) * UNIT_SIZE

    const val HALF_WIDTH = WIDTH / 2
    const val HALF_LENGTH = LENGTH / 2
    const val HALF_VISIBLE_WIDTH = VISIBLE_WIDTH / 2
}