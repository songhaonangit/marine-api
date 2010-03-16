package net.sf.marineapi.nmea.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import net.sf.marineapi.nmea.sentence.WPLSentence;
import net.sf.marineapi.nmea.util.Direction;
import net.sf.marineapi.nmea.util.Waypoint;

import org.junit.Before;
import org.junit.Test;

/**
 * SentenceWPLTest
 * 
 * @author Kimmo Tuukkanen
 */
public class SentenceWPLTest {

    /** Example sentence */
    public static final String EXAMPLE = "$GPWPL,5536.200,N,01436.500,E,RUSKI*1F";

    private WPLSentence wpl;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        try {
            wpl = new WPLSentenceImpl(EXAMPLE);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.WPLSentenceImpl#getWaypoint()}.
     */
    @Test
    public void testGetWaypoint() {
        Waypoint wp = wpl.getWaypoint();
        assertNotNull(wp);
        assertEquals("RUSKI", wp.getId());
        assertEquals(Direction.NORTH, wp.getLatHemisphere());
        assertEquals(Direction.EAST, wp.getLonHemisphere());
        final Double lat = new Double(55 + (36.200 / 60));
        final Double lon = new Double(14 + (36.500 / 60));
        assertEquals(lat, new Double(wp.getLatitude()));
        assertEquals(lon, new Double(wp.getLongitude()));
    }
}