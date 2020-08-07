package pacman3d.entities

import pacman3d.ext.plusAssign
import pacman3d.logic.Direction.RIGHT
import pacman3d.logic.Position
import three.js.Scene

class World {

    val maze = Maze()

    val dots = Dots()

    val score = Score()

    val gameStateBanner = GameStateBanner()

    val pacmanLives = PacManLives()

    val bonusPoints = BonusPoints()

    val pacman = PacMan(
        initialDirection = RIGHT,
        initialPosition = Position(15.0, 26.5)
    )

    val blinky = Blinky()

    val clyde = Clyde()

    val inky = Inky()

    val pinky = Pinky()

    val ghosts = arrayOf(
        blinky,
        clyde,
        inky,
        pinky,
    )

    private val entities = arrayOf(
        maze,
        dots,
        score,
        pacman,
        bonusPoints,
        gameStateBanner,
        pacmanLives,
        *ghosts,
    )

    val preferredOrderToLeaveHouse = arrayOf(pinky, inky, clyde)

    init {
        setup()
    }

    private fun setup() {
        entities.forEach { it.setup(this) }
    }

    fun addRenderablesToScene(scene: Scene) {
        entities.forEach { scene += it.renderable }
    }

    fun update(time: Double) = with (entities) {
        forEach { if (it.isActive) it.update(this@World, time) }

        forEach { if (it.isVisible) it.renderable.update(this@World, time) }
    }

    fun setPacmanAndGhostsActive(active: Boolean) {
        pacman.isActive = active
        ghosts.forEach { it.isActive = active }
    }

    fun resetState() {
        entities.forEach { it.resetState(this) }
    }

    fun onVisualEffectBegin() {
        setPacmanAndGhostsActive(false)
    }

    fun onVisualEffectEnd() {
        setPacmanAndGhostsActive(true)
    }
}