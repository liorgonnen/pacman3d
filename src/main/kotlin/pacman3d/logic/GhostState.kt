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

    val canBeEaten get() = this == Frightened

    val canEatPacman get() = isAnyOf(Chase, Scatter)

    private val isEaten get() = this == Eaten

    fun canSwitchToState(newState: GhostState) = this === newState || when (newState) {
        Chase -> !isEaten
        Scatter -> !isEaten
        Frightened -> !isEaten
        Eaten -> this.isAnyOf(Frightened)
        LeaveGhostHouse -> this.isAnyOf(InGhostHouse)
        InGhostHouse -> true
    }

    fun isAnyOf(vararg states: GhostState) = states.any { it === this }

    fun isNotAnyOf(vararg states: GhostState) = states.none { it === this }
}