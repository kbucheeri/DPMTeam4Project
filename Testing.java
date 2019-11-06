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
import lejos.hardware.Sound;
import lejos.robotics.RegulatedMotor;
/**
 * Contains all methods related to testing
 * @author Admin
 *
 */
public class Testing {
  /**
<<<<<<< HEAD
   * Get ultrasonic data. Press any button to get readings from the ultrasnic sensor 10 times.
   */
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

  /**
   * time to run code 1000 times
   */
  public static void codePerformanceTest(UltrasonicPoller poller) {
    long t1 = System.nanoTime();
    for (int i = 0; i < 10000; i++) {
      US_SENSOR.getDistanceMode().fetchSample(new float[US_SENSOR.sampleSize()], 0); // acquire distance data in meters
    }
    long t2 = System.nanoTime();
    System.out.println("Time to run: " + (t2 - t1) / 1000000);
=======
   * Prints values 10 times at 0.5 second intervals to test Ultrasonic sensor accuracy
   */
  public static void ultrasonicTest()
  {
    
>>>>>>> 9b0408be69a1f80cf1356ffad1ccc0d7a02f2881
  }

  /**
   * Runs a function 10,000 times in a loop to measure execution time
   */
<<<<<<< HEAD
  public static void ultrasonicLocalitzationTest() {

    while (true) {
      Team4.sleepFor(1000);
      UltrasonicLocalizer.RisingEdge();
      if (Button.waitForAnyPress() == Button.ID_ESCAPE)
        break;
    }
    System.exit(0);
  }

  public static void trackTest() {
=======
  public static void codePerformanceTest() {
    
  }
 /**
  * The track test to test rotational accuracy and precision.
  */
  public static void trackTest()
  {
>>>>>>> 9b0408be69a1f80cf1356ffad1ccc0d7a02f2881
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
<<<<<<< HEAD
    Team4.sleepFor(2000);

=======
    Main.sleepFor(2000);
    
>>>>>>> 9b0408be69a1f80cf1356ffad1ccc0d7a02f2881
    Sound.beepSequenceUp();
    // Navigation.turn(180);
    launchMotor1.setSpeed(8000);
    launchMotor2.setSpeed(8000);

    /*
     * for(int i = 0; i < 5; i ++) {Resources.launchMotor1.rotate(-(70 + 5 * i), true);
     * Resources.launchMotor2.rotate(-(70 + 5 * i), false); Resources.launchMotor1.stop();
     * Resources.launchMotor2.stop(); Lab5.sleepFor(1000); /* reset position
     */
<<<<<<< HEAD
    launch(115);
    launchMotor1.stop();
    launchMotor2.stop();
    Team4.sleepFor(500);

    launch(-100);
    launchMotor1.flt();
    launchMotor2.flt();

    Team4.sleepFor(3000);
    Sound.beepSequence();
    launch(115);
    // } */
    System.exit(0);
  }

  public static void launch(int angle) {
    Resources.launchMotor1.rotate(-angle, true);
    Resources.launchMotor2.rotate(-angle, false);
  }

  public static void lightSensorTest() {
=======
   
  //  } */
    System.exit(0);
  }
 /**
  * The light sensor test for intensity values under different conditions
  */
  public static void lightSensorTest()
  {
>>>>>>> 9b0408be69a1f80cf1356ffad1ccc0d7a02f2881
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
<<<<<<< HEAD

  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * 
   * @param distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that angle.
   * 
   * @param angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public static void Test() {
    while (true) {
      leftMotor.setSpeed(500);
      rightMotor.setSpeed(500);
      System.out.println("");
    }
  }

  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * TRACK * angle / 360.0);
  }
=======
  
>>>>>>> 9b0408be69a1f80cf1356ffad1ccc0d7a02f2881
}
