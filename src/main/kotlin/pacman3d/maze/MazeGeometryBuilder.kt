package pacman3d.maze

import pacman3d.ext.absValue
import pacman3d.ext.minus
import pacman3d.ext.plus
import pacman3d.maze.Maze.HALF_WALL_THICKNESS
import pacman3d.maze.Maze.Pos.bl
import pacman3d.maze.Maze.Pos.bm
import pacman3d.maze.Maze.Pos.br
import pacman3d.maze.Maze.Pos.ml
import pacman3d.maze.Maze.Pos.mm
import pacman3d.maze.Maze.Pos.mr
import pacman3d.maze.Maze.Pos.tl
import pacman3d.maze.Maze.Pos.tm
import pacman3d.maze.Maze.Pos.tr
import pacman3d.maze.Maze.WALL_HEIGHT
import pacman3d.maze.Maze.WALL_THICKNESS
import three.js.*
import kotlin.math.PI
import kotlin.math.sign

class MazeGeometryBuilder {
    
    private val mazePieces = ArrayList<Geometry>()
    
    private val currentPosition = Vector2(0, 0)
    
    private fun moveTo(pos: Vector2) { currentPosition.copy(pos) }
    
    private fun wallTo(pos: Vector2) {
        mazePieces += wall(currentPosition, pos)
        moveTo(pos)
    }
    
    private fun curveTo(pos: Vector2, flip: Boolean = false) {
        mazePieces += corner(currentPosition, pos, flip)
        moveTo(pos)
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

        val options = object : ExtrudeGeometryOptions {
            override var steps: Number? = 10
            override var depth: Number? = WALL_HEIGHT
            override var bevelEnabled: Boolean? = false
        }

        return ExtrudeGeometry(shape, options).apply {
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

    fun build(): Geometry {
        moveTo(Maze.Pos[0, 3].bl)
        curveTo(Maze.Pos[0, 3].tr, true)

        // Main outer bounds
        wallTo(Maze.Pos[27, 3].tl)
        curveTo(Maze.Pos[27, 3].br)
        wallTo(Maze.Pos[27, 11].br)
        curveTo(Maze.Pos[27, 12].bl, true)
        wallTo(Maze.Pos[23, 12].bm)
        curveTo(Maze.Pos[22, 13].mr)
        wallTo(Maze.Pos[22, 15].mr)
        curveTo(Maze.Pos[23, 16].tm, true)
        wallTo(Maze.Pos[27, 16].tr)

        moveTo(Maze.Pos[27, 18].br)
        wallTo(Maze.Pos[23, 18].bm)
        curveTo(Maze.Pos[22, 19].mr)

        wallTo(Maze.Pos[22, 21].mr)
        curveTo(Maze.Pos[23, 22].tm, true)
        wallTo(Maze.Pos[26, 22].tr)
        curveTo(Maze.Pos[27, 22].br)
        wallTo(Maze.Pos[27, 32].br)
        curveTo(Maze.Pos[27, 33].bl, true)
        wallTo(Maze.Pos[1, 33].bl)
        curveTo(Maze.Pos[0, 33].tl)
        wallTo(Maze.Pos[0, 22].bl)
        curveTo(Maze.Pos[0, 22].tr, true)
        wallTo(Maze.Pos[4, 22].tm)
        curveTo(Maze.Pos[5, 21].ml) // <-- Problem
        wallTo(Maze.Pos[5, 19].ml)
        curveTo(Maze.Pos[4, 18].bm, true) // <-- Problem
        wallTo(Maze.Pos[0, 18].bl)

        moveTo(Maze.Pos[0, 16].tl)
        wallTo(Maze.Pos[4, 16].tm)
        curveTo(Maze.Pos[5, 15].ml)
        wallTo(Maze.Pos[5, 13].ml)
        curveTo(Maze.Pos[4, 12].bm, true)
        wallTo(Maze.Pos[0, 12].br)
        curveTo(Maze.Pos[0, 12].tl)
        wallTo(Maze.Pos[0, 4].tl)

        // Main inner bounds
        moveTo(Maze.Pos[0, 3].bm)
        curveTo(Maze.Pos[0, 3].mr, true)
        wallTo(Maze.Pos[13, 3].ml)
        curveTo(Maze.Pos[13, 3].bm)
        wallTo(Maze.Pos[13, 7].tm)
        curveTo(Maze.Pos[13, 7].mr, true)
        curveTo(Maze.Pos[14, 7].tm)
        wallTo(Maze.Pos[14, 3].bm)
        curveTo(Maze.Pos[14, 3].mr, true)
        wallTo(Maze.Pos[27, 3].ml)
        curveTo(Maze.Pos[27, 3].bm)
        wallTo(Maze.Pos[27, 12].tm)
        curveTo(Maze.Pos[27, 12].ml, true)
        wallTo(Maze.Pos[22, 12].mr)
        curveTo(Maze.Pos[22, 12].bm)
        wallTo(Maze.Pos[22, 16].tm)
        curveTo(Maze.Pos[22, 16].mr, true)
        wallTo(Maze.Pos[27, 16].mr)

        moveTo(Maze.Pos[27, 18].mr)
        wallTo(Maze.Pos[22, 18].mr)
        curveTo(Maze.Pos[22, 18].bm)
        wallTo(Maze.Pos[22, 22].tm)
        curveTo(Maze.Pos[22, 22].mr, true)
        wallTo(Maze.Pos[27, 22].ml)
        curveTo(Maze.Pos[27, 22].bm)
        wallTo(Maze.Pos[27, 33].tm)
        curveTo(Maze.Pos[27, 33].ml, true)
        wallTo(Maze.Pos[0, 33].mr)
        curveTo(Maze.Pos[0, 33].tm)
        wallTo(Maze.Pos[0, 28].bm)
        curveTo(Maze.Pos[0, 28].mr, true)
        wallTo(Maze.Pos[2, 28].ml)
        curveTo(Maze.Pos[2, 28].tm)
        curveTo(Maze.Pos[2, 27].ml, true)
        wallTo(Maze.Pos[0, 27].mr)
        curveTo(Maze.Pos[0, 27].tm)
        wallTo(Maze.Pos[0, 22].bm)
        curveTo(Maze.Pos[0, 22].mr, true)
        wallTo(Maze.Pos[5, 22].ml)
        curveTo(Maze.Pos[5, 22].tm)
        wallTo(Maze.Pos[5, 18].bm)
        curveTo(Maze.Pos[5, 18].ml, true)
        wallTo(Maze.Pos[0, 18].ml)

        moveTo(Maze.Pos[0, 16].ml)
        wallTo(Maze.Pos[5, 16].ml)
        curveTo(Maze.Pos[5, 16].tm)
        wallTo(Maze.Pos[5, 12].bm)
        curveTo(Maze.Pos[5, 12].ml, true)
        wallTo(Maze.Pos[0, 12].mr)
        curveTo(Maze.Pos[0, 12].tm)
        wallTo(Maze.Pos[0, 3].bm)
//        moveTo(Maze.Pos[2, 6].mm)
//        wallTo(Maze.Pos[5, 6].mm)
//        wallTo(Maze.Pos[5, 8].mm)
//        wallTo(Maze.Pos[2, 8].mm)

//        // Q1
//        corner(Maze.Pos[3, 28].mm, Maze.Pos[4, 29].mm),
//
//        // Q4
//        corner(Maze.Pos[3, 29].mm, Maze.Pos[4, 30].mm, false),
//
//        corner(Maze.Pos[3, 27].mm, Maze.Pos[4, 26].mm),
//        corner(Maze.Pos[3, 26].mm, Maze.Pos[4, 25].mm, false),
        return mazePieces.fold(Geometry()) { maze, wall -> maze.apply { merge(wall) } }
    }
}