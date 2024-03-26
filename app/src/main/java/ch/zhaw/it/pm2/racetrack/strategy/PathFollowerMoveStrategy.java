package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Car;
import ch.zhaw.it.pm2.racetrack.Direction;
import ch.zhaw.it.pm2.racetrack.PositionVector;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PathFollowerMoveStrategy implements MoveStrategy {

    private final List<PositionVector> waypoints;
    private int currentWaypointIndex;
    Car car;

    /**
     * This constructor is used to initialize the PathFollowerMoveStrategy object.
     * It takes a file path and a car object as parameters.
     * The file path is used to read the waypoints for the car.
     * The waypoints are stored in a list and the current waypoint index is set to 0.
     * The car object is stored in the car variable.
     * The method reads the file line by line and for each line, it removes the parentheses, splits the line into parts,
     * parses the x and y coordinates and adds a new PositionVector with these coordinates to the waypoints list.
     * If an exception occurs during file reading or parsing, an IllegalArgumentException is thrown with the message "Invalid waypoint file".
     *
     * @param filePath The path to the file containing the waypoints.
     * @param car      The car object that will follow the waypoints.
     * @throws IllegalArgumentException If the waypoint file is invalid.
     */
    public PathFollowerMoveStrategy(String filePath, Car car) {
        waypoints = new ArrayList<>();
        currentWaypointIndex = 0;
        this.car = car;
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line: lines) {
                String[] parts = line.replace("(", "").replace(")", "").split(",");
                int x = Integer.parseInt(parts[0].split(":")[1].trim());
                int y = Integer.parseInt(parts[1].split(":")[1].trim());
                waypoints.add(new PositionVector(x, y));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid waypoint file", e);
        }
    }

    /**
     * This method is used to determine the next move of the car.
     * It checks if the current waypoint index is greater than or equal to the size of the waypoints list.
     * If it is, an IllegalArgumentException is thrown indicating that the waypoint file ended prematurely.
     * If it is not, the method gets the next waypoint from the waypoints list and increments the current waypoint index.
     * The car's position is then set to the next waypoint.
     * The method returns Direction.NONE, indicating that the car does not change its direction.
     *
     * @return The direction of the car's next move. Always returns Direction.NONE.
     * @throws IllegalArgumentException If the waypoint file ended prematurely.
     */
    @Override
    public Direction nextMove() {
        if (currentWaypointIndex >= waypoints.size()) {
            throw new IllegalArgumentException("Waypoint file ended prematurely");
        }
        PositionVector nextWaypoint = waypoints.get(currentWaypointIndex);
        currentWaypointIndex++;
        car.setPosition(nextWaypoint);
        return Direction.NONE;
    }
}
