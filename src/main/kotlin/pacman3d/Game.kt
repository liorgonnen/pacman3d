package pacman3d

import kotlinx.browser.document
import kotlinx.browser.window
import pacman3d.camera.CameraAnimator
import pacman3d.ext.*
import pacman3d.maze.MazeConst
import pacman3d.entities.World
import pacman3d.logic.states.GameController
import pacman3d.logic.states.GameStateMachine
import stats.js.Stats
import three.js.*

class Game {

    private var paused = false

    private val clock = Clock()

    private val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.set(0, MazeConst.LENGTH * 0.6, MazeConst.LENGTH * 0.6)
        lookAt(0, 0, 0)
    }

    private val cameraAnimator = CameraAnimator(camera)

    private val renderer = WebGLRenderer().init(clearColor = 0x333333)

    private val gameController = GameController()

    private val stats = Stats().apply {
        showPanel(0) // 0: fps, 1: ms, 2: mb, 3+: custom
        document.body?.appendChild(domElement)
        with(domElement.style) {
            position="fixed"
            top="0px"
            left="0px"
        }
    }

    private val scene = Scene().apply {
        add(DirectionalLight(0xffffff, 1).apply { position.set(-MazeConst.VISIBLE_WIDTH, 40, MazeConst.LENGTH) })
        add(HemisphereLight(0xffffff, 0xffffff, 0.5))
        //add(AmbientLight(0x555555))
    }

    init {
        window.onresize = {
            camera.onResize()
            renderer.onResize()
            updateScene()
        }

        window.onblur = { pause() }

        window.onfocus = { resume() }

        gameController.setup(scene)

        gameController.start()
    }

    private fun pause() {
        clock.stop()
        paused = true
    }

    private fun resume() {
        paused = false
        clock.start()
        animate()
    }

    private fun updateScene() {
        stats.begin()

        val time = clock.getDelta().toDouble()

        gameController.update(time)

        //cameraAnimator.update(time)

        renderer.render(scene, camera)

        stats.end()
    }

    fun animate() {
        updateScene()

        if (!paused) window.requestAnimationFrame { animate() }
    }
}