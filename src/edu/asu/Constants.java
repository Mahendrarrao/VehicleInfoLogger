package edu.asu;
//Author : Mahendra Rao (mrrao1)
// All the constants to be used across the multiple files are declared here and is only existing as a separate file for implementing good design.
public final class Constants {
    private Constants() {
    }

    public static short STEERING_ANGLE_BYTE1_BITMASK = (short) 0x3F;
    public static float STEERING_ANGLE_STEP_SIZE = (float) (0.5);
    public static float STEERING_ANGLE_MIN_VALUE = -2048;

    public static short DISPLAYED_SPEED_BYTE1_BITMASK = (short) 0x0F;
    public static float DISPLAYED_SPEED_STEP_SIZE = (float) 0.1;
    public static float DISPLAYED_SPEED_MIN_VALUE = (float) (0.0);

    public static float YAW_RATE_STEP_SIZE = (float) 0.01;
    public static float YAW_RATE_MIN_VALUE = (float) (-327.68);

    public static float LONG_ACCELERATION_STEP_SIZE = (float) 0.08;
    public static float LONG_ACCELERATION_MIN_VALUE = (float) (-10.24);

    public static float LAT_ACCELERATION_STEP_SIZE = (float) 0.08;
    public static float LAT_ACCELERATION_MIN_VALUE = (float) (-10.24);

    public static short DISPLAYED_SPEED_BIT = (short) 0x01;
    public static short STEERING_WHEEL_ANGLE_BIT = (short) 0x02;
    public static short YAW_RATE_BIT = (short) 0x04;
    public static short LONG_ACCELERATION_BIT = (short) 0x08;
    public static short LAT_ACCELERATION_BIT = (short) 0x10;
    public static short GPS_VALUE_BIT = (short) 0x20;

}
