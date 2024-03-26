package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.*;
import ch.zhaw.it.pm2.racetrack.strategy.MoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class PathFinderMoveStrategy implements MoveStrategy {

    private final Car car;
    private final Track track;

    private boolean firstCall = true;

    private ArrayList<PositionVector> visited = new ArrayList<>();
    private ArrayList<Node> plausiblePath = new ArrayList<>();

    private Game game;

    public PathFinderMoveStrategy(Car car, Track track) {
        this.car = car;
        this.track = track;
        this.game = new Game(track);
    }

    public boolean nodeWon(Node n) {
        return game.handleFinishLine(n.toCar(), track.getSpaceTypeAtPosition(n.position));
    }

    public boolean nodeCrash(Node n) {

        //UserInterface.printSomething("Node position" + n.position);
        try {
            return track.getTrack()[n.position.getX()][n.position.getY()] == SpaceType.WALL;
        } catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }

    }

    public Node canNextNodeGoDirection(Node n, Direction d) {
        Node nextNode = new Node(n.position, n.velocity, d, n.distance + 1, n);
        nextNode.accelerate(d);

        if (nodeCrash(nextNode) || visited.contains(nextNode.position)) {
            return null;
        } else {
            return nextNode;
        }
    }

    public Node tryDirection(Node n) {
        for (int i = 0; i < 9; i++) {
            Direction d = Direction.values()[i];
            Node nextNode = canNextNodeGoDirection(n, d);
            if (nextNode != null) {
                return nextNode;
            }
        }
        UserInterface.printSomething("Going to parent Node");

        return tryDirection(plausiblePath.removeLast());
    }


    public void findPath() {
        boolean goalReached = false;
        Node start = new Node(car.getPosition(), car.getVelocity(), Direction.NONE, 0, null);
        while (!goalReached) {
            Node nextNode = tryDirection(start);
            if (nodeWon(nextNode)) {
                goalReached = true;
            }
            visited.add(nextNode.position);
            plausiblePath.add(nextNode);
            UserInterface.printSomething(nextNode.toString());
            UserInterface.printSomething("Step: " + plausiblePath.size());
        }
    }

    @Override
    public Direction nextMove() {
        if (firstCall) {
            findPath();
            firstCall = false;
        }
        return plausiblePath.removeFirst().direction;
    }

    private class Node {
        int distance;
        Node parent;
        PositionVector position;
        PositionVector velocity;
        Direction direction;

        public Node(PositionVector position, PositionVector velocity, Direction direction, int distance, Node parent) {
            this.position = position;
            this.velocity = velocity;
            this.direction = direction;
            this.distance = distance;
            this.parent = parent;
        }

        public void accelerate(Direction acceleration) {
            Car tempCar = new Car('a', this.position);
            tempCar.setVelocity(this.velocity);
            tempCar.accelerate(acceleration);
            this.velocity = tempCar.getVelocity();
            this.position = tempCar.nextPosition();
        }

        public String toString() {
            return "Node: " + position + " Velocity: " + velocity + " Direction: " + direction + " Distance: " + distance + "\n  Parent: (" + parent + ")";
        }

        public Car toCar() {
            Car tempCar = new Car('a', this.position);
            tempCar.setVelocity(this.velocity);
            return tempCar;
        }
    }
}
