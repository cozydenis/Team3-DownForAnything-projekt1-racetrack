package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.strategy.MoveStrategy;

import java.io.File;
import java.io.IOException;

/**
 * Main class of the application.
 */
public class Racetrack {
    Config config = new Config();

    /**
     * Main method of the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Racetrack racetrack = new Racetrack();
        racetrack.init();
    }

    /**
     * Initializes the game.
     */
    private void init() {
        UserInterface.printWelcome();

        File trackFile = selectTrackFile();
        Track track = createTrack(trackFile);
        setCarStrategies(track);

        run(track);
    }

    /**
     * Selects a track file from the track directory.
     *
     * @return the selected track file
     */
    private File selectTrackFile() {
        File[] listOfFiles = config.getTrackDirectory().listFiles();
        if (listOfFiles == null) {
            throw new RuntimeException("No tracks found in " + config.getTrackDirectory());
        }

        UserInterface.printTrackSelectionPrompt(listOfFiles);
        int trackNumber = UserInterface.readTrackNumber(listOfFiles.length);
        return listOfFiles[trackNumber - 1];
    }

    /**
     * Creates a track object from a track file.
     *
     * @param trackFile the track file
     * @return the track object
     */
    private Track createTrack(File trackFile) {
        try {
            return new Track(trackFile);
        } catch (IOException | InvalidFileFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the move strategies for the cars on the track.
     *
     * @param track the track
     */
    private void setCarStrategies(Track track) {
        for (Car car: track.getCars().values()) {
            MoveStrategy moveStrategy = UserInterface.promptMoveStrategy(car, track);
            car.setMoveStrategy(moveStrategy);
        }
    }

    /**
     * Runs the game with the given track.
     *
     * @param track the track
     */
    private void run(final Track track) {
        Game game = new Game(track);
        game.startGame();
    }
}
