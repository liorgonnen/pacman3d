package pacman3d.logic.states

import org.w3c.dom.events.KeyboardEvent
import pacman3d.KEY_ARROW_DOWN
import pacman3d.KEY_ARROW_LEFT
import pacman3d.KEY_ARROW_RIGHT
import pacman3d.KEY_ARROW_UP
import pacman3d.entities.World
import pacman3d.logic.Direction.*

interface GameState {

    fun onEnterState(world: World)

    fun handleKeyboardEvent(stateMachine: GameStateMachine, world: World, event: KeyboardEvent)

    fun update(stateMachine: GameStateMachine, world: World, time: Double)

    fun onLeaveState(world: World)
}

abstract class AbsGameState : GameState {

    override fun onEnterState(world: World) = Unit

    override fun update(stateMachine: GameStateMachine, world: World, time: Double) = Unit

    override fun onLeaveState(world: World) = Unit

}

object WaitingForPlayer : AbsGameState() {

    override fun onEnterState(world: World) {
        world.setPacmanAndGhostsActive(false)
    }

    override fun onLeaveState(world: World) = with (world) {
        readyText.isVisible = false
        setPacmanAndGhostsActive(true)
    }

    override fun handleKeyboardEvent(stateMachine: GameStateMachine, world: World, event: KeyboardEvent) {
        stateMachine.currentState = Playing // Start the game on any key press
    }
}

object Paused : AbsGameState() {

    override fun handleKeyboardEvent(stateMachine: GameStateMachine, world: World, event: KeyboardEvent) {
        stateMachine.currentState = Playing // Start the game on any key press
    }
}

object Playing : AbsGameState() {

    override fun handleKeyboardEvent(stateMachine: GameStateMachine, world: World, event: KeyboardEvent) {
        with (world.pacman) {
            when (event.keyCode) {
                KEY_ARROW_UP -> requestDirection(UP)
                KEY_ARROW_DOWN -> requestDirection(DOWN)
                KEY_ARROW_LEFT -> requestDirection(LEFT)
                KEY_ARROW_RIGHT -> requestDirection(RIGHT)
            }
        }
    }
}

