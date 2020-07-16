package pacman3d.camera

import pacman3d.ext.setXZFromAngle
import pacman3d.maze.Maze
import three.js.Camera

class CameraAnimator(private val camera: Camera) {

    companion object {
        private val CAMERA_HEIGHT = Maze.LENGTH * 0.5
    }

    private var cameraAngle = 0.0

    fun update(time: Double) {
        cameraAngle += time / 15.0
        camera.position.setXZFromAngle(cameraAngle).multiplyScalar(Maze.HALF_LENGTH).setY(CAMERA_HEIGHT)
        //cameraLookAt.setXZFromAngle(cameraAngle + PI / 2).multiplyScalar(HALF_SCENE_SIZE * 0.2)
        camera.lookAt(0, 0, 0)
    }
}