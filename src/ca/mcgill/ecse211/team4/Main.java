/**
 * Khaled Bucheeri
 */
package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Timer;

/**
 * The main driver class for the entire program.
 * @version 1.00
 */
public class Main {
  public static boolean ENABLE_CORRECTION = true;

  /**
   * The main entry point.
   * 
   * @param args
   */
  public static void main(String[] args) {
   // double Tx = 2 * TILE_SIZE + 15, Ty = TILE_SIZE * 5 + 15;
   // new Thread(new Display()).start();
    new Thread(odometer).start();
 Launcher.launchThenWaitTest();
    //System.out.println("max speed: " + launchMotor1.getMaxSpeed());
    UltrasonicPoller usPoller = new UltrasonicPoller();
    Timer usTimer = new Timer(220, usPoller);
    lightPoller.initialize(200); 
    rightPoller.initialize(200);
    usTimer.start();
    sleepFor(200);
    UltrasonicLocalizer.RisingEdge();
    sleepFor(500); 
    Sound.buzz();
    usTimer.setDelay(5000);     // increase sleep time to decrease processing requirement*/
    lightPoller.begin();
    rightPoller.begin();
    LightLocalizer.localizeDistance();
    Button.waitForAnyPress();
    Main.ENABLE_CORRECTION = false;
    Navigation.travelToParallel(30.5, 30.5);
    Button.waitForAnyPress(); 
   
    System.exit(0);
    Navigation.travelTo(0, TILE_SIZE * 5);

    
    
    
    Button.waitForAnyPress();
    // Launcher.launchThenWaitTest();
   
    localize();
   
    // didn't test turnToPoint
    // Navigation.turnToPoint(Tx, Ty);
    // Launcher.launchThenWaitTest();
  //  Button.waitForAnyPress();
    Launcher.launchThenWaitTest();
    System.out.println("max speed" + launchMotor1.getMaxSpeed());
    Sound.beepSequenceUp();

    Button.waitForAnyPress();

    LCD.clear();
    /*
     * // new Thread(new OdometryCorrectionTest()).start();
     */
    while (true) {
      /*
       * navigated to last pooint, receive next command
       */


      if (Button.waitForAnyPress() != Button.ID_ESCAPE)
        System.exit(0);

    }
  }


  
  /**
   * initiates localization routines (ultrasonic, light)
   */
  private static void localize() {
    System.out.println("max speed: " + launchMotor1.getMaxSpeed());
    UltrasonicPoller usPoller = new UltrasonicPoller();
    Timer usTimer = new Timer(100, usPoller);
    sleepFor(500);
    UltrasonicLocalizer.RisingEdge();
    sleepFor(500);
    Sound.buzz();
    usTimer.setDelay(1000);     // increase sleep time to decrease processing requirement
   // new Thread(new lightPoller()).start();
   // Button.waitForAnyPress();
    // LightLocalizer.localizeAngle();
  }

  /**
   * Travels to the launch point on the island
   * 
   * @param Tx - centre of the square x coordinate
   * @param Ty - centre of the square y coordinate
   */
  public static void travelToLaunchPoint(double Tx, double Ty) {

    double dist = Math.hypot(Tx, Ty); // distane to robot, since robot starts at 0,0
    int bound = (int) (120 + Math.max(20, TRACK));
    double distCentreX = 90 - Tx; // distance from centre for cases where traget square is near centre
    double distCentreY = 90 - Ty;
    if (Math.abs(dist - 120.0) < 2) // already within the error
    {
      Navigation.turnTo(90 - Math.toDegrees(Math.atan2(Ty, Tx)));
    } else if (Ty > 60 && Ty < 120 && (Tx > 60 && Tx < 120)) {
      /*
       * (Tx > 90) { sleepFor(100); Navigation.travelTo(Tx - 120 / 1.41 - 2 * distCentreX, Ty + 120 / 1.41 + 2 *
       * distCentreY); sleepFor(1000); Navigation.turnToPoint(Tx, Ty); sleepFor(500); } else { sleepFor(100);
       * Navigation.travelTo(Tx - 120 / 1.41 - 2 * distCentreX, Ty + 120 / 1.41 + 2 * distCentreY); sleepFor(1000);
       * Navigation.turnToPoint(Tx, Ty); sleepFor(500); }
       */
      sleepFor(100);
      Navigation.travelTo(Tx + 120 / 1.41 + 2 * distCentreX, Ty + 120 / 1.41 + 2 * distCentreY);
      sleepFor(1000);
      Navigation.turnToPoint(Tx, Ty - 30);
      sleepFor(500);
    } else if (Ty < bound) {
      Navigation.travelTo(Tx, Ty + 120 + 8 + 30);
      sleepFor(1000);
      Navigation.turnTo(180);
      sleepFor(500);
    } else if (Ty > bound) {
      
      
      
      Navigation.travelTo(Ty - 5 - 120 - 25, Tx - 15 + 15);
      Sound.twoBeeps();
      sleepFor(1000);
      Navigation.turnTo(90);
      sleepFor(600);
      Navigation.turnTo(90);
      sleepFor(600);
      
    } else if (Tx < bound) {
      Navigation.travelTo(Tx + 120 + 8, Ty + 30 );
      Sound.twoBeeps();
      sleepFor(1000);
      Navigation.turnTo(270);
      sleepFor(500);
    } else if (Tx > bound) {
      Navigation.travelTo(Tx - 5 - 120, Ty + 30);
      Sound.twoBeeps();
      sleepFor(1000);
      Navigation.turnTo(90);
      sleepFor(500);
    }
  }

  /**
   * Sleeps current thread for the specified duration.
   * 
   * @param duration sleep duration in milliseconds
   */
  public static void sleepFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }
  /**
   * includes a loop, just in case
   */
  public static void travelTo(double x, double y)
  {
    int count = 0;
    while(true)
    {
      count++;
      if(count > 5)
        break;
    /*  System.out.println("Current: " + (int) x + ", " + (int) y + ", " + (int) odometer.getXYT()[2]);
      System.out.println(Navigation.isNavigating()); System.out.println(LightLocalizer.LOCALIZING);
      System.out.println(OdometryCorrection.doneCorrecting() ); System.out.println(OdometryCorrection.isCorrecting());
      System.out.println("Distance: " + Math.hypot(odometer.getXYT()[0] - x, odometer.getXYT()[1] - y)); */
      if(Navigation.isNavigating() == false) //done navigating
      {
          if(Math.hypot(odometer.getXYT()[0] - x, odometer.getXYT()[1] - y) < 0.25
              || LightLocalizer.LOCALIZING == true) //close to the target
          { break;}
          else if(OdometryCorrection.doneCorrecting() == true || OdometryCorrection.isCorrecting() == false) //&& OdometryCorrection.isCorrecting() == false) //dont want to keep going forward if localizing
            Navigation.travelToParallel(x, y); 
      }
      //currently navigating
      sleepFor(200);
    }
    Navigation.travelToParallel(x, y);
  }
}
