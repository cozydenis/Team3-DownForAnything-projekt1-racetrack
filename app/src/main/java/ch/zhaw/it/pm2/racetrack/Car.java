package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.CarSpecification;
import ch.zhaw.it.pm2.racetrack.strategy.MoveStrategy;

/**
 * Class representing a car on the racetrack.<br/>
 * Uses {@link PositionVector} to store current position on the track grid and current velocity vector.<br/>
 * Each car has an identifier character which represents the car on the racetrack board.<br/>
 * Also keeps the state, if the car is crashed (not active anymore).
 * The state can not be changed back to uncrashed.<br/>
 * The velocity is changed by providing an acceleration vector.<br/>
 * The car is able to calculate the endpoint of its next position and on request moves to it.<br/>
 */
public class Car implements CarSpecification {

    /**
     * Car identifier used to represent the car on the track.
     */
    private final char id;
    private int remainingLaps;
    private boolean crashed;
    private PositionVector position;
    private PositionVector currentVelocity;
    MoveStrategy moveStrategy;

    /**
     * Constructor for class Car.
     *
     * @param id            unique Car identification
     * @param startPosition initial position of the Car
     */
    public Car(char id, PositionVector startPosition) {
        this.id = id;
        this.position = startPosition;
        this.currentVelocity = Direction.NONE.vector;
        this.moveStrategy = null;
        this.crashed = false;
        remainingLaps = 1;
    }

    /**
     * Returns Identifier of the car, which represents the car on the track.
     *
     * @return the identifier character
     */
    @Override
    public char getId() {
        return this.id;
    }

    /**
     * Returns the current immutable position of the car on the track as a {@link PositionVector}.
     *
     * @return the car's current position
     */
    @Override
    public PositionVector getPosition() {
        return this.position;
    }

    /**
     * Returns the current immutable velocity vector of the car as a {@link PositionVector}.
     *
     * @return the car's current velocity vector
     */
    @Override
    public PositionVector getVelocity() {
        return this.currentVelocity;
    }

    /**
     * Return the position that will apply after the next move at the current velocity.
     * Does not complete the move, so the current position remains unchanged.
     *
     * @return expected position after the next move
     */
    @Override
    public PositionVector nextPosition() {
        int newX = position.getX() + currentVelocity.getX();
        int newY = position.getY() + currentVelocity.getY();
        return new PositionVector(newX, newY);
    }

    /**
     * Add the specified amounts to this car's velocity.<br/>
     * The only acceleration values allowed are -1, 0 or 1 in both axis<br/>
     * There are 9 possible acceleration vectors, which are defined in {@link Direction}.<br/>
     * Changes only velocity, not position.<br/>
     *
     * @param acceleration a Direction vector containing the amounts to add to the velocity in x and y dimension
     */
    @Override
    public void accelerate(Direction acceleration) {
        if (acceleration == null) {
            throw new IllegalArgumentException("Illegal acceleration: acceleration cannot be null");
        }
        currentVelocity = currentVelocity.add(acceleration.vector);
    }

    /**
     * Update this Car's position based on its current velocity.
     */
    @Override
    public void move() {
        int newX = position.getX() + currentVelocity.getX();
        int newY = position.getY() + currentVelocity.getY();
        position = new PositionVector(newX, newY);
    }

    public void setVelocity(PositionVector velocity) {
        this.currentVelocity = velocity;
    }

    /**
     * Mark this Car as being crashed at the given position.
     *
     * @param crashPosition position the car crashed.
     */
    @Override
    public void crash(PositionVector crashPosition) {
        crashed = true;
    }

    /**
     * Returns whether this Car has been marked as crashed.
     *
     * @return true if crash() has been called on this Car, false otherwise.
     */
    @Override
    public boolean isCrashed() {
        return crashed;
    }

    public void setMoveStrategy(MoveStrategy strategy) {
        this.moveStrategy = strategy;
    }

    //for testing
    public MoveStrategy getMoveStrategy() {
        return this.moveStrategy;
    }

    /**
     * Sets the position of the car to the specified position vector.
     *
     * @param position The new position vector for the car.
     */

    public void setPosition(PositionVector position) {
        this.position = position;
    }

    /**
     * Reduce the number of remaining laps by one.
     */
    public void goesOverFinishLine() {
        remainingLaps--;
    }

    /**
     * Adds one to the number of remaining laps.
     */
    public void goesOverFinishLineBackwards() {
        remainingLaps++;
    }

    /**
     * Returns the number of remaining laps.
     *
     * @return the number of remaining laps
     */
    public int getRemainingLaps() {
        return remainingLaps;
    }
}
