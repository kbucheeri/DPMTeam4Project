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
   * Prints values 10 times at 0.5 second intervals to test Ultrasonic sensor accuracy
   */
  public static void ultrasonicTest()
  {
    
  }
  /**
   * Runs a function 10,000 times in a loop to measure execution time
   */
  public static void codePerformanceTest() {
    
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
    
    Sound.beepSequenceUp();
   // Navigation.turn(180);
    launchMotor1.setSpeed(8000);
    launchMotor2.setSpeed(8000);
    
  /*  for(int i = 0; i < 5; i ++)
    {Resources.launchMotor1.rotate(-(70 + 5 * i), true);
    Resources.launchMotor2.rotate(-(70 + 5 * i), false);
    Resources.launchMotor1.stop();    
    Resources.launchMotor2.stop();
    Lab5.sleepFor(1000);
    /*
     * reset position
     */
   
  //  } */
    System.exit(0);
  }
 /**
  * The light sensor test for intensity values under different conditions
  */
  public static void lightSensorTest()
  {
    (new Thread() {
      public void run() {
        /**
         *  reset the motors
         */
        leftMotor.stop();
        rightMotor.stop();
        leftMotor.setAcceleration(ACCELERATION);
        rightMotor.setAcceleration(ACCELERATION);

        /***
         *  Sleep for 2 seconds
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
        leftMotor.setSpeed(0); //added these so that we can easily check if robot is travelling in a straight line 

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
  
}
