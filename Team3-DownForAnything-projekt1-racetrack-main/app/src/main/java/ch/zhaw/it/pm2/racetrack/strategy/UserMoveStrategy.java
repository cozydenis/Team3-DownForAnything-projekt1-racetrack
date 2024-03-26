package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Direction;
import ch.zhaw.it.pm2.racetrack.UserInterface;

/**
 * Let the user decide the next move.
 */
public class UserMoveStrategy implements MoveStrategy {

    /**
     * This method is used to determine the next move of the user.
     * It prompts the user to enter a direction and then converts the user's input into a Direction enum using the readDirection method from the MoveStrategy interface.
     * The method continues to prompt the user until a valid input is entered.
     * The valid inputs are "UP", "DOWN", "LEFT", "RIGHT", "NONE", "QUIT".
     * If the user enters "QUIT", the method returns null, terminating the game.
     * If the user enters any other string, the method returns the corresponding Direction enum.
     *
     * @return The corresponding Direction enum. Returns null if the user enters "QUIT".
     */
    @Override
    public Direction nextMove() {
        Direction chosenDirection;
        String userInput = UserInterface.getUserInputDirection();
        chosenDirection = MoveStrategy.readDirection(userInput);
        return chosenDirection;
    }

    public Direction nextMove(String userInput) {
        Direction chosenDirection;
        chosenDirection = MoveStrategy.readDirection(userInput);
        return chosenDirection;
    }
}
