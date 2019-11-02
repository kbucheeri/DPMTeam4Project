package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.colorSensor;
import static ca.mcgill.ecse211.lab5.Resources.leftMotor;
import static ca.mcgill.ecse211.lab5.Resources.odometer;
import static ca.mcgill.ecse211.lab5.Resources.rightMotor;
import static ca.mcgill.ecse211.lab5.Resources.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class LightLocalizer {
  public static int[] buffer = new int[5];


  /**
   * Performs light localization
   * Assumes it starts facing 0 degrees in the corner block
   * Makes the robot at 1,1
   */
  public static void localizeDistance() {
    /**
     * Float Array to store RGB Raw values
     */
    leftMotor.setSpeed(100);
    rightMotor.setSpeed(100);
    Navigation.turnTo(0);
    sleepFor(300);
    leftMotor.forward();
    rightMotor.forward();
    // if light sensing returns to 0 to prevent multiple line readings.
    boolean steadyState = true;
    sleepFor(100);
    /*
     * in the sweep. stops when either of the motors stop.
     */
    while (true) {
      
      int diff = lightPoller.getIntensity();
      if (diff > 0) // negative value, so has returned to steady state (fluctuations around 0,0)
        steadyState = true;
      /*
       * spike of less than -75 implies a line has been detected. steadyState makes it
       * so that it only looks for lines again after it goes back to 0, i.e doesn't detect same lines multiple times.
       */
      if (diff < LIGHT_DIFF_THRESHOLD && steadyState == true) {
        
        Sound.beepSequence();
        steadyState = false;
        break;
      }
      sleepFor(100);
    }
    odometer.setY(-Resources.SENSOR_TO_WHEEL_DISTANCE);
    leftMotor.stop();
    rightMotor.stop();
    leftMotor.rotate(Navigation.convertDistance(8), true);
    rightMotor.rotate(Navigation.convertDistance(8), false);
    //back up
    sleepFor(600);
    Navigation.turnTo(90);
    sleepFor(600);
    leftMotor.forward();
    rightMotor.forward();
    steadyState = true;
    while (true) {
      int diff = lightPoller.getIntensity();
      if (diff > 0) // negative value, so has returned to steady state (fluctuations around 0,0)
        steadyState = true;
      /*
       * spike of less than -75 implies a line has been detected. steadyState makes it
       * so that it only looks for lines again after it goes back to 0, i.e doesn't detect same lines multiple times.
       */
      LCD.drawString("Diff: " + diff, 0, 5);
      if (diff < Resources.LIGHT_DIFF_THRESHOLD && steadyState == true) {
        
        Sound.beepSequence();
        steadyState = false;
        break;

      }
      sleepFor(50);
    }
    odometer.setX(-Resources.SENSOR_TO_WHEEL_DISTANCE);
    rightMotor.stop();
    leftMotor.stop();
    sleepFor(500);
    Navigation.travelTo(0, 0);
    sleepFor(500);
    Navigation.turnTo(0);
    sleepFor(400);
    Sound.twoBeeps();
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
  * performs angle correction to 0,0.
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


