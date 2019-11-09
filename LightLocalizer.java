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
    Main.ENABLE_CORRECTION = false;
    leftMotor.rotate(-Navigation.convertDistance(2), true); //to prevent being over the line, travel backwards
    rightMotor.rotate(-Navigation.convertDistance(2), false);
    Main.ENABLE_CORRECTION = true;
    LOCALIZING = true;
    LOCALIZINGY = true;
    leftMotor.setSpeed(100);
    rightMotor.setSpeed(100);
    Navigation.travelTo(odometer.getXYT()[0], odometer.getXYT()[1] + 50);
 //   Main.ENABLE_CORRECTION = false;
 //   leftMotor.rotate(Navigation.convertDistance(12), true);
 //   rightMotor.rotate(Navigation.convertDistance(12), false);
    //back up
    LOCALIZINGY=false;
    LOCALIZINGX = true;
    Main.ENABLE_CORRECTION = false;
    Sound.beepSequenceUp();
    Navigation.travelTo(odometer.getXYT()[0], STARTING_Y);  
    Main.ENABLE_CORRECTION = true;
    Sound.buzz();
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
   Navigation.travelTo(0, 0);
   sleepFor(500);
   Navigation.turnTo(0);
   leftMotor.stop();
   rightMotor.stop();
   leftMotor.setSpeed(150);
   rightMotor.setSpeed(150);
   sleepFor(600);
   //Resources.SENSOR_TO_WHEEL_ANGLE;
   leftMotor.rotate(Navigation.convertAngle(370), true);
   rightMotor.rotate(-Navigation.convertAngle(370), true);
   int count = 0;
   /*
    * sum of the errors
    */
   int sum = 0;
   sleepFor(100);
   boolean steadyState = true;
   while(leftMotor.isMoving())
   {

     int diff = lightPoller.getIntensity();
     if (diff > 0) // negative value, so has returned to steady state (fluctuations around 0,0)
       steadyState = true;
     /*
      * spike of less than -55 implies a line has been detected. steadyState makes it
      * so that it only looks for lines again after it goes back to 0, i.e doesn't detect same lines multiple times.
      */
     //assume angle isnt bad (won't mistake a line for another.
     if ((diff < Resources.LIGHT_DIFF_THRESHOLD) && (steadyState == true)
         && (odometer.getXYT()[2] > 50) && (odometer.getXYT()[2] < 300)) {
       
       Sound.playTone(count * 2000, 300);
       steadyState = false;      
      
       int nearestLine = (int) (Math.round(odometer.getXYT()[2] / 90.0) * 90);
       int error = (int) (nearestLine - odometer.getXYT()[2]);
      // System.out.println("lines: " + count + "at " + odometer.getXYT()[2]);
       //System.out.println("error: " + error);
       sum += error;
       count++; //increment lines detected
     }
     sleepFor(40);
   }
   rightMotor.stop();
   leftMotor.stop();
   sleepFor(500);
  // sum -= (90 + 180 + 270);
   //get average error
   if(count != 0 )
   sum /= count;
   odometer.incrementTheta(-sum);

   Navigation.turnTo(0);
   Button.waitForAnyPress();
 }
}


