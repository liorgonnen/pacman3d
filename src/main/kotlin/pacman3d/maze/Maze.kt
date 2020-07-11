package pacman3d.maze

import three.js.Vector2

object Maze {

    private const val WIDTH_UNITS = 28
    private const val LENGTH_UNITS = 36

    const val WALL_HEIGHT = 2.0
    const val WALL_THICKNESS = 0.2
    const val HALF_WALL_THICKNESS = WALL_THICKNESS / 2

    private const val UNIT_SIZE = 1.0
    private const val HALF_UNIT_SIZE = UNIT_SIZE / 2.0

    const val WIDTH = WIDTH_UNITS * UNIT_SIZE
    const val LENGTH = LENGTH_UNITS * UNIT_SIZE

    object Pos {
        private const val HALF_WIDTH = WIDTH / 2
        private const val HALF_LENGTH = LENGTH / 2

        // Top
        private val TM = Vector2(HALF_UNIT_SIZE, 0)
        private val TR = Vector2(UNIT_SIZE, 0)

        // Middle
        private val ML = Vector2(0, HALF_UNIT_SIZE)
        private val MM = Vector2(HALF_UNIT_SIZE, HALF_UNIT_SIZE)
        private val MR = Vector2(UNIT_SIZE, HALF_UNIT_SIZE)

        // Bottom
        private val BL = Vector2(0, UNIT_SIZE)
        private val BM = Vector2(HALF_UNIT_SIZE, UNIT_SIZE)
        private val BR = Vector2(UNIT_SIZE, UNIT_SIZE)

        // Returns the top left corner of the given indices
        operator fun get(x: Int, y: Int) = Vector2(-HALF_WIDTH + x * UNIT_SIZE, -HALF_LENGTH + y * UNIT_SIZE)

        val Vector2.tl get() = this
        val Vector2.tm get() = add(TM)
        val Vector2.tr get() = add(TR)

        val Vector2.ml get() = add(ML)
        val Vector2.mm get() = add(MM)
        val Vector2.mr get() = add(MR)

        val Vector2.bl get() = add(BL)
        val Vector2.bm get() = add(BM)
        val Vector2.br get() = add(BR)
    }
}