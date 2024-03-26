package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Direction;
import ch.zhaw.it.pm2.racetrack.UserInterface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Determines the next move based on a file containing a list of directions.
 */
public class MoveListStrategy implements MoveStrategy {

    /**
     * Constructs a new MoveListStrategy object.
     *
     * @param path The path to the file containing the list of directions.
     */
    public MoveListStrategy(String path) {
        directionList = loadMovesFromFile(path);
    }

    List<Direction> directionList = new ArrayList<>();

    /**
     * {@inheritDoc}
     *
     * @return next direction from move file or NONE, if no more moves are available.
     */
    @Override
    public Direction nextMove() {
        if (directionList.isEmpty()) {
            return Direction.NONE;
        } else {
            return directionList.removeFirst();
        }
    }

    /**
     * Reads the move file and stores the directions in a list.
     *
     * @param filename file containing the list of directions
     */
    public List<Direction> loadMovesFromFile(String filename) {
        List<Direction> moves = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                moves.add(MoveStrategy.readDirection(line));
            }
        } catch (IOException e) {
            UserInterface.printSomething("Error reading moves file: " + e.getMessage());
        }
        return moves;
    }


}
