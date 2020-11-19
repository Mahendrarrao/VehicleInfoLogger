package edu.asu;
//Author : Mahendra Rao (mrrao1)
import static edu.asu.Constants.DISPLAYED_SPEED_BIT;
import static edu.asu.Constants.DISPLAYED_SPEED_MIN_VALUE;
import static edu.asu.Constants.GPS_VALUE_BIT;
import static edu.asu.Constants.LAT_ACCELERATION_BIT;
import static edu.asu.Constants.LAT_ACCELERATION_MIN_VALUE;
import static edu.asu.Constants.LONG_ACCELERATION_BIT;
import static edu.asu.Constants.LONG_ACCELERATION_MIN_VALUE;
import static edu.asu.Constants.STEERING_ANGLE_MIN_VALUE;
import static edu.asu.Constants.STEERING_WHEEL_ANGLE_BIT;
import static edu.asu.Constants.YAW_RATE_BIT;
import static edu.asu.Constants.YAW_RATE_MIN_VALUE;

public class VehicleInfoEntry {
    VehicleInfoEntry(float timeOffset) {
        this.timeOffset = timeOffset;
        availableTypes = 0;
    }

    public float getTimeOffset() {
        return timeOffset;
    }

    public short getAvailableTypes() {
        return availableTypes;
    }

    public void setDisplayedVehicleSpeed(float speed) {
        availableTypes |= DISPLAYED_SPEED_BIT;
        this.displayedVehicleSpeed = speed;
    }

    public float getDisplayedVehicleSpeed() {
        if ((short) (availableTypes & DISPLAYED_SPEED_BIT) > 0)
            return displayedVehicleSpeed;
        return DISPLAYED_SPEED_MIN_VALUE - 1;
    }

    public void setSteeringWheelAngle(float steeringWheelAngle) {
        availableTypes |= STEERING_WHEEL_ANGLE_BIT;
        this.steeringWheelAngle = steeringWheelAngle;
    }

    public float getSteeringWheelAngle() {
        if ((short) (availableTypes & STEERING_WHEEL_ANGLE_BIT) > 0)
            return this.steeringWheelAngle;
        return STEERING_ANGLE_MIN_VALUE - 1;
    }

    public void setYawRate(float yawRate) {
        availableTypes |= YAW_RATE_BIT;
        this.yawRate = yawRate;
    }

    public float getYawRate() {
        if ((short) (availableTypes & YAW_RATE_BIT) > 0)
            return this.yawRate;
        return YAW_RATE_MIN_VALUE - 1;
    }

    public void setLongitudeAcceleration(float longitudeAcceleration) {
        availableTypes |= LONG_ACCELERATION_BIT;
        this.longitudeAcceleration = longitudeAcceleration;
    }

    public float getLongitudeAcceleration() {
        if ((short) (availableTypes & LONG_ACCELERATION_BIT) > 0)
            return this.longitudeAcceleration;
        return LONG_ACCELERATION_MIN_VALUE - 1;
    }

    public void setLatitudeAcceleration(float latitudeAcceleration) {
        availableTypes |= LAT_ACCELERATION_BIT;
        this.latitudeAcceleration = latitudeAcceleration;
    }

    public float getLatitudeAcceleration() {
        if ((short) (availableTypes & LAT_ACCELERATION_BIT) > 0)
            return this.latitudeAcceleration;
        return LAT_ACCELERATION_MIN_VALUE - 1;
    }

    public void setGpsValue(GpsValue gpsValue) {
        availableTypes |= GPS_VALUE_BIT;
        this.gpsValue = gpsValue;
    }

    public GpsValue getGpsValue() {
        if ((short) (availableTypes & GPS_VALUE_BIT) > 0)
            return this.gpsValue;
        return null;
    }

    private short availableTypes;

    private final float timeOffset;
    //values that can be stored in the entry for the time offset
    private float displayedVehicleSpeed;
    private float steeringWheelAngle;
    private float yawRate;
    private float longitudeAcceleration;
    private float latitudeAcceleration;
    private GpsValue gpsValue;
}
