package pacman3d

import kotlinx.browser.document
import kotlinx.browser.window
import pacman3d.camera.CameraAnimator
import pacman3d.ext.*
import pacman3d.gameobjects.*
import pacman3d.maze.Maze
import pacman3d.maze.MazeGeometryBuilder
import pacman3d.state.GameState
import stats.js.Stats
import three.js.*

class Game {
    private val clock = Clock()

    private val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.set(0, Maze.LENGTH * 0.5, Maze.LENGTH * 0.5)
        lookAt(0, 0, 0)
    }

    private val cameraAnimator = CameraAnimator(camera)

    private val renderer = WebGLRenderer().init(clearColor = 0x333333)

    private val gameObjects = arrayOf(
        Dots(),
        Pacman(),
        Blinky(),
        Pinky(),
        Inky(),
        Clyde(),
    )

    private val gameState = GameState()

    private val stats = Stats().apply {
        showPanel(0) // 0: fps, 1: ms, 2: mb, 3+: custom
        document.body?.appendChild(domElement)
        with(domElement.style) {
            position="fixed"
            top="0px"
            left="0px"
        }
    }

    private val plane = Mesh(PlaneGeometry(Maze.WIDTH, Maze.EFFECTIVE_LENGTH, 5), 0.toMeshLambertMaterial()).apply {
        rotateX(-HALF_PI)
    }

    private val maze = Mesh(MazeGeometryBuilder().build(), 0x151FCD.toMeshLambertMaterial())

    private val scene = Scene().apply {
        add(plane)
        add(maze)

        gameObjects.forEach { add(it) }

        add(DirectionalLight(0xffffff, 1).apply { position.set(-Maze.WIDTH, 40, Maze.LENGTH) })
        add(HemisphereLight(0xffffff, 0xffffff, 0.5))
        //add(AmbientLight(0x555555))
    }

    init {
        window.onresize = {
            camera.onResize()
            renderer.onResize()
        }

        gameObjects.forEach { it.setup(gameState) }

        document.onkeydown = gameState::keyboardHandler
    }

    fun animate() {
        stats.begin()

        val time = clock.getDelta().toDouble()

        gameState.update(time)

        gameObjects.forEach { it.update(gameState, time) }

        //cameraAnimator.update(time)

        renderer.render(scene, camera)

        stats.end()

        window.requestAnimationFrame { animate() }
    }
}