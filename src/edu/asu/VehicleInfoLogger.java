package edu.asu;
//Author : Mahendra Rao (mrrao1)
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.asu.Constants.DISPLAYED_SPEED_BIT;
import static edu.asu.Constants.DISPLAYED_SPEED_BYTE1_BITMASK;
import static edu.asu.Constants.DISPLAYED_SPEED_STEP_SIZE;
import static edu.asu.Constants.GPS_VALUE_BIT;
import static edu.asu.Constants.LAT_ACCELERATION_BIT;
import static edu.asu.Constants.LAT_ACCELERATION_MIN_VALUE;
import static edu.asu.Constants.LAT_ACCELERATION_STEP_SIZE;
import static edu.asu.Constants.LONG_ACCELERATION_BIT;
import static edu.asu.Constants.LONG_ACCELERATION_MIN_VALUE;
import static edu.asu.Constants.LONG_ACCELERATION_STEP_SIZE;
import static edu.asu.Constants.STEERING_ANGLE_BYTE1_BITMASK;
import static edu.asu.Constants.STEERING_ANGLE_MIN_VALUE;
import static edu.asu.Constants.STEERING_ANGLE_STEP_SIZE;
import static edu.asu.Constants.STEERING_WHEEL_ANGLE_BIT;
import static edu.asu.Constants.YAW_RATE_BIT;
import static edu.asu.Constants.YAW_RATE_MIN_VALUE;
import static edu.asu.Constants.YAW_RATE_STEP_SIZE;

public class VehicleInfoLogger {
    private static final String DEFAULT_CAN_MSGS_TRC_FILE = "19 CANmessages.trc";
    private static final String DEFAULT_GPS_TRACK_HTM_FILE = "19 GPS Track.htm";


    private static float calcSteeringWheelAngle(int byte1, int byte2) {
        // extract relevant bits from Byte 1
        int value = (int) (byte1 & STEERING_ANGLE_BYTE1_BITMASK);
        float angle;

        // Shift 8 bits so that Byte 2 can be OR'ed to lower part
        value <<= 8;
        value = (int) (value | byte2);

        // Scale the value by step size and offset it by the minimum value
        angle = (float) (value * STEERING_ANGLE_STEP_SIZE);
        angle += STEERING_ANGLE_MIN_VALUE;
        return angle;
    }

    private static float calcDisplayedSpeed(int byte1, int byte2) {
        // extract relevant bits from Byte 1
        int value = (int) (byte1 & DISPLAYED_SPEED_BYTE1_BITMASK);
        float speed;

        // Shift Byte1 to Bits 15 - 8 of the short value
        value <<= 8;
        // Bits 8 - 0 will be Byte2
        value = (int) (value | byte2);

        // Scale the value by step size, minimum value is 0, so no offset
        speed = (float) (value * DISPLAYED_SPEED_STEP_SIZE);
        return speed;
    }

    private static float calcYawRate(int byte1, int byte2) {
        // create 16 bit value by concatenating byte1 and byte2
        int value = (int) ((byte1 << 8) | byte2);

        // scale by step size
        float yawRate = (float) (value * YAW_RATE_STEP_SIZE);

        // Add minimum value as offset
        yawRate += YAW_RATE_MIN_VALUE;

        return yawRate;
    }

    private static float calcLongAccel(short byte5) {
        // scale by step-size
        float acceleration = (float) (byte5 * LONG_ACCELERATION_STEP_SIZE);

        // add minimum value as offset
        acceleration += LONG_ACCELERATION_MIN_VALUE;
        return acceleration;
    }

    private static float calcLatAccel(short byte6) {
        // scale by step-size
        float acceleration = (float) (byte6 * LAT_ACCELERATION_STEP_SIZE);

        // add minimum value as offset
        acceleration += LAT_ACCELERATION_MIN_VALUE;
        return acceleration;
    }

    private static float roundTwoDecimals(float value) {
        int scaled = Math.round(value * 100);
        return ((float) scaled / 100);
    }

    private static boolean parseSensorData(String canMessagesTrc) {
        Scanner canSc;
        File canFile = new File(canMessagesTrc);

        // Regular expressions used for parsing the file
        final String BLANK_LINE_REGEX = "^\\s*$";  // regex to match a string containing only whitespace
        final String TRC_COMMENT_REGEX = "^;.*$";  // regex to match comments which start with ; as first character
        final String TRC_DATA_REGEX = "(\\d+)\\)\\s+(\\d+\\.\\d)\\s+\\D+\\s+([0-9A-F]+)\\s+(\\d)\\s+(.+)";
        Pattern trcDataPattern = Pattern.compile(TRC_DATA_REGEX);
        String frameId;
        int dataLen;
        float timeOffset, steeringAngle, displayedSpeed, yawRate, longAccel, latAccel;
        VehicleInfoEntry currentEntry = null;
        float currentTimeOffset = (float) (-1.0);


        try {
            canSc = new Scanner(canFile);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + canMessagesTrc);
            return false;
        }
        while (canSc.hasNextLine()) {
            // process the trc file
            String line = canSc.nextLine();
            if (line.matches(BLANK_LINE_REGEX))
                continue;
            if (line.matches(TRC_COMMENT_REGEX))
                continue;
            // System.out.println(line);
            Matcher matcher = trcDataPattern.matcher(line);
            if (matcher.find()) {
                String timeOffsetStr = matcher.group(2);
                timeOffset = Float.parseFloat(timeOffsetStr);
                frameId = matcher.group(3);
                String dataLenStr = matcher.group(4);
                dataLen = Integer.parseInt(dataLenStr);
                String dataBytesStr = matcher.group(5);
                String[] strBytes = dataBytesStr.trim().split(" ");
                if (strBytes.length != dataLen) {
                    System.out.printf("Number of bytes (%d) received does not match data length (%d)\n", strBytes.length, dataLen);
                    // ignore this erroneous line
                    continue;
                }

                // The relevant frame ids as given in CAN Frames Info.txt are 0003, 019F and 0245.
                // Hence we process only frames with these frame ids
                switch (frameId) {
                    case "0003": // This frame gives the steering wheel angle
                        if (strBytes.length > 1) {
                            int byte1 = Short.parseShort(strBytes[0], 16);
                            int byte2 = Short.parseShort(strBytes[1], 16);
                            steeringAngle = calcSteeringWheelAngle(byte1, byte2);
                            // If the current entry is for this time offset just update it
                            // else create new one
                            if (timeOffset == currentTimeOffset && currentEntry != null)
                                currentEntry.setSteeringWheelAngle(steeringAngle);
                            else {
                                if (currentEntry != null)
                                    vehicleInfoLog.add(currentEntry);
                                currentEntry = new VehicleInfoEntry(timeOffset);
                                currentTimeOffset = timeOffset;
                                currentEntry.setSteeringWheelAngle(steeringAngle);
                            }
                        }
                        break;
                    case "019F": // This frame gives the displayed speed
                        if (strBytes.length > 1) {
                            int byte1 = Short.parseShort(strBytes[0], 16);
                            int byte2 = Short.parseShort(strBytes[1], 16);
                            displayedSpeed = calcDisplayedSpeed(byte1, byte2);
                            if (timeOffset == currentTimeOffset && currentEntry != null)
                                currentEntry.setDisplayedVehicleSpeed(displayedSpeed);
                            else {
                                if (currentEntry != null)
                                    vehicleInfoLog.add(currentEntry);
                                currentEntry = new VehicleInfoEntry(timeOffset);
                                currentTimeOffset = timeOffset;
                                currentEntry.setDisplayedVehicleSpeed(displayedSpeed);
                            }
                        }
                        break;
                    case "0245": // This frame gives the yaw rate, longitudinal and lateral acceleration
                        if (strBytes.length > 1) {
                            int byte1 = Short.parseShort(strBytes[0], 16);
                            int byte2 = Short.parseShort(strBytes[1], 16);
                            yawRate = calcYawRate(byte1, byte2);
                            if (timeOffset == currentTimeOffset && currentEntry != null)
                                currentEntry.setYawRate(yawRate);
                            else {
                                if (currentEntry != null)
                                    vehicleInfoLog.add(currentEntry);
                                currentEntry = new VehicleInfoEntry(timeOffset);
                                currentTimeOffset = timeOffset;
                                currentEntry.setYawRate(yawRate);
                            }
                            if (strBytes.length > 4) {
                                short byte5 = Short.parseShort(strBytes[4], 16);
                                longAccel = calcLongAccel(byte5);
                                // Entry for this time offset has already been set when setting yaw rate
                                currentEntry.setLongitudeAcceleration(longAccel);
                            }
                            if (strBytes.length > 5) {
                                short byte6 = Short.parseShort(strBytes[5], 16);
                                latAccel = calcLatAccel(byte6);
                                currentEntry.setLatitudeAcceleration(latAccel);
                            }
                        }
                        break;
                    default: // ignore frames that are not relevant
                        break;

                }
            }
        }
        if (currentEntry != null)
            vehicleInfoLog.add(currentEntry);
        return true;
    }

    /*
     * Obtain the index for a time offset, at which an entry can be inserted in the vehicleInfoLog.
     * If an entry already exists for the time offset, the index for that entry is returned and the
     * boolean "matched" in the InsertionInfo is set to true. If entry does not exist, "matched" is
     * set to false.
     */
    private static InsertionInfo getInsertionInfo(float timeOffset) {
        VehicleInfoEntry foundEntry = null;
        boolean matched = false;
        int requiredIndex;


        for (VehicleInfoEntry entry : vehicleInfoLog) {
            if (entry.getTimeOffset() == timeOffset) {
                matched = true;
                foundEntry = entry;
            }
            if (entry.getTimeOffset() > timeOffset) {
                foundEntry = entry;
                break;
            }
        }

        if (foundEntry != null) {
            requiredIndex = vehicleInfoLog.indexOf(foundEntry);
            return new InsertionInfo(requiredIndex, matched);
        }
        return new InsertionInfo(vehicleInfoLog.size(), false);
    }

    /*
     * Search for the start marker for the GPS data in the HTML file.
     * The start marker is the line defining the variable for the GPS data, that is,
     * var t = [
     * The lines following the start marker are assumed to hold the GPS data as in the
     * given HTML file.
     * The GPS data is extracted from each line and stored against a time offset which is
     * incremented by 1000 ms for each line.
     * It is assumed that vehicleLogInfo already has the data from the other sensors so the
     * GPS data is inserted into vehicleLogInfo based on the time offset.
     */
    private static boolean parseGpsTrackHtm(String gpsTrackHtm) {
        final String BLANK_LINE_REGEX = "^\\s*$";  // regex to match a string containing only whitespace
        final String GPS_DATA_START_MARKER_REGEX = "^\\s*var\\s+t\\s+=\\s\\["; // The regex to match var t = [
        // The below regex matches [ new GLatLng(<latitude>, <longitude>) and keeps the latitude and longitude
        // values in group 1 and 2 respectively
        final String GPS_DATA_LINE_REGEX = "^\\s*\\[?\\s*new\\s+GLatLng\\s*\\(\\s*(\\d+\\.\\d+),\\s*(\\d+\\.\\d+)";
        final String GPS_DATA_END_REGEX = "^\\s*\\];"; // stop looking for GPS data after this line
        final float GPS_POSITION_TIME_OFFSET_STEP = (float) 1000; // increment time offset by 1000 ms after each reading
        float gpsTimeOffset = 0; // start time offset for GPS data is 0 ms

        Pattern gpsDataPattern = Pattern.compile(GPS_DATA_LINE_REGEX);
        File gpsTrackFile = new File(gpsTrackHtm);
        boolean gpsDataFound = false;
        InsertionInfo insertionInfo;
        VehicleInfoEntry entry;
        Scanner gpsSc;

        try {
            gpsSc = new Scanner(gpsTrackFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file: " + gpsTrackHtm);
            return false; // indicate failure
        }

        // check each line
        while (gpsSc.hasNextLine()) {
            String line = gpsSc.nextLine();
            if (!gpsDataFound) { // look for the start marker if not yet found
                if (line.matches(GPS_DATA_START_MARKER_REGEX))
                    gpsDataFound = true;
            } else {
                if (line.matches(GPS_DATA_END_REGEX)) // GPS data values in the file have ended
                    break;
                if (!line.matches(BLANK_LINE_REGEX)) { // if not empty line it should match the GPS data pattern
                    Matcher matcher = gpsDataPattern.matcher(line); // match the line with the GPS data pattern
                    if (matcher.find()) {
                        // If the line has matched, group(1) will have latitude and group(2) will have longitude
                        float latitude = Float.parseFloat(matcher.group(1));
                        float longitude = Float.parseFloat(matcher.group(2));
                        GpsValue value = new GpsValue(latitude, longitude); // create a GPS value object

                        // Find the index in the vehicleInfoLog where this GPS value can be inserted.
                        // If there already exists an entry for this time offset, set the GPS value in that
                        // entry. Else create a new entry and set the GPS value in it. Insert the new entry
                        // at the index that has been obtained.
                        insertionInfo = getInsertionInfo(gpsTimeOffset);
                        boolean matched = insertionInfo.getMatched();
                        if (matched) {
                            entry = vehicleInfoLog.get(insertionInfo.getIndex());
                            entry.setGpsValue(value);
                        } else {
                            entry = new VehicleInfoEntry(gpsTimeOffset);
                            entry.setGpsValue(value);
                            vehicleInfoLog.add(insertionInfo.getIndex(), entry);
                        }
                        gpsTimeOffset += GPS_POSITION_TIME_OFFSET_STEP; // Increment the time offset by 1000 ms
                    } else {
                        // Found a line that does not contain the GPS data between the start and end markers.
                        System.out.println("Invalid line found in data:");
                        System.out.println(line);
                    }
                }
            }
        }
        return true; // indicate no errors parsing the file
    }


    /*
     * Iterate over all the entries in the vehicleInfoLog and
     */
    private static void printVehicleInfoLog() {
        float displayedSpeed, steeringWheelAngle, yawRate, longitudeAcceleration, latitudeAcceleration;
        GpsValue gpsValue;

        System.out.println("Time offset\t\t\tSensor\t\t\tValue");
        System.out.println("===========\t\t\t======\t\t\t=====");

        // Print the sensor types and values for each entry stored in the vehicleInfoLog. The types of
        // values stored in an entry is given by the "availableTypes" field in the entry.
        for (VehicleInfoEntry entry : vehicleInfoLog) {
            short availableTypes = entry.getAvailableTypes();
            float timeOffset = entry.getTimeOffset();
            if ((short) (availableTypes & STEERING_WHEEL_ANGLE_BIT) > 0) {
                steeringWheelAngle = roundTwoDecimals(entry.getSteeringWheelAngle());
                System.out.println(timeOffset + "\t\t" + "Steering Wheel Angle\t\t" + steeringWheelAngle);
            }
            if ((short) (availableTypes & DISPLAYED_SPEED_BIT) > 0) {
                displayedSpeed = roundTwoDecimals(entry.getDisplayedVehicleSpeed());
                System.out.println(timeOffset + "\t\t" + "Displayed Speed\t\t" + displayedSpeed);
            }
            if ((short) (availableTypes & YAW_RATE_BIT) > 0) {
                yawRate = roundTwoDecimals(entry.getYawRate());
                System.out.println(timeOffset + "\t\t" + "Yaw Rate\t\t\t\t\t" + yawRate);
            }
            if ((short) (availableTypes & LONG_ACCELERATION_BIT) > 0) {
                longitudeAcceleration = roundTwoDecimals(entry.getLongitudeAcceleration());
                System.out.println(timeOffset + "\t\t" + "Longitude Acceleration\t\t" + longitudeAcceleration);
            }
            if ((short) (availableTypes & LAT_ACCELERATION_BIT) > 0) {
                latitudeAcceleration = roundTwoDecimals(entry.getLatitudeAcceleration());
                System.out.println(timeOffset + "\t\t" + "Latitude Acceleration\t\t" + latitudeAcceleration);
            }
            if ((short) (availableTypes & GPS_VALUE_BIT) > 0) {
                gpsValue = entry.getGpsValue();
                System.out.println(timeOffset + "\t\t" + "GPS Position (Lat, Lng)\t\t" +
                        gpsValue.getLatitude() + ", " + gpsValue.getLongitude());
            }
        }

    }

    // This ArrayList stores the sensor and GPS data for all time offsets
    private static final ArrayList<VehicleInfoEntry> vehicleInfoLog = new ArrayList<>();

    /*
     * The inputs for this program are the trc file that contains sensor data and the GPS HTM
     * file that contains the GPS data for the vehicle.
     * The trc and GPS HTM filenames can be passed as arguments. If no arguments are specified,
     * the name of the trc file is defaulted to "19 CANmessages.trc" and the name of the GPS HTM
     * file is defaulted to "19 GPS Track.htm".
     */
    public static void main(String[] args) {
        String canMessagesTrc = DEFAULT_CAN_MSGS_TRC_FILE;
        String gpsTrackHtm = DEFAULT_GPS_TRACK_HTM_FILE;
        int numArgs = args.length;

        if (numArgs > 0) {
            canMessagesTrc = args[0];
        }
        if (numArgs > 1) {
            gpsTrackHtm = args[1];
        }

        if (parseSensorData(canMessagesTrc)) {
            if (parseGpsTrackHtm(gpsTrackHtm))
                printVehicleInfoLog();
            else
                System.out.println("Unable to parse file: " + gpsTrackHtm);
        } else {
            System.out.println("Unable to parse file: " + canMessagesTrc);
        }
    }

}
