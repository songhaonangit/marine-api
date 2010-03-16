/* 
 * RMCSentenceImpl.java
 * Copyright (C) 2010 Kimmo Tuukkanen
 * 
 * This file is part of Java Marine API.
 * <http://sourceforge.net/projects/marineapi/>
 * 
 * Java Marine API is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Java Marine API is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java Marine API. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.marineapi.nmea.parser;

import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Direction;
import net.sf.marineapi.nmea.util.GpsMode;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.SentenceId;

/**
 * RMC sentence parser.
 * 
 * @author Kimmo Tuukkanen
 * @version $Revision$
 */
class RMCSentenceImpl extends PositionParser implements RMCSentence {

    private static final int UTC_TIME = 1;
    private static final int DATA_STATUS = 2;
    private static final int LATITUDE = 3;
    private static final int LAT_HEMISPHERE = 4;
    private static final int LONGITUDE = 5;
    private static final int LON_HEMISPHERE = 6;
    private static final int SPEED = 7;
    private static final int COURSE = 8;
    private static final int UTC_DATE = 9;
    private static final int MAG_VARIATION = 10;
    private static final int VAR_HEMISPHERE = 11;
    private static final int MODE = 12;

    /**
     * Constructor.
     */
    public RMCSentenceImpl(String nmea) {
        super(nmea, SentenceId.RMC);
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.RMCSentence#getCorrectedCourse()
     */
    public double getCorrectedCourse() {
        return getCourse() + getVariation();
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.RMCSentence#getCourse()
     */
    public double getCourse() {
        return getDoubleValue(COURSE);

    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.RMCSentence#getDataStatus()
     */
    public DataStatus getDataStatus() {
        return DataStatus.valueOf(getCharValue(DATA_STATUS));
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.RMCSentence#getDirectionOfVariation()
     */
    public Direction getDirectionOfVariation() {
        return Direction.valueOf(getCharValue(VAR_HEMISPHERE));
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.RMCSentence#getGpsMode()
     */
    public GpsMode getGpsMode() {
        return GpsMode.valueOf(getCharValue(MODE));
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.RMCSentence#getSpeed()
     */
    public double getSpeed() {
        return getDoubleValue(SPEED);
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.PositionSentence#getPosition()
     */
    public Position getPosition() {
        double lat = parseLatitude(LATITUDE);
        double lon = parseLongitude(LONGITUDE);
        Direction lath = parseHemisphereLat(LAT_HEMISPHERE);
        Direction lonh = parseHemisphereLon(LON_HEMISPHERE);
        return new Position(lat, lath, lon, lonh);
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.DateSentence#getDate()
     */
    public Date getDate() {
        int y = getUtcYear();
        int m = getUtcMonth() - 1;
        int d = getUtcDay();
        int h = getUtcHours();
        int mi = getUtcMinutes();
        int s = (int) Math.floor(getUtcSeconds());
        GregorianCalendar cal = new GregorianCalendar(y, m, d, h, mi, s);
        return cal.getTime();
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.DateSentence#getUtcDay()
     */
    public int getUtcDay() {
        return Integer.parseInt(getStringValue(UTC_DATE).substring(0, 2));
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.TimeSentence#getUtcHours()
     */
    public int getUtcHours() {
        return Integer.parseInt(getUtcTime().substring(0, 2));
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.TimeSentence#getUtcMinutes()
     */
    public int getUtcMinutes() {
        return Integer.parseInt(getUtcTime().substring(2, 4));
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.DateSentence#getUtcMonth()
     */
    public int getUtcMonth() {
        return Integer.parseInt(getStringValue(UTC_DATE).substring(2, 4));
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.TimeSentence#getUtcSeconds()
     */
    public double getUtcSeconds() {
        return Integer.parseInt(getUtcTime().substring(4, 6));
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.TimeSentence#getUtcTime()
     */
    public String getUtcTime() {
        return getStringValue(UTC_TIME);
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.DateSentence#getUtcYear()
     */
    public int getUtcYear() {
        int y = Integer.parseInt(getStringValue(UTC_DATE).substring(4));
        if (y < 100 && y <= PIVOT_YEAR) {
            y += 2000;
        } else if (y < 100) {
            y += 1900;
        }
        return y;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.parser.RMCSentence#getVariation()
     */
    public double getVariation() {
        double variation = getDoubleValue(MAG_VARIATION);
        if (Direction.EAST.equals(getDirectionOfVariation())) {
            variation = -(variation);
        }
        return variation;
    }
}