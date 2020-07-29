package pacman3d.ext

fun <T> Array<T>.levelValue(level: Int) = this[minOf(level, size - 1)]