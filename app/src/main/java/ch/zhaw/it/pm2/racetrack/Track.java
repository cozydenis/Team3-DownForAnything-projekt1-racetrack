package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.TrackSpecification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class represents the racetrack board.
 *
 * <p>The racetrack board consists of a rectangular grid of 'width' columns and 'height' rows.
 * The zero point of he grid is at the top left. The x-axis points to the right and the y-axis points downwards.</p>
 * <p>Positions on the track grid are specified using {@link PositionVector} objects. These are vectors containing an
 * x/y coordinate pair, pointing from the zero-point (top-left) to the addressed space in the grid.</p>
 *
 * <p>Each position in the grid represents a space which can hold an enum object of type {@link SpaceType}.<br>
 * Possible Space types are:
 * <ul>
 *  <li>WALL : road boundary or off track space</li>
 *  <li>TRACK: road or open track space</li>
 *  <li>FINISH_LEFT, FINISH_RIGHT, FINISH_UP, FINISH_DOWN :  finish line spaces which have to be crossed
 *      in the indicated direction to winn the race.</li>
 * </ul>
 * <p>Beside the board the track contains the list of cars, with their current state (position, velocity,...)</p>
 *
 * <p>At initialization the track grid data is read from the given track file. The track data must be a
 * rectangular block of text. Empty lines at the start are ignored. Processing stops at the first empty line
 * following a non-empty line, or at the end of the file.</p>
 * <p>Characters in the line represent SpaceTypes. The mapping of the Characters is as follows:</p>
 * <ul>
 *   <li>WALL : '#'</li>
 *   <li>TRACK: ' '</li>
 *   <li>FINISH_LEFT : '&lt;'</li>
 *   <li>FINISH_RIGHT: '&gt;'</li>
 *   <li>FINISH_UP   : '^;'</li>
 *   <li>FINISH_DOWN: 'v'</li>
 *   <li>Any other character indicates the starting position of a car.<br>
 *       The character acts as the id for the car and must be unique.<br>
 *       There are 1 to {@link TrackSpecification#MAX_CARS} allowed. </li>
 * </ul>
 *
 * <p>All lines must have the same length, used to initialize the grid width.<br/>
 * Beginning empty lines are skipped. <br/>
 * The track ends with the first empty line or the file end.<br>
 * An {@link InvalidFileFormatException} is thrown, if
 * <ul>
 *   <li>the file contains no track lines (grid height is 0)</li>
 *   <li>not all track lines have the same length</li>
 *   <li>the file contains no cars</li>
 *   <li>the file contains more than {@link TrackSpecification#MAX_CARS} cars</li>
 * </ul>
 *
 * <p>The Tracks {@link #toString()} method returns a String representing the current state of the race
 * (including car positions and status)</p>
 */
public class Track implements TrackSpecification {

    private SpaceType[][] grid;
    private final Map<Character, Car> cars;

    /**
     * Initialize a Track from the given track file.<br/>
     * See class description for structure and valid tracks.
     *
     * @param trackFile Reference to a file containing the track data
     * @throws IOException                if the track file can not be opened or reading fails
     * @throws InvalidFileFormatException if the track file contains invalid data
     *                                    (no track lines, inconsistent length, no cars)
     */

    public Track(File trackFile) throws IOException, InvalidFileFormatException {
        cars = new HashMap<>();
        Map<Integer, String> trackLines = readTrackFile(trackFile);
        processTrackLines(trackLines);
    }

    /*
     * Read the track file and return the track lines as a map of row number to line content.
     * @param trackFile the track file to read
     * @return the track lines as a map of row number to line content
     * @throws IOException if the track file can not be opened or reading fails
     * @throws InvalidFileFormatException if the track file contains invalid data
     * (no track lines, inconsistent length)
     */
    private Map<Integer, String> readTrackFile(File trackFile) throws IOException, InvalidFileFormatException {
        Map<Integer, String> trackLines = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(trackFile, StandardCharsets.UTF_8))) {
            String line;
            int width = -1;
            int height = 0;

            while ((line = reader.readLine()) != null) {
                // Skip empty lines at the beginning
                if (height == 0 && line.trim().isEmpty()) continue;

                if (width == -1) {
                    width = line.length();
                } else if (line.length() != width) {
                    throw new InvalidFileFormatException("Inconsistent track line length");
                }

                trackLines.put(height, line);
                height++;
            }

            if (height == 0) {
                throw new InvalidFileFormatException("The track file is empty or not properly formatted.");
            }

            grid = new SpaceType[height][width];
        } catch (IOException e) {
            throw new IOException("Failed to read the track file: " + e.getMessage(), e);
        }
        return trackLines;
    }

    /*
     * Process the track lines and initialize the grid and cars.
     * @param trackLines the track lines to process
     * @throws InvalidFileFormatException if the track file contains invalid data
     * (no cars, too many cars, duplicate car id)
     *
     */
    private void processTrackLines(Map<Integer, String> trackLines) throws InvalidFileFormatException {
        for (Map.Entry<Integer, String> entry: trackLines.entrySet()) {
            int row = entry.getKey();
            String trackLine = entry.getValue();
            for (int col = 0; col < grid[0].length; col++) {
                char spaceChar = trackLine.charAt(col);

                Optional<SpaceType> optionalSpaceType = SpaceType.spaceTypeForChar(spaceChar);
                if (optionalSpaceType.isPresent()) {
                    grid[row][col] = optionalSpaceType.get();
                } else {
                    // No valid space type, so this could be a car identifier
                    // Ensure it's a valid car character and not already used
                    if (!cars.containsKey(spaceChar)) {
                        if (cars.size() >= MAX_CARS) {
                            throw new InvalidFileFormatException("Amount of cars exceeds the maximum of " + MAX_CARS);
                        }

                        cars.put(spaceChar, new Car(spaceChar, new PositionVector(col, row)));
                        // Assuming cars are on TRACK spaces
                        grid[row][col] = SpaceType.TRACK;
                    } else {
                        // Handle the error of duplicate car identifiers
                        throw new InvalidFileFormatException("Duplicate car id: " + spaceChar);
                    }
                }
            }
        }

        if (cars.isEmpty()) {
            throw new InvalidFileFormatException("The track file contains no cars.");
        }
    }

    /**
     * Return the height (number of rows) of the track grid.
     *
     * @return Height of the track grid
     */
    public int getHeight() {
        return grid.length;
    }

    /**
     * Return the width (number of columns) of the track grid.
     *
     * @return the width of the track grid
     */
    public int getWidth() {
        return grid[0].length;
    }


    /**
     * Return the number of cars.
     *
     * @return the number of cars
     */
    @Override
    public int getCarCount() {
        return cars.size();
    }

    /**
     * Get instance of specified car.
     *
     * @param carIndex the zero-based carIndex number
     * @return the car instance at the given index
     */
    @Override
    public Car getCar(int carIndex) {
        return (Car) cars.values().toArray()[carIndex];
    }


    /**
     * Return the type of space at the given position.
     * If the location is outside the track bounds, it is considered a WALL.
     *
     * @param position the coordinates of the position to examine
     * @return the type of track position at the given location
     */
    @Override
    public SpaceType getSpaceTypeAtPosition(PositionVector position) {
        if (position.getX() < 0 || position.getX() >= getWidth() || position.getY() < 0 || position.getY() >= getHeight()) {
            return SpaceType.WALL;
        }
        return grid[position.getY()][position.getX()];
    }

    /**
     * Gets the character representation for the given position of the racetrack, including cars.<br/>
     * This can be used for generating the {@link #toString()} representation of the racetrack.<br/>
     * If there is an active car (not crashed) at the given position, then the car id is returned.<br/>
     * If there is a crashed car at the position, {@link #CRASH_INDICATOR} is returned.<br/>
     * Otherwise, the space character for the given position is returned
     *
     * @param row row (y-value) of the racetrack position
     * @param col column (x-value) of the racetrack position
     * @return character representing the position (col,row) on the track
     * or {@link Car#getId()} resp. {@link #CRASH_INDICATOR}, if a car is at the given position
     */
    @Override
    public char getCharRepresentationAtPosition(int row, int col) {
        for (Car car: cars.values()) {
            if (car.getPosition().getX() == col && car.getPosition().getY() == row) {
                if (car.isCrashed()) {
                    return CRASH_INDICATOR;
                } else {
                    return car.getId();
                }
            }
        }
        return grid[row][col].getSpaceChar();
    }

    /**
     * Return a String representation of the track, including the car locations and status.
     *
     * @return a String representation of the track
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                sb.append(getCharRepresentationAtPosition(row, col));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public SpaceType[][] getTrack() {
        return this.grid;
    }

    /**
     * Return the map of cars on the track.
     *
     * @return the map of cars on the track
     */
    public Map<Character, Car> getCars() {
        return cars;
    }
}
