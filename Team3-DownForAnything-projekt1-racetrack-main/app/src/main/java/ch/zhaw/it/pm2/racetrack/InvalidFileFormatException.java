package ch.zhaw.it.pm2.racetrack;

/**
 * Used for invalid formatted files for Track, MoveStrategy, ...
 */
public class InvalidFileFormatException extends Exception {
    public InvalidFileFormatException(String message) {
        super(message);
    }
}
