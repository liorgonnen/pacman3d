package pacman3d.maze

import pacman3d.ext.ExtrudeOptions
import pacman3d.ext.absValue
import pacman3d.ext.minus
import pacman3d.ext.plus
import pacman3d.maze.MazeConst.HALF_WALL_THICKNESS
import pacman3d.maze.MazeConst.WALL_HEIGHT
import pacman3d.maze.MazeConst.WALL_THICKNESS
import three.js.*
import kotlin.math.PI
import kotlin.math.sign

private class MazeCoordinates(var x: Int = 0, var y: Int = 0) {

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

    private fun toWorldPosition() = Vector2(-MazeConst.HALF_VISIBLE_WIDTH + x * MazeConst.UNIT_SIZE, -MazeConst.HALF_LENGTH + y * MazeConst.UNIT_SIZE)

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

class MazeGeometryBuilder {
    
    private val mazePieces = ArrayList<Geometry>()
    
    private val currentPosition = Vector2(0, 0)

    private val extrudeOptions = ExtrudeOptions(steps = 20, depth = WALL_HEIGHT, bevelEnabled = false)

    fun build(): Geometry {
        mazeFrameOuterBounds()
        mazeFrameInnerBounds()

        // First row islands
        square(2, 5, 5, 7)
        square(7, 5, 11, 7)
        square(16, 5, 20, 7)
        square(22, 5, 25, 7)

        // Second row islands
        square(2, 9, 5, 10)
        square(22, 9, 25, 10)

        // Vertical islands
        square(7, 18, 8, 22)
        square(19, 18, 20, 22)

        square(7, 24, 11, 25)
        square(16, 24, 20, 25)

        horizontalTWall(10, 9)
        horizontalTWall(10, 21)
        horizontalTWall(10, 27)

        verticalTWallLeft()
        verticalTWallRight()

        flipTWallLeft()
        flipTWallRight()

        flipLWallLeft()
        flipLWallRight()

        ghostHouse()

        return mazePieces.fold(Geometry()) { maze, wall -> maze.apply { merge(wall) } }.apply { mergeVertices() }
    }
    
    private fun moveTo(pos: Vector2) { currentPosition.copy(pos) }
    
    private fun wallTo(pos: Vector2) {
        mazePieces += wall(currentPosition, pos)
        moveTo(pos)
    }
    
    private fun curveTo(pos: Vector2, flip: Boolean = false) {
        mazePieces += corner(currentPosition, pos, flip)
        moveTo(pos)
    }

    private fun square(x1: Int, y1: Int, x2: Int, y2: Int) {
        val needsHLines = x2 - x1 > 1
        val needsVLines = y2 - y1 > 1

        moveTo(MazeCoordinates(x1, y1).bm)
        curveTo(MazeCoordinates(x1, y1).mr, true)

        if (needsHLines) wallTo(MazeCoordinates(x2, y1).ml)

        curveTo(MazeCoordinates(x2, y1).bm)

        if (needsVLines) wallTo(MazeCoordinates(x2, y2).tm)

        curveTo(MazeCoordinates(x2, y2).ml, true)

        if (needsHLines) wallTo(MazeCoordinates(x1, y2).mr)

        curveTo(MazeCoordinates(x1, y2).tm)

        if (needsVLines) wallTo(MazeCoordinates(x1, y1).bm)
    }

    private fun horizontalTWall(x: Int, y: Int) {
        moveTo(MazeCoordinates(x, y).bm)
        curveTo(MazeCoordinates(x, y).mr, true)
        wallTo(MazeCoordinates(x + 7, y).ml)
        curveTo(MazeCoordinates(x + 7, y).bm)
        curveTo(MazeCoordinates(x + 7, y + 1).ml, true)
        wallTo(MazeCoordinates(x + 4, y + 1).mr)
        curveTo(MazeCoordinates(x + 4, y + 1).bm)
        wallTo(MazeCoordinates(x + 4, y + 4).tm)
        curveTo(MazeCoordinates(x + 4, y + 4).ml, true)
        curveTo(MazeCoordinates(x + 3, y + 4).tm)
        wallTo(MazeCoordinates(x + 3, y + 1).bm)
        curveTo(MazeCoordinates(x + 3, y + 1).ml, true)
        wallTo(MazeCoordinates(x, y + 1).mr)
        curveTo(MazeCoordinates(x, y + 1).tm)
    }
    
    private fun corner(from: Vector2, to: Vector2, flip: Boolean): Geometry {
        val shape = Shape().apply {
            val dx = to.x - from.x.toDouble()
            val dy = to.y - from.y.toDouble()
            val thicknessDy = HALF_WALL_THICKNESS * dy.sign
            val thicknessDx = HALF_WALL_THICKNESS * dx.sign

            if (flip) {
                lineTo(-HALF_WALL_THICKNESS * dx.sign, 0)
                quadraticCurveTo(-thicknessDx, -dy - thicknessDy, dx, -dy - thicknessDy)
                lineTo(dx, -dy + thicknessDy)
                quadraticCurveTo(HALF_WALL_THICKNESS * dx.sign, -dy + thicknessDy, HALF_WALL_THICKNESS * dx.sign, 0)
            }
            else {
                lineTo(0, HALF_WALL_THICKNESS * dy.sign)
                quadraticCurveTo(dx + thicknessDx, thicknessDy, dx + thicknessDx, -dy)
                lineTo(dx - thicknessDx, -dy)
                quadraticCurveTo(dx - thicknessDx, -thicknessDy, 0, -thicknessDy)
            }
        }

        return ExtrudeGeometry(shape, extrudeOptions).apply {
            rotateX(-PI / 2)
            translate(from.x, -WALL_HEIGHT / 2, from.y)
        }
    }

    private fun wall(from: Vector2, to: Vector2): BoxGeometry = when {
        from.y == to.y -> { // Horizontal wall
            BoxGeometry((from.x - to.x.toDouble()).absValue + WALL_THICKNESS, WALL_HEIGHT, WALL_THICKNESS).apply {
                translate((from.x + to.x.toDouble()) / 2, 0, from.y)
            }
        }
        from.x == to.x -> { // Vertical wall
            BoxGeometry(WALL_THICKNESS, WALL_HEIGHT, (from.y - to.y.toDouble()).absValue + WALL_THICKNESS).apply {
                translate(from.x, 0, (from.y + to.y.toDouble()) / 2)
            }
        }
        else -> error("Not a vertical or horizontal wall")
    }

    private fun ghostHouse() {
        // Ghost house - Outer
        moveTo(MazeCoordinates(15, 15).ml)
        wallTo(MazeCoordinates(17, 15).mm)
        wallTo(MazeCoordinates(17, 19).mm)
        wallTo(MazeCoordinates(10, 19).mm)
        wallTo(MazeCoordinates(10, 15).mm)
        wallTo(MazeCoordinates(12, 15).mr)

        // Ghost house - Inner
        moveTo(MazeCoordinates(15, 15).bl)
        wallTo(MazeCoordinates(17, 15).bl)
        wallTo(MazeCoordinates(17, 19).tl)
        wallTo(MazeCoordinates(10, 19).tr)
        wallTo(MazeCoordinates(10, 15).br)
        wallTo(MazeCoordinates(12, 15).br)
    }

    private fun mazeFrameOuterBounds() {
        moveTo(MazeCoordinates(0, 3).bl)
        curveTo(MazeCoordinates(0, 3).tr, true)

        // Main outer bounds
        wallTo(MazeCoordinates(27, 3).tl)
        curveTo(MazeCoordinates(27, 3).br)
        wallTo(MazeCoordinates(27, 11).br)
        curveTo(MazeCoordinates(27, 12).bl, true)
        wallTo(MazeCoordinates(23, 12).bm)
        curveTo(MazeCoordinates(22, 13).mr)
        wallTo(MazeCoordinates(22, 15).mr)
        curveTo(MazeCoordinates(23, 16).tm, true)
        wallTo(MazeCoordinates(27, 16).tr)

        moveTo(MazeCoordinates(27, 18).br)
        wallTo(MazeCoordinates(23, 18).bm)
        curveTo(MazeCoordinates(22, 19).mr)

        wallTo(MazeCoordinates(22, 21).mr)
        curveTo(MazeCoordinates(23, 22).tm, true)
        wallTo(MazeCoordinates(26, 22).tr)
        curveTo(MazeCoordinates(27, 22).br)
        wallTo(MazeCoordinates(27, 32).br)
        curveTo(MazeCoordinates(27, 33).bl, true)
        wallTo(MazeCoordinates(1, 33).bl)
        curveTo(MazeCoordinates(0, 33).tl)
        wallTo(MazeCoordinates(0, 22).bl)
        curveTo(MazeCoordinates(0, 22).tr, true)
        wallTo(MazeCoordinates(4, 22).tm)
        curveTo(MazeCoordinates(5, 21).ml) // <-- Problem
        wallTo(MazeCoordinates(5, 19).ml)
        curveTo(MazeCoordinates(4, 18).bm, true) // <-- Problem
        wallTo(MazeCoordinates(0, 18).bl)

        moveTo(MazeCoordinates(0, 16).tl)
        wallTo(MazeCoordinates(4, 16).tm)
        curveTo(MazeCoordinates(5, 15).ml)
        wallTo(MazeCoordinates(5, 13).ml)
        curveTo(MazeCoordinates(4, 12).bm, true)
        wallTo(MazeCoordinates(0, 12).br)
        curveTo(MazeCoordinates(0, 12).tl)
        wallTo(MazeCoordinates(0, 4).tl)
    }

    private fun mazeFrameInnerBounds() {
        // Main inner bounds
        moveTo(MazeCoordinates(0, 3).bm)
        curveTo(MazeCoordinates(0, 3).mr, true)
        wallTo(MazeCoordinates(13, 3).ml)
        curveTo(MazeCoordinates(13, 3).bm)
        wallTo(MazeCoordinates(13, 7).tm)
        curveTo(MazeCoordinates(13, 7).mr, true)
        curveTo(MazeCoordinates(14, 7).tm)
        wallTo(MazeCoordinates(14, 3).bm)
        curveTo(MazeCoordinates(14, 3).mr, true)
        wallTo(MazeCoordinates(27, 3).ml)
        curveTo(MazeCoordinates(27, 3).bm)
        wallTo(MazeCoordinates(27, 12).tm)
        curveTo(MazeCoordinates(27, 12).ml, true)
        wallTo(MazeCoordinates(22, 12).mr)
        curveTo(MazeCoordinates(22, 12).bm)
        wallTo(MazeCoordinates(22, 16).tm)
        curveTo(MazeCoordinates(22, 16).mr, true)
        wallTo(MazeCoordinates(27, 16).mr)

        moveTo(MazeCoordinates(27, 18).mr)
        wallTo(MazeCoordinates(22, 18).mr)
        curveTo(MazeCoordinates(22, 18).bm)
        wallTo(MazeCoordinates(22, 22).tm)
        curveTo(MazeCoordinates(22, 22).mr, true)
        wallTo(MazeCoordinates(27, 22).ml)
        curveTo(MazeCoordinates(27, 22).bm)

        wallTo(MazeCoordinates(27, 27).tm)
        curveTo(MazeCoordinates(27, 27).ml, true)
        wallTo(MazeCoordinates(25, 27).mr)
        curveTo(MazeCoordinates(25, 27).bm)
        curveTo(MazeCoordinates(25, 28).mr, true)
        wallTo(MazeCoordinates(27, 28).ml)
        curveTo(MazeCoordinates(27, 28).bm)

        wallTo(MazeCoordinates(27, 33).tm)
        curveTo(MazeCoordinates(27, 33).ml, true)
        wallTo(MazeCoordinates(0, 33).mr)
        curveTo(MazeCoordinates(0, 33).tm)
        wallTo(MazeCoordinates(0, 28).bm)
        curveTo(MazeCoordinates(0, 28).mr, true)
        wallTo(MazeCoordinates(2, 28).ml)
        curveTo(MazeCoordinates(2, 28).tm)
        curveTo(MazeCoordinates(2, 27).ml, true)
        wallTo(MazeCoordinates(0, 27).mr)
        curveTo(MazeCoordinates(0, 27).tm)
        wallTo(MazeCoordinates(0, 22).bm)
        curveTo(MazeCoordinates(0, 22).mr, true)
        wallTo(MazeCoordinates(5, 22).ml)
        curveTo(MazeCoordinates(5, 22).tm)
        wallTo(MazeCoordinates(5, 18).bm)
        curveTo(MazeCoordinates(5, 18).ml, true)
        wallTo(MazeCoordinates(0, 18).ml)

        moveTo(MazeCoordinates(0, 16).ml)
        wallTo(MazeCoordinates(5, 16).ml)
        curveTo(MazeCoordinates(5, 16).tm)
        wallTo(MazeCoordinates(5, 12).bm)
        curveTo(MazeCoordinates(5, 12).ml, true)
        wallTo(MazeCoordinates(0, 12).mr)
        curveTo(MazeCoordinates(0, 12).tm)
        wallTo(MazeCoordinates(0, 3).bm)
    }

    private fun verticalTWallLeft() {
        moveTo(MazeCoordinates(7, 9).bm)
        curveTo(MazeCoordinates(7, 9).mr, true)
        curveTo(MazeCoordinates(8, 9).bm)
        wallTo(MazeCoordinates(8, 12).tm)
        curveTo(MazeCoordinates(8, 12).mr, true)
        wallTo(MazeCoordinates(11, 12).ml)
        curveTo(MazeCoordinates(11, 12).bm)
        curveTo(MazeCoordinates(11, 13).ml, true)
        wallTo(MazeCoordinates(8, 13).mr)
        curveTo(MazeCoordinates(8, 13).bm)
        wallTo(MazeCoordinates(8, 16).tm)
        curveTo(MazeCoordinates(8, 16).ml, true)
        curveTo(MazeCoordinates(7, 16).tm)
        wallTo(MazeCoordinates(7, 9).bm)
    }

    private fun verticalTWallRight() {
        moveTo(MazeCoordinates(19, 9).bm)
        curveTo(MazeCoordinates(19, 9).mr, true)
        curveTo(MazeCoordinates(20, 9).bm)
        wallTo(MazeCoordinates(20, 16).tm)
        curveTo(MazeCoordinates(20, 16).ml, true)
        curveTo(MazeCoordinates(19, 16).tm)
        wallTo(MazeCoordinates(19, 13).bm)
        curveTo(MazeCoordinates(19, 13).ml, true)
        wallTo(MazeCoordinates(16, 13).mr)
        curveTo(MazeCoordinates(16, 13).tm)
        curveTo(MazeCoordinates(16, 12).mr, true)
        wallTo(MazeCoordinates(19, 12).ml)
        curveTo(MazeCoordinates(19, 12).tm)
        wallTo(MazeCoordinates(19, 9).bm)
    }

    private fun flipTWallLeft() {
        moveTo(MazeCoordinates(2, 30).bm)
        curveTo(MazeCoordinates(2, 30).mr, true)
        wallTo(MazeCoordinates(7, 30).ml)
        curveTo(MazeCoordinates(7, 30).tm)
        wallTo(MazeCoordinates(7, 27).bm)
        curveTo(MazeCoordinates(7, 27).mr, true)
        curveTo(MazeCoordinates(8, 27).bm)
        wallTo(MazeCoordinates(8, 30).tm)
        curveTo(MazeCoordinates(8, 30).mr, true)
        wallTo(MazeCoordinates(11, 30).ml)
        curveTo(MazeCoordinates(11, 30).bm)
        curveTo(MazeCoordinates(11, 31).ml, true)
        wallTo(MazeCoordinates(2, 31).mr)
        curveTo(MazeCoordinates(2, 31).tm)
    }

    private fun flipTWallRight() {
        moveTo(MazeCoordinates(16, 30).bm)
        curveTo(MazeCoordinates(16, 30).mr, true)
        wallTo(MazeCoordinates(19, 30).ml)
        curveTo(MazeCoordinates(19, 30).tm)
        wallTo(MazeCoordinates(19, 27).bm)
        curveTo(MazeCoordinates(19, 27).mr, true)
        curveTo(MazeCoordinates(20, 27).bm)
        wallTo(MazeCoordinates(20, 30).tm)
        curveTo(MazeCoordinates(20, 30).mr, true)
        wallTo(MazeCoordinates(25, 30).ml)
        curveTo(MazeCoordinates(25, 30).bm)
        curveTo(MazeCoordinates(25, 31).ml, true)
        wallTo(MazeCoordinates(16, 31).mr)
        curveTo(MazeCoordinates(16, 31).tm)
    }

    private fun flipLWallLeft() {
        moveTo(MazeCoordinates(2, 24).bm)
        curveTo(MazeCoordinates(2, 24).mr, true)
        wallTo(MazeCoordinates(5, 24).ml)
        curveTo(MazeCoordinates(5, 24).bm)
        wallTo(MazeCoordinates(5, 28).tm)
        curveTo(MazeCoordinates(5, 28).ml, true)
        curveTo(MazeCoordinates(4, 28).tm)
        wallTo(MazeCoordinates(4, 25).bm)
        curveTo(MazeCoordinates(4, 25).ml, true)
        wallTo(MazeCoordinates(2, 25).mr)
        curveTo(MazeCoordinates(2, 25).tm)
    }

    private fun flipLWallRight() {
        moveTo(MazeCoordinates(22, 24).bm)
        curveTo(MazeCoordinates(22, 24).mr, true)
        wallTo(MazeCoordinates(25, 24).ml)
        curveTo(MazeCoordinates(25, 24).bm)
        curveTo(MazeCoordinates(25, 25).ml, true)
        wallTo(MazeCoordinates(23, 25).mr)
        curveTo(MazeCoordinates(23, 25).bm)
        wallTo(MazeCoordinates(23, 28).tm)
        curveTo(MazeCoordinates(23, 28).ml, true)
        curveTo(MazeCoordinates(22, 28).tm)
        wallTo(MazeCoordinates(22, 24).bm)
    }
}