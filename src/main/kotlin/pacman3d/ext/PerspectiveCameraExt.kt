package pacman3d.ext

import three.js.PerspectiveCamera
import kotlinx.browser.window

inline fun PerspectiveCamera.onResize() {
    aspect = window.aspectRatio
    updateProjectionMatrix()
}
