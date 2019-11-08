package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.ACCELERATION;
import static ca.mcgill.ecse211.team4.Resources.FORWARD_SPEED;
import static ca.mcgill.ecse211.team4.Resources.ROTATE_SPEED;
import static ca.mcgill.ecse211.team4.Resources.TILE_SIZE;
import static ca.mcgill.ecse211.team4.Resources.TIMEOUT_PERIOD;
import static ca.mcgill.ecse211.team4.Resources.TRACK;
import static ca.mcgill.ecse211.team4.Resources.WHEEL_RAD;
import static ca.mcgill.ecse211.team4.Resources.launchMotor1;
import static ca.mcgill.ecse211.team4.Resources.launchMotor2;
import static ca.mcgill.ecse211.team4.Resources.leftMotor;
import static ca.mcgill.ecse211.team4.Resources.rightMotor;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.RegulatedMotor;
/**
 * Contains all methods related to testing
 * @author Admin
 *
 */
public class Testing {
/*  /**
   * Get ultrasonic data. Press any button to get readings from the ultrasnic sensor 10 times.
   
  public static void UltrasonicTester() {
    while (true) {
      float[] usData = new float[US_SENSOR.sampleSize()];
      for (int i = 0; i < 10; i++) {
        US_SENSOR.getDistanceMode().fetchSample(usData, 0); // acquire distance data in meters
        int distance = (int) (usData[0] * 100.0); // extract from buffer, convert to cm, cast to int
        System.out.println(distance); //print data to screen
        Team4.sleepFor(100);
      }
      Button.waitForAnyPress();
      System.out.println("\n\n");
    }
  }
*/
  /**
   * time to run code 1000 times
   
  public static void codePerformanceTest(UltrasonicPoller poller) {
    long t1 = System.nanoTime();
    for (int i = 0; i < 10000; i++) {
      US_SENSOR.getDistanceMode().fetchSample(new float[US_SENSOR.sampleSize()], 0); // acquire distance data in meters
    }
    long t2 = System.nanoTime();
    System.out.println("Time to run: " + (t2 - t1) / 1000000);

  // * Prints values 10 times at 0.5 second intervals to test Ultrasonic sensor accuracy
 */
  public static void ultrasonicTest()
  {
    

  }

  /**
   * Runs a function 10,000 times in a loop to measure execution time
   */
  public static void ultrasonicLocalitzationTest() {

    while (true) {
      Main.sleepFor(1000);
      UltrasonicLocalizer.RisingEdge();
      if (Button.waitForAnyPress() == Button.ID_ESCAPE)
        break;
    }
    System.exit(0);
  }

 /**
  * The track test to test rotational accuracy and precision.
  */
  public static void trackTest()
  {
    leftMotor.stop();
    rightMotor.stop();
    launchMotor1.setAcceleration(999999);
    launchMotor2.setAcceleration(999999);
    leftMotor.setAcceleration(2000);
    rightMotor.setAcceleration(2000);
    leftMotor.setSpeed(300);
    rightMotor.setSpeed(300);
    leftMotor.rotate(Navigation.convertAngle(360), true);
    rightMotor.rotate(-Navigation.convertAngle(360), false);
    leftMotor.stop();
    rightMotor.stop();
    Main.sleepFor(2000);
  }
    

 
 /**
  * The light sensor test for intensity values under different conditions
  */
  public static void lightSensorTest()
  {
    (new Thread() {
      public void run() {
        /**
         * reset the motors
         */
        leftMotor.stop();
        rightMotor.stop();
        leftMotor.setAcceleration(ACCELERATION);
        rightMotor.setAcceleration(ACCELERATION);

        /***
         * Sleep for 2 seconds
         */
        Main.sleepFor(TIMEOUT_PERIOD);

        for (int i = 0; i < 1; i++) {
          /**
           * Rotate for 360 Degrees, to plot the distance
           */
          leftMotor.setSpeed(150);
          rightMotor.setSpeed(150);
          leftMotor.rotate(Navigation.convertAngle(400), true);
          rightMotor.rotate(-Navigation.convertAngle(400), false);
          System.out.println("i = " + i + "\n\n\n\n\n\n");
          try {
            Thread.sleep(200);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        leftMotor.setSpeed(0); // added these so that we can easily check if robot is travelling in a straight line

        rightMotor.setSpeed(0);
        UltrasonicLocalizer.sweepDone = true;
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }


        System.exit(0);
      }
    }).start();
  }

  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * 
   * @param distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Moves robot 5 tiles forward. Used to test wheel radius
   */
  public static void radiusTest() {
    Navigation.travelTo(0, TILE_SIZE * 5);
  }

  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * TRACK * angle / 360.0);
  }
  
}
