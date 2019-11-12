package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
/**
 * Performs ultrasonic localization
 *
 */
public class LightLocalizer {
	public static int[] buffer = new int[5];
	  public static boolean LOCALIZING = false;
	  public static boolean LOCALIZINGX = false;
	  public static boolean LOCALIZINGY = false;
	  /**
	   * Performs light localization
	   * Assumes it starts facing 0 degrees in the corner block
	   * Makes the robot go to the closest intersection of the axes
	   */
	  public static void localizeDistance() {
	    LOCALIZING = true;
	    Main.ENABLE_CORRECTION = false;
	    moveBackwards(5);//to prevent issues with starting over the line
	    Main.ENABLE_CORRECTION = true;

	    LOCALIZINGY = true;
	    FORWARD_SPEED = 80;
	    /*
	     * Move forward until you detect a line
	     */
	    Navigation.travelTo(odometer.getXYT()[0] - 15, odometer.getXYT()[1] + 50);
	    LOCALIZINGY=false;
	    sleepFor(350);
	    System.out.println(odometer.getXYT());
	    Main.ENABLE_CORRECTION = false;
	    /*
	     * move on to the line
	     */
	    Navigation.travelTo(odometer.getXYT()[0], STARTING_Y);  
	    Main.ENABLE_CORRECTION = true;
	    sleepFor(350);
	    
	    System.out.println(odometer.getXYT());
	    
	    
	    
	    LOCALIZINGX = true;
	    Navigation.travelTo(odometer.getXYT()[0]+ 20, odometer.getXYT()[1]);
	    Main.ENABLE_CORRECTION = false;
	    LOCALIZINGX = false;
	    LOCALIZING = false;
	  //  Navigation.travelTo(STARTING_X, STARTING_Y);
	    Navigation.turnTo(90);
	    leftMotor.rotate(Navigation.convertDistance(SENSOR_TO_WHEEL_DISTANCE), true); //to prevent being over the line, travel backwards
	    rightMotor.rotate(Navigation.convertDistance(SENSOR_TO_WHEEL_DISTANCE), false);
	    odometer.setXYT(0, 0, 90);
	    sleepFor(100);
	  //  odometer.setTheta(-90); //hardcoded
	    Navigation.turnTo(0);
	    sleepFor(200);
	    Main.ENABLE_CORRECTION = true;
	    Sound.twoBeeps();
	    Sound.twoBeeps();
	    FORWARD_SPEED = 120;
	    Main.ENABLE_CORRECTION = false;
	  //  System.out.println("Current coord: " + (int) odometer.getXYT()[0] + ", "+ (int) odometer.getXYT()[1] +", " + (int) odometer.getXYT()[2]);
	  }

	  /**
	   * sleeps thread for a set amount of time
	   * 
	   * @param duration amount to sleep for
	   */
	  public static void sleepFor(int duration) {
	    try {
	      Thread.sleep(duration);
	    } catch (InterruptedException e) {
	      e.printStackTrace();
	    }
	  }

	 /**
	  * performs angle correction to 0,0 if at an intersection
	  */
	 public static void localizeAngle()
	 {
	   
	 }
	 /**
	  * moves backwards a distance in cm
	  * @param dist distance in cm
	  */
	 public static void moveBackwards(int dist)
	 {
	   leftMotor.rotate(-Navigation.convertDistance(dist), true); //to prevent being over the line, travel backwards
       rightMotor.rotate(-Navigation.convertDistance(dist), false);
	 }
}


