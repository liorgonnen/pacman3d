package pacman3d.ext

import three.js.Color
import three.js.LineBasicMaterial
import three.js.MeshPhongMaterial
import three.js.Vector3
import kotlin.math.*
import kotlin.random.Random

const val TWO_PI = PI * 2.0
const val HALF_PI = PI / 2.0

inline val Number.absValue get() = abs(toDouble())

fun randomAngle(rangeInRadians: Double) = Random.nextDouble(-1.0, 1.0) * rangeInRadians
fun fraction(value: Double, min: Double, max: Double) = (value.coerceIn(min, max) - min) / (max - min)

fun Number.toDegrees() = this / PI * 180.0
fun Number.toRadians() = this * PI / 180.0

operator fun Number.minus(other: Double) = toDouble() - other
operator fun Number.plus(other: Double) = toDouble() + other
operator fun Number.div(other: Double) = toDouble() / other
operator fun Number.times(other: Double) = toDouble() * other
operator fun Number.compareTo(other: Double): Int = toDouble().compareTo(other)

operator fun Double.minus(other: Number) = this - other.toDouble()
operator fun Double.plus(other: Number) = this + other.toDouble()

inline val Double.sqr get() = this * this
inline val Int.sqr get() = this * this
inline val Int.sqrt get(): Double = sqrt(this.toDouble())

// For debug only. Allocates a new object
fun Double.toSpeedVector() = Vector3().setXZFromAngle(this)

fun Double.wrapTo2PI() = when {
    this < 0.0 -> (this % TWO_PI) + TWO_PI
    this > TWO_PI -> (this % TWO_PI)
    else -> this
}

fun Double.isInAngleRange(startAngle: Double, endAngle: Double): Boolean {
    if (startAngle > endAngle) throw IllegalArgumentException()

    if (startAngle < 0 || endAngle >= TWO_PI) {
        return this in startAngle.wrapTo2PI()..TWO_PI || (this >= 0.0 && this <= endAngle.wrapTo2PI())
    }

    return this in startAngle.wrapTo2PI()..endAngle.wrapTo2PI()
}

fun Double.asRangeFraction(min: Double, max: Double) = fraction(this, min, max)

// For easy printing
val Double.roundAngleDegrees get() = this.toDegrees().roundToInt()

fun Number.truncate(decimalDigits: Int) = 10.0.pow(decimalDigits).let { pow -> (this.toDouble() * pow).roundToInt() / pow }

fun Number.toMeshPhongMaterial() = let { materialColor -> MeshPhongMaterial().apply { color = Color(materialColor) } }
fun Number.toLineBasicMaterial() = let { materialColor -> LineBasicMaterial().apply { color = Color(materialColor) } }
fun Number.toMeshLambertMaterial() = let { materialColor -> MeshPhongMaterial().apply { color = Color(materialColor) }}

val Number.normalized get() = if (this < 0.0) -1.0 else if (this > 0.0) 1.0 else 0.0

/**
 * An equality operator with some epsilon
 */
infix fun Number.equ(other: Number) = this.toDouble() - other.toDouble() < 0.01