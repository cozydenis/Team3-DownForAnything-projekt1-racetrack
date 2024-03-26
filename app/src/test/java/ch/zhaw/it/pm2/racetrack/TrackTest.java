package ch.zhaw.it.pm2.racetrack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TrackTest {

    private Track track;

    @BeforeEach
    public void setUp() throws IOException, InvalidFileFormatException {
        Config CFG = new Config();

        File trackFile = new File(CFG.getTrackDirectory().toString() + File.separator + "challenge.txt");
        track = new Track(trackFile);
    }

    @Test
    public void testGetHeight() {
        int expectedHeight = 26;
        assertEquals(expectedHeight, track.getHeight());
    }

    @Test
    public void testGetWidth() {
        int expectedWidth = 63;
        assertEquals(expectedWidth, track.getWidth());
    }

    @Test
    public void testGetCarCount() {
        int expectedCarCount = 2;
        assertEquals(expectedCarCount, track.getCarCount());
    }

    @Test
    public void testGetCar() {
        Car expectedCar = new Car('a', new PositionVector(24, 22));
        assertEquals(expectedCar.getId(), track.getCar(0).getId());
        assertEquals(expectedCar.getPosition(), track.getCar(0).getPosition());
    }

    @Test
    public void testGetSpaceTypeAtPosition() {
        PositionVector position = new PositionVector(0, 0);
        SpaceType expectedSpaceType = SpaceType.WALL;
        assertEquals(expectedSpaceType, track.getSpaceTypeAtPosition(position));
    }

    @Test
    public void testGetCharRepresentationAtPosition() {
        char expectedChar = '#';
        assertEquals(expectedChar, track.getCharRepresentationAtPosition(0, 0));
    }

    @Test
    public void testToString() {
        String expectedString = """
            ###############################################################
            #################                                 #############
            ###############                                     ###########
            ###############                                       #########
            ###############         ##################            #########
            ###############        ####################           #########
            ##############        #####################           #########
            ############        ######################           ##########
            #########         ######################           ############
            #########       ######################           ##############
            #########      #####################           ################
            #########        #################           ##################
            #########         ################           ##################
            ##########         ##################           ###############
            ###########         ####################          #############
            ###########         #######################          ##########
            ##########         ##########################         #########
            #########         ############################         ########
            ########         #############################         ########
            #######         ##############################         ########
            ######         #############################           ########
            ######         ############################           #########
            ######                > a                           ###########
            ######                >                          ##############
            ########              > b                     #################
            ###############################################################
            """;
        assertEquals(expectedString, track.toString());
    }
}
