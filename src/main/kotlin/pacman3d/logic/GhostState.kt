package pacman3d.logic

import pacman3d.logic.behaviors.*

enum class GhostState(val movementStrategy: GhostMovementStrategy) {

    Chase(ChaseMovementStrategy),
    Scatter(ScatterMovementStrategy),
    Frightened(FrightenedMovementStrategy),
    Eaten(ReturnToGhostHouseMovementStrategy),
    LeaveGhostHouse(LeaveGhostHouseMovementStrategy),
    InGhostHouse(InGhostHouseMovementStrategy);

    val canBeFrightened get() = isAnyOf(InGhostHouse, Chase, Scatter)

    val isFrightened get() = this == Frightened

    val canEatPacman get() = isNotAnyOf(Eaten, Frightened, InGhostHouse)

    private val isEaten get() = this == Eaten

    fun canSwitchToState(newState: GhostState) = this === newState || when (newState) {
        Chase -> this.isNotAnyOf(Eaten, InGhostHouse, LeaveGhostHouse)
        Scatter -> this.isNotAnyOf(Eaten, InGhostHouse, LeaveGhostHouse)
        Frightened -> this.isNotAnyOf(Eaten, InGhostHouse, LeaveGhostHouse) // TODO: Solve this. Ghost in house or leaving house should become frightened.
        Eaten -> this.isAnyOf(Frightened)
        LeaveGhostHouse -> this.isAnyOf(InGhostHouse)
        InGhostHouse -> true
    }

    private fun isAnyOf(vararg states: GhostState) = states.any { it === this }

    private fun isNotAnyOf(vararg states: GhostState) = states.none { it === this }
}