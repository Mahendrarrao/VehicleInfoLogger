package edu.asu;
//Author : Mahendra Rao (mrrao1)
public class GpsValue {
    private final float latitude, longitude;

    GpsValue(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    float getLatitude() {
        return latitude;
    }

    float getLongitude() {
        return longitude;
    }
}
