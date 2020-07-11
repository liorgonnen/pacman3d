 package pacman3d.ext

import three.js.LineCurve3
import three.js.Quaternion
import three.js.Vector3
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

val Vector3.isZero get() = x == 0 && y == 0 && z == 0

inline val Vector3.isNoneZero get() = !isZero

inline fun Vector3.zero() = set(0.0, 0.0, 0.0)

/**
 * Angle zero is towards the positive Z-Axis, so actually x serves s the Y-axis, that's why we pass it as the first
 * parameter to [atan2]
 */
inline fun Vector3.asAngle() = atan2(x.toDouble(), z.toDouble()).wrapTo2PI()

fun Vector3.setXZFromAngle(angle: Double) = set(sin(angle), 0, cos(angle))

fun Vector3.asString() = "[${x.truncate(2)}, ${y.truncate(2)}, ${z.truncate(2)}]"

fun Vector3.fromQuaternion(q: Quaternion) = apply {
    val x = q.x.toDouble()
    val y = q.y.toDouble()
    val z = q.z.toDouble()
    val w = q.w.toDouble()
    set(2 * (x * z + w * y), 2 * (y * x - w * x), 1 - 2 * (x * x + y * y))
}

fun Vector3.rotateBy(angle: Double) {
    val length = this.length()
    setXZFromAngle((this.asAngle() + angle) % TWO_PI).normalize().multiplyScalar(length)
}

/**
* Rotate this vector 90 degrees clockwise on the XZ plane.
*/
fun Vector3.rotate90CW() = apply {
    val temp = z
    z = -x.toDouble()
    x = temp
}

 fun Vector3.rotate90CCW() = apply {
     val temp = z.toDouble()
     z = x.toDouble()
     x = -temp
 }

 infix fun Vector3.lineCurveTo(other: Vector3) = LineCurve3(this, other)
