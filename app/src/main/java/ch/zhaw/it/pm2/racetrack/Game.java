package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.GameSpecification;
import ch.zhaw.it.pm2.racetrack.strategy.MoveStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Game controller class, performing all actions to modify the game state.
 * It contains the logic to switch and move the cars, detect if they are crashed
 * and if we have a winner.
 * It also acts as a facade to track and car information, to get game state information.
 */
public class Game implements GameSpecification {
    private final Track track;
    private int currentCarIndex;
    private int winner;
    public static final int NO_WINNER = -1;

    /**
     * Constructor for the Game class.
     *
     * @param track the track to be used for this game
     */
    public Game(final Track track) {
        this.track = track;
        this.currentCarIndex = 0;
        this.winner = NO_WINNER;
    }

    /**
     * Starts the game loop, continuously moving cars until a winner is declared.
     */
    public void startGame() {
        while (getWinner() == NO_WINNER) {
            UserInterface.printStatus(track, track.getCar(currentCarIndex).getId());

            Direction acceleration = track.getCar(currentCarIndex).getMoveStrategy().nextMove();
            doCarTurn(acceleration);

            if (getWinner() != NO_WINNER) {
                declareWinner();
                break;
            }
        }
    }

    /**
     * Announces the winner of the game by printing the winning car's ID.
     */
    private void declareWinner() {
        UserInterface.printFinalGame(track, getCarId(getWinner()));
        UserInterface.promptQuit();
    }


    /**
     * Return the number of cars on the track.
     *
     * @return the number of cars
     */
    @Override
    public int getCarCount() {
        return track.getCarCount();
    }

    /**
     * Return the index of the current active car.
     * Car indexes are zero-based, so the first car is 0, and the last car is getCarCount() - 1.
     *
     * @return the zero-based number of the current car
     */
    @Override
    public int getCurrentCarIndex() {
        return currentCarIndex;
    }

    /**
     * Get the id of the specified car.
     *
     * @param carIndex The zero-based carIndex number
     * @return a char containing the id of the car
     */
    @Override
    public char getCarId(int carIndex) {
        return track.getCar(carIndex).getId();
    }

    /**
     * Get the position of the specified car.
     *
     * @param carIndex The zero-based carIndex number
     * @return a PositionVector containing the car's current position
     */
    @Override
    public PositionVector getCarPosition(int carIndex) {
        return track.getCar(carIndex).getPosition();
    }

    /**
     * Get the velocity of the specified car.
     *
     * @param carIndex The zero-based carIndex number
     * @return a PositionVector containing the car's current velocity
     */
    @Override
    public PositionVector getCarVelocity(int carIndex) {
        return track.getCar(carIndex).getVelocity();
    }

    /**
     * Set the {@link MoveStrategy} for the specified car.
     *
     * @param carIndex        The zero-based carIndex number
     * @param carMoveStrategy the {@link MoveStrategy} to be associated with the specified car
     */
    @Override
    public void setCarMoveStrategy(int carIndex, MoveStrategy carMoveStrategy) {
        Car car = track.getCar(carIndex);
        car.setMoveStrategy(carMoveStrategy); //Given there is will be a method in the Car class to set the move strategy
    }

    /**
     * Get the next move for the specified car, depending on its {@link MoveStrategy}.
     *
     * @param carIndex The zero-based carIndex number
     * @return the {@link Direction} containing the next move for the specified car
     */
    @Override
    public Direction nextCarMove(int carIndex) {
        return track.getCar(carIndex).getMoveStrategy().nextMove();
    }

    /**
     * Return the carIndex of the winner.<br/>
     * If the game is still in progress, returns {@link #NO_WINNER}.
     *
     * @return the winning car's index (zero-based, see {@link #getCurrentCarIndex()}),
     * or {@link #NO_WINNER} if the game is still in progress
     */
    @Override
    public int getWinner() {
        return winner;
    }

    /**
     * Executes the next turn for the current active car. This method manages the sequence of actions
     * that occur during a car's turn, including acceleration, movement, and post-movement processes.
     *
     * @param acceleration a Direction containing the current car's acceleration vector (-1,0,1) in x and y direction
     *                     for this turn.
     */
    @Override
    public void doCarTurn(Direction acceleration) {
        Car currentCar = track.getCar(currentCarIndex);

        if (currentCar.isCrashed()) {
            switchToNextActiveCar();
            return;
        }

        processCarMovement(currentCar, acceleration);
        processCarPostMovement(currentCar);
    }

    /**
     * Processes the movement of the car including acceleration and path calculation.
     * It iterates through the path and applies position-specific logic.
     *
     * @param currentCar   the car that is currently taking its turn.
     * @param acceleration the acceleration direction for the current turn.
     */
    private void processCarMovement(Car currentCar, Direction acceleration) {
        currentCar.accelerate(acceleration);
        PositionVector startPosition = currentCar.getPosition();
        PositionVector endPosition = currentCar.nextPosition();
        List<PositionVector> path = calculatePath(startPosition, endPosition);

        for (PositionVector position: path) {
            if (processPositionForCar(currentCar, position)) break;
        }
    }

    /**
     * Processes the action for the car at a given position. This includes handling collisions,
     * moving through track, and crossing the finish line.
     *
     * @param currentCar the car that is currently being processed.
     * @param position   the position to process for the given car.
     */
    private boolean processPositionForCar(Car currentCar, PositionVector position) {
        SpaceType spaceType = track.getSpaceTypeAtPosition(position);
        if (checkCollisionWithOtherCars(position)) {
            currentCar.crash(position);
            UserInterface.printCrashInfo(currentCar.getId(), position, "car collision");
            return true;
        }
        return switch (spaceType) {
            case WALL -> handleWallCollision(currentCar, position);
            case TRACK -> false;
            case FINISH_LEFT, FINISH_RIGHT, FINISH_UP, FINISH_DOWN -> handleFinishLine(currentCar, spaceType);
        };
    }

    /**
     * Handles the scenario where the car collides with a wall.
     *
     * @param car      the car that collided with the wall.
     * @param position the position of the collision.
     */
    private boolean handleWallCollision(Car car, PositionVector position) {
        car.crash(position);
        UserInterface.printCrashInfo(car.getId(), position, "wall collision");
        return true;
    }

    /**
     * Processes a position on the track to check for and handle collisions with other cars.
     *
     * @param car      the car being processed.
     * @param position the position to check for collisions.
     */
    private boolean handleTrackPosition(Car car, PositionVector position) {

        return false;
    }

    /**
     * Handles the car's interaction with the finish line, determining if the car crosses the finish line correctly.
     *
     * @param car       the car being processed.
     * @param spaceType the type of space the car is moving into, should be a finish line variant.
     */
    public boolean handleFinishLine(Car car, SpaceType spaceType) {
        if (checkFinishLineCrossing(spaceType, car)) {
            setWinner(currentCarIndex);
            return true;
        }
        return false;
    }

    /**
     * Executes post-movement processes for a car, including moving the car, checking for a winner,
     * and switching to the next car if necessary.
     *
     * @param currentCar the car that has just completed its turn.
     */
    private void processCarPostMovement(Car currentCar) {
        if (!currentCar.isCrashed()) {
            currentCar.move();
        }

        if (countActiveCars() == 1) {
            setLastRemainingCarAsWinner();
        }

        if (winner == NO_WINNER) {
            switchToNextActiveCar();
        }
    }

    /**
     * Sets the last remaining car as the winner of the game. This is called when only one car
     * has not crashed.
     */
    private void setLastRemainingCarAsWinner() {
        for (int i = 0; i < track.getCarCount(); i++) {
            if (!track.getCar(i).isCrashed()) {
                winner = i;
                break;
            }
        }
    }

    /**
     * Switches to the next car who is still in the game. Skips crashed cars.
     */
    @Override
    public void switchToNextActiveCar() {
        do {
            currentCarIndex = (currentCarIndex + 1) % track.getCarCount();

            // If we loop around and get back to the same car, break to avoid an infinite loop
            // This condition triggers when all other cars have crashed, and only one is left active
        } while (track.getCar(currentCarIndex).isCrashed() && currentCarIndex != winner);

    }

    /**
     * Returns all the grid positions in the path between two positions, for use in determining line of sight. <br>
     * Determine the 'pixels/positions' on a raster/grid using Bresenham's line algorithm.
     * (<a href="https://de.wikipedia.org/wiki/Bresenham-Algorithmus">Bresenham-Algorithmus</a>)<br>
     * Basic steps are <ul>
     * <li>Detect which axis of the distance vector is longer (faster movement)</li>
     * <li>for each pixel on the 'faster' axis calculate the position on the 'slower' axis.</li>
     * </ul>
     * Direction of the movement has to correctly considered.
     *
     * @param startPosition Starting position as a PositionVector
     * @param endPosition   Ending position as a PositionVector
     * @return intervening grid positions as a List of PositionVector's, including the starting and ending positions.
     */
    @Override
    public List<PositionVector> calculatePath(PositionVector startPosition, PositionVector endPosition) {
        List<PositionVector> path = new ArrayList<>();
        int x0 = startPosition.getX();
        int y0 = startPosition.getY();
        int x1 = endPosition.getX();
        int y1 = endPosition.getY();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            path.add(new PositionVector(x0, y0));
            if (x0 == x1 && y0 == y1) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }

        return path;
    }

    /**
     * Checks if the specified position results in a collision with any of the other cars on the track.
     *
     * @param position The position to check for collisions.
     * @return true if there is a collision with another car at the specified position, false otherwise.
     */
    private boolean checkCollisionWithOtherCars(PositionVector position) {
        boolean collision = false;
        for (int i = 0; i < track.getCarCount(); i++) {
            if (i != currentCarIndex) {
                Car otherCar = track.getCar(i);
                if (otherCar.getPosition().equals(position) && !otherCar.isCrashed()) {
                    collision = true;
                    break;
                }
            }
        }
        return collision;
    }

    /**
     * Checks if crossing the finish line is valid based on the car's current velocity and the space type.
     *
     * @param spaceType The type of space the car is moving into.
     * @param car       The car that is crossing the finish line.
     * @return true if the car crosses the finish line correctly, false otherwise.
     */
    private boolean checkFinishLineCrossing(SpaceType spaceType, Car car) {
        boolean crossing = false;

        switch (spaceType) {
            case FINISH_LEFT:
                crossing = crossFinishLine(car, car.getVelocity().getX() < 0);
                break;
            case FINISH_RIGHT:
                crossing = crossFinishLine(car, car.getVelocity().getX() > 0);
                break;
            case FINISH_UP:
                crossing = crossFinishLine(car, car.getVelocity().getY() < 0);
                break;
            case FINISH_DOWN:
                crossing = crossFinishLine(car, car.getVelocity().getY() > 0);
                break;
            default:
                break;
        }
        return crossing;
    }

    /**
     * Checks if the car has crossed the finish line correctly, and updates the car's state accordingly.
     *
     * @param car       The car that is crossing the finish line.
     * @param isForward A boolean indicating if the car is moving forward or backward.
     * @return true if the car has crossed the finish line correctly, false otherwise.
     */
    private boolean crossFinishLine(Car car, boolean isForward) {
        if (isForward) {
            if (car.getRemainingLaps() == 1) {
                return true;
            } else {
                car.goesOverFinishLine();
            }
        } else {
            car.goesOverFinishLineBackwards();
        }
        return false;
    }

    /**
     * Counts the number of cars that have not crashed.
     *
     * @return The number of active (non-crashed) cars on the track.
     */
    private int countActiveCars() {
        int activeCarCount = 0;
        for (int i = 0; i < track.getCarCount(); i++) {
            if (!track.getCar(i).isCrashed()) {
                activeCarCount++;
            }
        }
        return activeCarCount;
    }

    /**
     * Sets the specified car as the winner of the game.
     *
     * @param carIndex The index of the car to set as the winner.
     */
    private void setWinner(int carIndex) {
        winner = carIndex;
    }
}
