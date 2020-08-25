package pacman3d.logic

object GameData {

    val INKY_DOT_LIMIT = arrayOf(30, 0)
    val PINKY_DOT_LIMIT = arrayOf(0)
    val CLYDE_DOT_LIMIT = arrayOf(60, 50, 0)

    /**
     * Anytime Pac-Man avoids eating dots long enough for the timer to reach its limit, the most-preferred ghost waiting in the ghost house (if any) is forced to leave immediately
     */
    val LEAVE_GHOST_HOUSE_TIMES = arrayOf(4, 4, 4, 4, 3)

    private const val INDEFINITE = 0.0
    private const val ONE_FRAME = 1.0 / 60

    val SCATTER_CHASE_TIME_TABLE = arrayOf(
        /* Level 1    */ arrayOf(7.0, 20.0, 7.0, 20.0, 5.0, 20.0, 5.0, INDEFINITE),
        /* Levels 2-4 */ *Array(3) { arrayOf(7.0, 20.0, 7.0, 20.0, 5.0, 1033.0, ONE_FRAME, INDEFINITE) },
        /* Levels 5+  */ arrayOf(5.0, 20.0, 5.0, 20.0, 5.0, 1037.0, ONE_FRAME, INDEFINITE),
    )

    val FRIGHTENED_TIME  = arrayOf(6, 5, 4, 3, 2, 5, 2, 2, 1, 5, 2, 1, 1, 3, 1, 1, 0, 1, 0)
    val NUMBER_OF_FLASHES = arrayOf(5, 5, 5, 5, 5, 5, 5, 5, 3, 5, 5, 3, 3, 5, 3, 3, 0, 3, 0)
}