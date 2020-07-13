package pacman3d

import kotlinx.browser.document
import kotlinx.browser.window
import pacman3d.gameobjects.Dots
import pacman3d.ext.*
import pacman3d.maze.Maze
import pacman3d.maze.MazeGeometryBuilder
import pacman3d.state.GameState
import stats.js.Stats
import three.js.*

class Game {
    private val clock = Clock()

    private val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.set(0, 25, 20)
        lookAt(0, 0, 0)
    }

    private val renderer = WebGLRenderer().init(clearColor = 0x333333)

    private val dots = Dots()

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

    private val plane = Mesh(PlaneGeometry(Maze.WIDTH, Maze.EFFECTIVE_LENGTH, 5), MeshPhongMaterial().apply { color = Color(0) }).apply {
        rotateX(-HALF_PI)
    }

    private val maze = Mesh(MazeGeometryBuilder().build(), 0x151FCD.toMeshPhongMaterial())

    private val scene = Scene().apply {
        add(plane)
        add(maze)
        add(dots)

        add(DirectionalLight(0xffffff, 1).apply { position.set(-1, 2, 4) })
        add(AmbientLight(0x404040, 1))
    }

    init {
        window.onresize = {
            camera.onResize()
            renderer.onResize()
        }

        dots.setup(gameState)
    }

    fun animate() {
        stats.begin()

        renderer.render(scene, camera)

        stats.end()

        window.requestAnimationFrame { animate() }
    }
}