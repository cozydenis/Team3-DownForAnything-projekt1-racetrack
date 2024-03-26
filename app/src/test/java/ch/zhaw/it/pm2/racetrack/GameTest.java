package ch.zhaw.it.pm2.racetrack;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

    private Game game;
    private Track track;
    public static Config CFG = new Config();

    @BeforeEach
    public void setUp() {
        File trackFile = new File(CFG.getTrackDirectory().toString() + File.separator + "challenge.txt");
        try {
            track = new Track(trackFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        game = new Game(track);
    }

    /**
     * Test the game initialization with two cars, verifying the initial positions and velocities.
     */
    @Test
    void testGameInitializationWithTwoCars() {
        // Assuming there are 2 cars in the game
        int expectedCarCount = 2;

        // Check the number of cars
        assertEquals(expectedCarCount, game.getCarCount(),
            "The game should be initialized with the correct number of cars");

        // Check the initial positions and velocities of the cars

        PositionVector expectedInitialPositionCarA = new PositionVector(24, 22);
        PositionVector expectedInitialVelocityCarA = new PositionVector(0, 0);

        PositionVector expectedInitialPositionCarB = new PositionVector(24, 24);
        PositionVector expectedInitialVelocityCarB = new PositionVector(0, 0);

        assertEquals(expectedInitialPositionCarA, game.getCarPosition(0), "Car A should be initialized at the correct position");
        assertEquals(expectedInitialVelocityCarA, game.getCarVelocity(0), "Car A should be initialized with the correct velocity");

        assertEquals(expectedInitialPositionCarB, game.getCarPosition(1), "Car B should be initialized at the correct position");
        assertEquals(expectedInitialVelocityCarB, game.getCarVelocity(1), "Car B should be initialized with the correct velocity");


        assertEquals(0, game.getCurrentCarIndex(), "The first car should be the initial active car");
        assertEquals(Game.NO_WINNER, game.getWinner(), "No winner should be set at the beginning of the game");
    }

    /**
     * Test that a car is declared the winner if it crosses the finish line correctly.
     */
    @Test
    void testWinnerIfCarCrossedFinishLineCorrectly() {
        int carIndex = 0;

        // Set the car's position to a point close to the finish line
        PositionVector positionNearFinish = new PositionVector(21, 22);
        track.getCar(carIndex).setPosition(positionNearFinish); // given there will be a method setPosition in Car class

        // Perform a car turn to move the car across the finish line
        game.doCarTurn(Direction.RIGHT);

        // Assert that the car is the winner
        assertEquals(carIndex, game.getWinner(),
            "The current car should be the winner if it crossed the finish line correctly");
    }

    /**
     * Test that a car is not declared the winner if it crosses the finish line incorrectly.
     */
    @Test
    void testIfCarCrossedFinishLineIncorrectly() {
        game.doCarTurn(Direction.LEFT);
        game.doCarTurn(Direction.NONE);
        game.doCarTurn(Direction.LEFT);
        game.doCarTurn(Direction.NONE);
        game.doCarTurn(Direction.RIGHT);
        game.doCarTurn(Direction.NONE);
        game.doCarTurn(Direction.RIGHT);
        game.doCarTurn(Direction.NONE);
        game.doCarTurn(Direction.RIGHT);
        game.doCarTurn(Direction.NONE);
        game.doCarTurn(Direction.RIGHT);
        game.doCarTurn(Direction.NONE);

        assertEquals(Game.NO_WINNER, game.getWinner(),
            "The car should not be the winner if it crossed the finish line incorrectly");
    }

    /**
     * Test that the remaining car is declared the winner when all other cars have crashed.
     */
    @Test
    void testWinnerIfAllOtherCarsCrashed() {
        assertEquals(Game.NO_WINNER, game.getWinner(), "No winner should be set at the beginning of the game");
        Car carA = track.getCar(0);

        carA.crash(new PositionVector(22, 22));

        game.switchToNextActiveCar();
        game.doCarTurn(Direction.UP);

        assertEquals(game.getCurrentCarIndex(), game.getWinner(), "The winner should be the only car that has not crashed");
    }

    /**
     * Test the scenario where one car crashes into another car.
     */
    @Test
    void carCrashesIntoOtherCar() {

        // Car A moves
        game.doCarTurn(Direction.LEFT);
        // Car B moves
        game.doCarTurn(Direction.LEFT);

        game.doCarTurn(Direction.DOWN);
        game.doCarTurn(Direction.UP);

        game.doCarTurn(Direction.UP_LEFT);
        game.doCarTurn(Direction.DOWN_LEFT);

        assertTrue(track.getCar(1).isCrashed(), "The car A should crash into car B");
    }

    /**
     * Test switching the active car to the next one in the sequence.
     */
    @Test
    public void testSwitchToNextActiveCar() {
        int totalCars = 2;
        int initialCarIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        int expectedCarIndex = (initialCarIndex + 1) % totalCars;

        assertEquals(expectedCarIndex, game.getCurrentCarIndex(), "The next active car should be selected");
    }

    /**
     * Test the initial condition where no winner should be declared.
     */
    @Test
    public void testGetWinner() {
        int winner = game.getWinner();
        assertEquals(Game.NO_WINNER, winner, "No winner should be set at the beginning of the game");
    }

    /**
     * Test if a car crashes into a wall and verify the crash state.
     */
    @Test
    void carCrashesIntoWall() {
        int carIndex = 0;

        // Perform a car turn to move the car into the wall
        game.doCarTurn(Direction.UP);

        assertTrue(track.getCar(carIndex).isCrashed(), "The car should have crashed into the wall");
        assertEquals(game.getCurrentCarIndex() + 1, game.getWinner(),
            "The winner should be the only car that has not crashed");
    }

    /**
     * Test various car movements and verify the updated positions and velocities.
     */
    @Test
    void testDoCarTurn() {

        // Car A moves
        game.doCarTurn(Direction.LEFT);
        // Car B moves
        game.doCarTurn(Direction.RIGHT);

        assertEquals(new PositionVector(23, 22), game.getCarPosition(0), "Car A should move to the left");
        assertEquals(new PositionVector(25, 24), game.getCarPosition(1), "Car B should move to the right");
        assertEquals(new PositionVector(-1, 0), game.getCarVelocity(0), "Car A should have a velocity of -1 in x direction");
        assertEquals(new PositionVector(1, 0), game.getCarVelocity(1), "Car B should have a velocity of 1 in x direction");


        game.doCarTurn(Direction.DOWN_LEFT);
        game.doCarTurn(Direction.UP_RIGHT);

        assertEquals(new PositionVector(21, 23), game.getCarPosition(0), "Car A should move down left");
        assertEquals(new PositionVector(27, 23), game.getCarPosition(1), "Car B should move up right");
        assertEquals(new PositionVector(-2, 1), game.getCarVelocity(0), "Car A should have a velocity of -2 in x direction and 1 in y direction");
        assertEquals(new PositionVector(2, -1), game.getCarVelocity(1), "Car B should have a velocity of 2 in x direction and -1 in y direction");


        game.doCarTurn(Direction.RIGHT);
        game.doCarTurn(Direction.LEFT);

        assertEquals(new PositionVector(20, 24), game.getCarPosition(0), "Car A should move left");
        assertEquals(new PositionVector(28, 22), game.getCarPosition(1), "Car B should move right");
        assertEquals(new PositionVector(-1, 1), game.getCarVelocity(0), "Car A should have a velocity of -1 in x direction and 1 in y direction");
        assertEquals(new PositionVector(1, -1), game.getCarVelocity(1), "Car B should have a velocity of 1 in x direction and -1 in y direction");


        game.doCarTurn(Direction.UP_LEFT);
        game.doCarTurn(Direction.DOWN_RIGHT);

        assertEquals(new PositionVector(18, 24), game.getCarPosition(0), "Car A should move up left");
        assertEquals(new PositionVector(30, 22), game.getCarPosition(1), "Car B should move down right");
        assertEquals(new PositionVector(-2, 0), game.getCarVelocity(0), "Car A should have a velocity of -2 in x direction and 0 in y direction");
        assertEquals(new PositionVector(2, 0), game.getCarVelocity(1), "Car B should have a velocity of 2 in x direction and 0 in y direction");


        game.doCarTurn(Direction.NONE);
        game.doCarTurn(Direction.NONE);

        assertEquals(new PositionVector(16, 24), game.getCarPosition(0), "Car A move NONE, should move only with acceleration");
        assertEquals(new PositionVector(32, 22), game.getCarPosition(1), "Car B move NONE, should move only with acceleration");
        assertEquals(new PositionVector(-2, 0), game.getCarVelocity(0), "Car A should have a velocity of -1 in x direction and 0 in y direction");
        assertEquals(new PositionVector(2, 0), game.getCarVelocity(1), "Car B should have a velocity of -2 in x direction and 1 in y direction");
    }

    /**
     * Test path calculation in a horizontal direction and compare it with the expected path.
     */
    @Test
    void testCalculatePathHorizontal() {
        List<PositionVector> expectedPositions = Arrays.asList(
            new PositionVector(1, 1),
            new PositionVector(2, 1),
            new PositionVector(3, 1),
            new PositionVector(4, 1),
            new PositionVector(5, 1)
        );
        List<PositionVector> positions = game.calculatePath(new PositionVector(1, 1), new PositionVector(5, 1));
        assertEquals(expectedPositions, positions);
    }

    /**
     * Test path calculation in a vertical direction and compare it with the expected path.
     */
    @Test
    void testCalculatePathVertical() {
        List<PositionVector> expectedPositions = Arrays.asList(
            new PositionVector(1, 1),
            new PositionVector(1, 2),
            new PositionVector(1, 3),
            new PositionVector(1, 4),
            new PositionVector(1, 5)
        );
        List<PositionVector> positions = game.calculatePath(new PositionVector(1, 1), new PositionVector(1, 5));
        assertEquals(expectedPositions, positions);
    }

    /**
     * Test path calculation in a diagonal direction and compare it with the expected path.
     */
    @Test
    void testCalculatePathDiagonal() {

        List<PositionVector> expectedPositions = Arrays.asList(
            new PositionVector(1, 1),
            new PositionVector(2, 2),
            new PositionVector(3, 3)
        );

        List<PositionVector> positions = game.calculatePath(new PositionVector(1, 1), new PositionVector(3, 3));
        assertEquals(expectedPositions, positions);
    }
}
