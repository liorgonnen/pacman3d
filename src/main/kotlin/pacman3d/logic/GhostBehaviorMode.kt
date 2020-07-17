package pacman3d.logic

enum class GhostBehaviorMode {

    /**
     * A ghost's objective in chase mode is to find and capture Pac-Man by hunting him down through the maze.
     * Each ghost exhibits unique behavior when chasing Pac-Man, giving them their different personalities: Blinky (red)
     * is very aggressive and hard to shake once he gets behind you, Pinky (pink) tends to get in front of you and cut
     * you off, Inky (light blue) is the least predictable of the bunch, and Clyde (orange) seems to do his own thing
     * and stay out of the way.
     */
    Chase,

    /**
     * In scatter mode, the ghosts give up the chase for a few seconds and head for their respective home corners.
     */
    Scatter,

    /**
     * Ghosts enter frightened mode whenever Pac-Man eats one of the four pills. During the early levels, the ghosts
     * will all turn dark blue (vulnerable) and aimlessly wander the maze for a few seconds.
     * They will flash moments before returning to their previous mode of behavior.
     */
    Frightened
}