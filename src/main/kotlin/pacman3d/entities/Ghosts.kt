package pacman3d.entities

import pacman3d.logic.Direction
import pacman3d.logic.Position

class Blinky : Ghost(
    color = 0xFE0000,
    initialDirection = Direction.LEFT,
    initialPosition = Position(14.0, 14.0),
    scatterTargetTile = Position(26, 0)
) {
    override fun getChaseTargetTile(world: World, targetTile: Position) {
        targetTile.copy(world.pacman.position)
    }
}

class Clyde : Ghost(
        color = 0xFFB950,
        initialDirection = Direction.UP,
        initialPosition = Position(16.0, 17.0),
        scatterTargetTile = Position(0, 35)
) {
    override fun getChaseTargetTile(world: World, targetTile: Position) {
        val pacmanPosition = world.pacman.position
        val distanceToPacman = position.distanceTo(pacmanPosition)
        targetTile.copy(if (distanceToPacman >= 8.0) pacmanPosition else scatterTargetTile)
    }
}

class Inky : Ghost(
        color = 0x00D4D4,
        initialDirection = Direction.UP,
        initialPosition = Position(12.0, 17.0),
        scatterTargetTile = Position(27, 35),
) {
    override fun getChaseTargetTile(world: World, targetTile: Position) = with (world) {
        targetTile.copy(pacman.position).move(pacman.currentDirection, 2.0)

        // This replicates the bug in the original game as described above
        if (pacman.currentDirection == Direction.UP) targetTile.move(Direction.LEFT, 2.0)

        // Calculate a direct line to Blinky's position
        val blinkyDeltaX = blinky.position.mazeX - targetTile.mazeX
        val blinkyDeltaY = blinky.position.mazeY - targetTile.mazeY

        targetTile.x -= blinkyDeltaX
        targetTile.y -= blinkyDeltaY
    }
}

class Pinky : Ghost(
        color = 0xFFBBFF,
        initialDirection = Direction.DOWN,
        initialPosition = Position(14.0, 18.0),
        scatterTargetTile = Position(1, 0)
) {
    /**
     * In chase mode, Pinky behaves as he does because he does not target Pac-Man's tile directly. Instead, he selects an
     * offset four tiles away from Pac-Man in the direction Pac-Man is currently moving.
     * This is the behavior for left, right and down, but when going up, Pinky's target tile will be four tiles up and four
     * tiles to the left. This interesting outcome is due to a subtle error in the logic code that calculates Pinky's offset
     * from Pac-Man. This piece of code works properly for the other three cases but, when Pac-Man is moving upwards,
     * triggers an overflow bug that mistakenly includes a left offset equal in distance to the expected up offset
     */
    override fun getChaseTargetTile(world: World, targetTile: Position) = with (world) {
        targetTile.copy(pacman.position).move(pacman.currentDirection, 4.0)

        // This replicates the bug in the original game as described above
        if (pacman.currentDirection == Direction.UP) targetTile.move(Direction.LEFT, 4.0)
    }
}