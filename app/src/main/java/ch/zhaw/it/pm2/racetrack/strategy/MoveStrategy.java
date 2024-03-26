package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Direction;

/**
 * Move Strategy, the different cars may choose to determine the next direction to accelerate.
 */
public interface MoveStrategy {
    /**
     * Determine direction to accelerate in the next move.
     *
     * @return Direction vector to accelerate in the next move. null will terminate the game.
     */
    Direction nextMove();

    /**
     * Possible Move Strategies which can be selected. This shall not be altered!
     */
    enum StrategyType {
        DO_NOT_MOVE, USER, MOVE_LIST, PATH_FOLLOWER, PATH_FINDER
    }

    /**
     * This method is used to convert a string direction into a Direction enum.
     * The string direction is converted to uppercase and then matched with the corresponding Direction enum.
     * If the string direction is empty or null, the method returns null.
     * If the string direction does not match any Direction enum, the method also returns null.
     *
     * @param direction The string representation of the direction. It can be "UP", "DOWN", "LEFT", "RIGHT",
     *                  "DOWN_RIGHT", "DOWN_LEFT", "UP_RIGHT", "UP_LEFT", "NONE" or any other string.
     * @return The corresponding Direction enum. Returns null if the string direction is empty, null or does not match any Direction enum.
     */
    static Direction readDirection(String direction) {
        if (direction == null || direction.isEmpty()) {
            return null;
        } else {
            return switch (direction.toUpperCase()) {
                case "UP" -> Direction.UP;
                case "DOWN" -> Direction.DOWN;
                case "LEFT" -> Direction.LEFT;
                case "RIGHT" -> Direction.RIGHT;
                case "DOWN_RIGHT" -> Direction.DOWN_RIGHT;
                case "DOWN_LEFT" -> Direction.DOWN_LEFT;
                case "UP_RIGHT" -> Direction.UP_RIGHT;
                case "UP_LEFT" -> Direction.UP_LEFT;
                case "NONE" -> Direction.NONE;
                default -> null;
            };
        }
    }
}
