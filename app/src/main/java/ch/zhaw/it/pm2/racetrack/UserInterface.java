package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.strategy.DoNotMoveStrategy;
import ch.zhaw.it.pm2.racetrack.strategy.MoveListStrategy;
import ch.zhaw.it.pm2.racetrack.strategy.UserMoveStrategy;
import ch.zhaw.it.pm2.racetrack.strategy.PathFollowerMoveStrategy;
import ch.zhaw.it.pm2.racetrack.strategy.PathFinderMoveStrategy;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import ch.zhaw.it.pm2.racetrack.strategy.MoveStrategy;

import java.io.File;

/**
 * Class representing the user interface of the game.
 */
public class UserInterface {

    private static final TextIO textIO = TextIoFactory.getTextIO();

    /**
     * Prints a welcome message to the user.
     */
    public static void printWelcome() {
        textIO.getTextTerminal().println("Welcome to Racetrack!");
    }

    /**
     * Prints a prompt to the user to select a track.
     *
     * @param listOfFiles the list of files representing the tracks
     */
    public static void printTrackSelectionPrompt(File[] listOfFiles) {
        textIO.getTextTerminal().println("Please select a track:");
        for (int i = 0; i < listOfFiles.length; i++) {
            textIO.getTextTerminal().println(i + 1 + ": " + listOfFiles[i].getName());
        }
    }

    /**
     * Reads the track number from the user.
     *
     * @param maxVal the maximum value the user can enter
     * @return the track number
     */
    public static int readTrackNumber(int maxVal) {
        return textIO.newIntInputReader()
            .withMinVal(1)
            .withMaxVal(maxVal)
            .read("Enter the number of the track you want to select:");
    }

    /**
     * Prompts the user to select a move strategy for a car.
     *
     * @param car   the car for which the move strategy is selected
     * @param track the track on which the car is placed
     * @return the selected move strategy
     */
    public static MoveStrategy promptMoveStrategy(Car car, Track track) {
        textIO.getTextTerminal().println("\nPlease select a move strategy for car " + car.getId() + ":");
        for (MoveStrategy.StrategyType strategyType: MoveStrategy.StrategyType.values()) {
            textIO.getTextTerminal().println(strategyType.ordinal() + 1 + ": " + strategyType);
        }

        int strategyIndex = textIO.newIntInputReader()
            .withMinVal(1)
            .withMaxVal(MoveStrategy.StrategyType.values().length)
            .read("Enter the number of the move strategy you want to select:") - 1;

        return switch (MoveStrategy.StrategyType.values()[strategyIndex]) {
            case DO_NOT_MOVE -> new DoNotMoveStrategy();
            case USER -> new UserMoveStrategy();
            case MOVE_LIST -> new MoveListStrategy("src/main/resources/moves/challenge-car-a.txt");
            case PATH_FOLLOWER ->
                new PathFollowerMoveStrategy("src/main/resources/follower/challenge_handout_points.txt", car);
            case PATH_FINDER -> new PathFinderMoveStrategy(car, track);
        };
    }

    /**
     * Reads the next direction from the user.
     *
     * @return the next direction
     */
    public static String getUserInputDirection() {
        Direction direction = null;
        while (direction == null) {
            String userInput = textIO.newStringInputReader().read("What is the next direction you want to take? (UP, DOWN, LEFT, RIGHT, NONE, QUIT, DOWN_RIGHT, UP_RIGHT, UP_LEFT, DOWN_LEFT)");
            try {
                direction = Direction.valueOf(userInput.toUpperCase());
            } catch (IllegalArgumentException e) {
                if (userInput.equalsIgnoreCase("QUIT"))
                    System.exit(0);
                textIO.getTextTerminal().println("Invalid direction. Please enter either 'UP', 'DOWN', 'LEFT', 'RIGHT', 'NONE', 'QUIT', 'DOWN_RIGHT', 'UP_RIGHT', 'UP_LEFT', 'DOWN_LEFT'.");
            }
        }
        return direction.toString();
    }

    /**
     * Prints a message to the user.
     *
     * @param something the message to print
     */
    public static void printSomething(String something) {
        textIO.getTextTerminal().println(something);
    }

    /**
     * Prints the game status to the user.
     *
     * @param track           the track on which the game is played
     * @param currentCarIndex the index of the current car
     */
    public static void printStatus(Track track, char currentCarIndex) {
        textIO.getTextTerminal().println("Game status:");
        textIO.getTextTerminal().println(track.toString());
        textIO.getTextTerminal().println("Current turn: " + currentCarIndex);
    }

    /**
     * Prints crash information to the user.
     *
     * @param id       the id of the car that crashed
     * @param position the position of the car that crashed
     * @param reason   the reason why the car crashed
     */
    public static void printCrashInfo(char id, PositionVector position, String reason) {
        textIO.getTextTerminal().println("Car <" + id + "> crashed at position " + position + ": " + reason);
    }

    /**
     * Prints the final game status to the user.
     *
     * @param track the track on which the game was played
     * @param carId the id of the car that won the game
     */
    public static void printFinalGame(Track track, char carId) {
        textIO.getTextTerminal().println("Final game status:");
        textIO.getTextTerminal().println(track.toString());

        textIO.getTextTerminal().println("Car <" + carId + "> wins the game!");
    }

    /**
     * Prompts the user to quit the game.
     */
    public static void promptQuit() {
        textIO.getTextTerminal().println("Type 'q' to quit the game...");
        String input = textIO.newStringInputReader().read("");
        if (input.equalsIgnoreCase("q")) {
            System.exit(0);
        }
    }
}
