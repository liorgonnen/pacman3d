@file:Suppress("PackageDirectoryMismatch")
package stats.js.ext

import stats.js.Stats

fun Stats.measure(block: () -> Any) {
    begin()
    block()
    end()
}
