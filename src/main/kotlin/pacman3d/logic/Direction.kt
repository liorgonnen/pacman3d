package pacman3d.logic

sealed class Direction(val multiplier: Double) {
    object UP : Direction(-1.0)
    object DOWN : Direction(1.0)
    object LEFT : Direction(-1.0)
    object RIGHT : Direction(1.0)

    val isHorizontal get() = this == LEFT || this == RIGHT
    val isVertical get() = this == UP || this == DOWN
}