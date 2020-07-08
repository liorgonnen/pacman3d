package pacman3d.ext

import three.js.Quaternion
import three.js.Vector3

private val auxVec = Vector3()
private val auxQuaternion = Quaternion()

/**
 * Initialize a quaternion from a given direction
 *
 * @param direction The direction to create the quaternion from relative to the positive Z axis.
 */
fun Quaternion.setFromDirection(direction: Vector3) = apply {
    setFromUnitVectors(Z_AXIS, auxVec.copy(direction).normalize())
}

fun Quaternion.onChange(callback: () -> Unit) = apply { _onChangeCallback = callback }

/**
 * Same as [Quaternion.angleTo], but takes care of normalization internally
 */
infix fun Quaternion.dotAngle(other: Quaternion) = angleTo(auxQuaternion.copy(other).normalize())
