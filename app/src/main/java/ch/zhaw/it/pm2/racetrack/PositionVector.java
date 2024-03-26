package ch.zhaw.it.pm2.racetrack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds a position (vector to x,y-position of the car on the track grid)
 * or a velocity vector (x,y-components of the velocity vector of a car).<br/>
 * PositionVectors are immutable, which means they cannot be modified.<br/>
 * Vector operations like {@link #add(PositionVector)} and {@link #subtract(PositionVector)}
 * return a new PositionVector containing the result.
 *
 * @author mach
 * @version FS2023
 */
public final class PositionVector {
    /**
     * Format to print the position vector.
     */
    private static final String POSITION_VECTOR_FORMAT = "(X:%d, Y:%d)";

    /**
     * Pattern to parse a position vector from string format.
     */
    private static final Pattern POSITION_VECTOR_PATTERN = Pattern.compile("\\(X:(\\d+), Y:(\\d+)\\)");

    /**
     * horizontal value (position / velocity).
     */
    private final int x;

    /**
     * vertical value (position / velocity).
     */
    private final int y;


    /**
     * Base constructor, initializing the position using coordinates or a velocity vector.
     *
     * @param x horizontal value (position or velocity)
     * @param y vertical value (position or velocity)
     */
    public PositionVector(final int x, final int y) {
        this.y = y;
        this.x = x;
    }

    /**
     * Copy constructor, copying the values from another PositionVector.
     *
     * @param other position vector to copy from
     */
    public PositionVector(final PositionVector other) {
        this.x = other.getX();
        this.y = other.getY();
    }

    /**
     * @return the horizontal value (position or velocity)
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return vertical value (position or velocity)
     */
    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof final PositionVector otherVector)) return false;
        return this.y == otherVector.getY() && this.x == otherVector.getX();
    }

    @Override
    public int hashCode() {
        return this.x ^ this.y;
    }

    @Override
    public String toString() {
        return POSITION_VECTOR_FORMAT.formatted(this.x, this.y);
    }

    /**
     * Calculates the vector addition of the current vector with the given vector, e.g.
     * <ul>
     *   <li>if a velocity vector is added to a position, the next position is returned</li>
     *   <li>if a direction vector is added to a velocity, the new velocity is returned</li>
     * </ul>
     * The vectors values are not modified, but a new Vector containing the result is returned.
     *
     * @param vector a position or velocity vector to add
     * @return A new PositionVector holding the result of the addition.
     */
    public PositionVector add(final PositionVector vector) {
        return new PositionVector(this.getX() + vector.getX(), this.getY() + vector.getY());
    }

    /**
     * Calculates the vector difference of the current vector to the given vector,
     * i.e. subtracts the given from the current vectors coordinates. (e.g. car position and/or velocity vector) <br>
     * The vectors values are not modified, but a new Vector containing the result is returned.
     *
     * @param vector A position or velocity vector to subtract
     * @return A new PositionVector holding the result of the subtraction.
     */
    public PositionVector subtract(final PositionVector vector) {
        return new PositionVector(this.getX() - vector.getX(), this.getY() - vector.getY());
    }

    /**
     * Parses a position vector from a string in the format (X:1, Y:2).
     * This is the format produced by {@link #toString()}.
     *
     * @param positionString string to parse
     * @return parsed position vector
     */
    public static PositionVector parsePositionVector(String positionString) {
        Matcher matcher = POSITION_VECTOR_PATTERN.matcher(positionString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("String does not match position vector pattern: " + positionString);
        }
        return new PositionVector(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    public Double distance(PositionVector a) {
        return Math.sqrt(Math.pow(a.getX() - this.getX(), 2) + Math.pow(a.getY() - this.getY(), 2));
    }
}
