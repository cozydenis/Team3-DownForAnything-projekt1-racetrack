package ch.zhaw.it.pm2.racetrack;


import ch.zhaw.it.pm2.racetrack.strategy.DoNotMoveStrategy;
import ch.zhaw.it.pm2.racetrack.strategy.MoveListStrategy;
import ch.zhaw.it.pm2.racetrack.strategy.PathFollowerMoveStrategy;
import ch.zhaw.it.pm2.racetrack.strategy.UserMoveStrategy;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StrategyClassTest {
        @Test
        public void testUserMoveStrategy() {
            UserMoveStrategy strategy = new UserMoveStrategy();

            assertEquals(Direction.UP, strategy.nextMove("UP"));
            assertEquals(Direction.DOWN, strategy.nextMove("DOWN"));
            assertEquals(Direction.LEFT, strategy.nextMove("LEFT"));
            assertEquals(Direction.RIGHT, strategy.nextMove("RIGHT"));
            assertEquals(Direction.NONE, strategy.nextMove("NONE"));
            assertNull(strategy.nextMove("QUIT"));

        }

        @Test
        public void testDoNotMoveStrategy() {
            DoNotMoveStrategy strategy = new DoNotMoveStrategy();
            assertEquals(Direction.NONE, strategy.nextMove());
        }

        @Test
        public void testMoveListStrategy() {
            Config CFG = new Config();
            MoveListStrategy strategy = new MoveListStrategy(CFG.getMoveDirectory().toString() + File.separator + "challenge-car-a.txt");
            assertEquals(Direction.RIGHT, strategy.nextMove());
            assertEquals(Direction.RIGHT, strategy.nextMove());
            assertEquals(Direction.RIGHT, strategy.nextMove());
            assertEquals(Direction.NONE, strategy.nextMove());
            assertEquals(Direction.NONE, strategy.nextMove());

        }

        @Test
        public void testPathFollowerMoveStrategy() {
            Config CFG = new Config();
            Car car = new Car('a' , new PositionVector(0, 0));
            PathFollowerMoveStrategy strategy = new PathFollowerMoveStrategy(CFG.getFollowerDirectory().toString() + File.separator + "challenge_handout_points.txt",car);
            strategy.nextMove();
            assertEquals(new PositionVector(28, 22),car.getPosition());
            strategy.nextMove();
            assertEquals(new PositionVector(31, 22),car.getPosition());
            strategy.nextMove();
            assertEquals(new PositionVector(34, 22),car.getPosition());
            strategy.nextMove();
            assertEquals(new PositionVector(37, 22),car.getPosition());

        }



    }

