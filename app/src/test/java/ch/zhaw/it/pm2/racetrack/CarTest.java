package ch.zhaw.it.pm2.racetrack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarTest {
    private Car car;

    // initialise the car at the position (0,0) for every test
    @BeforeEach
    void setUp() {
        car = new Car('A', new PositionVector(0, 0));
    }

    /**
     * Tests accelerating the car in a valid direction (right).
     * Equivalence class: accelerate
     * The method should correctly update the velocity when
     * the car is accelerated in a valid direction (right in this case).
     */
    @Test
    void testAccelerateValidDirection() {
        car.accelerate(Direction.RIGHT);
        assertEquals(new PositionVector(1, 0), car.getVelocity(), "Acceleration to the right should correctly change the velocity.");
    }

    /**
     * Tests accelerating the car with a null direction.
     * Equivalence class: accelerate
     * Accelerating with a null direction should throw
     * an IllegalArgumentException.
     */
    @Test
    void testAccelerateWithNull() {
        assertThrows(IllegalArgumentException.class, () -> car.accelerate(null), "Accelerating with null should throw an IllegalArgumentException.");
    }

    /**
     * Tests accelerating the car twice in the same direction (up).
     * Equivalence class: accelerate
     * Assuming (0, -2) is within bounds and not a collision point,
     * accelerating twice in the same direction should correctly update the velocity.
     */
    @Test
    void testAccelerateMaximumOneDirection() {
        car.accelerate(Direction.UP);
        car.accelerate(Direction.UP);
        assertEquals(new PositionVector(0, -2), car.getVelocity(), "Accelerating twice upwards should correctly change the velocity.");
    }

    /**
     * Tests accelerating the car in opposite directions (left, then right).
     * Equivalence class: acccelerate
     * Accelerations in opposite directions should cancel each other out,
     * resulting in no change in velocity.
     */
    @Test
    void testAccelerateInOppositeDirections() {
        car.accelerate(Direction.LEFT);
        car.accelerate(Direction.RIGHT);
        assertEquals(new PositionVector(0, 0), car.getVelocity(), "Accelerations in opposite directions should cancel each other out.");
    }

    /**
     * Tests accelerating the car in multiple directions (up and right).
     * Equivalence class: accelerate
     * Accelerating in different directions should correctly
     * accumulate the velocity changes.
     */
    @Test
    void testAccelerateMultipleDirections() {
        car.accelerate(Direction.UP);
        car.accelerate(Direction.RIGHT);
        assertEquals(new PositionVector(1, -1), car.getVelocity(), "Accelerations in different directions should be correctly accumulated.");
    }

    /**
     * Tests moving the car with no velocity.
     * Equivalence class: move
     * Setting the car's velocity to zero (0,0) and then moving
     * it should not change the car's position.
     */
    @Test
    void testMoveWithNoVelocity() {
        car.setVelocity(new PositionVector(0, 0)); // Ensure the car's velocity is set to zero
        car.move(); // Move the car
        assertEquals(new PositionVector(0, 0), car.getPosition(), "A car with no velocity should not change its position.");
    }

    /**
     * Tests moving the car with maximum positive velocity.
     * Equivalence class: move
     * Setting the car's velocity to a high positive value
     * and then moving it should update the car's position accordingly.
     */
    @Test
    void testMoveWithMaxPositiveVelocity() {
        car.setVelocity(new PositionVector(5, 5)); // Set the velocity to a high positive value
        car.move(); // Move the car
        assertEquals(new PositionVector(5, 5), car.getPosition(), "The car should be correctly moved with maximum positive velocity.");
    }

    /**
     * Tests moving the car with maximum negative velocity.
     * Equivalence class: move
     * Setting the car's velocity to a high negative value
     * and then moving it should update the car's position accordingly in the negative direction.
     */
    @Test
    void testMoveWithMaxNegativeVelocity() {
        car.setVelocity(new PositionVector(-5, -5)); // Set the velocity to a high negative value
        car.move(); // Move the car
        assertEquals(new PositionVector(-5, -5), car.getPosition(), "The car should be correctly moved with maximum negative velocity.");
    }

    /**
     * Tests moving the car after resetting its velocity.
     * Equivalence class: move
     * Resetting the car's velocity to zero (0,0) after it has
     * been set to a non-zero value and then moving it should not change the car's position.
     */
    @Test
    void testMoveAfterResettingVelocity() {
        car.setVelocity(new PositionVector(3, 3)); // Set an initial velocity
        car.setVelocity(new PositionVector(0, 0)); // Reset the velocity
        car.move(); // Move the car
        assertEquals(new PositionVector(0, 0), car.getPosition(), "The car should not move after its velocity has been reset.");
    }
}
