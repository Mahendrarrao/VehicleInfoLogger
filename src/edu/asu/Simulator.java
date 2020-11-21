package edu.asu;

import static edu.asu.Constants.DISPLAYED_SPEED_BIT;
import static edu.asu.Constants.GPS_VALUE_BIT;
import static edu.asu.Constants.LAT_ACCELERATION_BIT;
import static edu.asu.Constants.LONG_ACCELERATION_BIT;
import static edu.asu.Constants.STEERING_WHEEL_ANGLE_BIT;
import static edu.asu.Constants.YAW_RATE_BIT;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Simulator extends TimerTask{
	
	public static ArrayList<VehicleInfoEntry> vehicleinfolog;
	private float timeOffset = 0;
    //values that can be stored in the entry for the time offset
    private String displayedVehicleSpeed="-";
    private String steeringWheelAngle="-";
    private String yawRate="-";
    private String longitudeAcceleration="-";
    private String latitudeAcceleration="-";
    private String latitude="-";
    private String longitutde="-";
    public static float i = 0;
    

	@Override
	public void run() {
		// TODO Auto-generated method stub
		vehicleinfolog = VehicleInfoLogger.vehicleInfoLog;
		i = (float) (i + 0.1);
		if ((int) VehicleInfoLogger.roundTwoDecimals(i) == 45000) {
			System.exit(0);
		}
//        System.out.println("Timer ran " + (int) VehicleInfoLogger.roundTwoDecimals(i));
		// TODO Auto-generated constructor stub
		for (VehicleInfoEntry entry : vehicleinfolog) {
            short availableTypes = entry.getAvailableTypes();
            timeOffset = entry.getTimeOffset();
            
            if (timeOffset == VehicleInfoLogger.roundTwoDecimals(i)) {
            	if ((short) (availableTypes & STEERING_WHEEL_ANGLE_BIT) > 0) {
                    steeringWheelAngle = String.valueOf(VehicleInfoLogger.roundTwoDecimals(entry.getSteeringWheelAngle()));
                    
                }
                if ((short) (availableTypes & DISPLAYED_SPEED_BIT) > 0) {
                	displayedVehicleSpeed = String.valueOf(VehicleInfoLogger.roundTwoDecimals(entry.getDisplayedVehicleSpeed()));
                    
                }
                if ((short) (availableTypes & YAW_RATE_BIT) > 0) {
                    yawRate = String.valueOf(VehicleInfoLogger.roundTwoDecimals(entry.getYawRate()));
                    
                }
                if ((short) (availableTypes & LONG_ACCELERATION_BIT) > 0) {
                    longitudeAcceleration = String.valueOf(VehicleInfoLogger.roundTwoDecimals(entry.getLongitudeAcceleration()));
                    
                }
                if ((short) (availableTypes & LAT_ACCELERATION_BIT) > 0) {
                    latitudeAcceleration = String.valueOf(VehicleInfoLogger.roundTwoDecimals(entry.getLatitudeAcceleration()));
                }
                if ((short) (availableTypes & GPS_VALUE_BIT) > 0) {
                	GpsValue gpsValue;
                    gpsValue = entry.getGpsValue();
                    this.latitude = String.valueOf(gpsValue.getLatitude());
                    this.longitutde = String.valueOf(gpsValue.getLongitude());
                }
            }
//            System.out.println(VehicleInfoLogger.roundTwoDecimals(i) + "|" + steeringWheelAngle + "|" + displayedVehicleSpeed + "|" +  yawRate + "|" + 
//            longitudeAcceleration + "|" + latitudeAcceleration + "|" + latitude + ", " + longitutde);  
		}
		
	}
}
