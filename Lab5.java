/**
 * Osman Warsi and Khalid Bucheeri
 */
package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * The main driver class for the odometry lab.
 */
public class Lab5 {

  /**
   * The main entry point.
   * 
   * @param args
   */
  public static void main(String[] args) {
    new Thread(new Display()).start();
    double Tx = 2 * TILE_SIZE + 15, Ty = TILE_SIZE * 5 + 15;

    Button.waitForAnyPress();
    // Launcher.launchThenWaitTest();
    new Thread(odometer).start();
    localize();
    travelToLaunchPoint(Tx, Ty);
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
   * Asks the user whether rising or falling edge localization should be used
   * 
   * @return the user choice
   */
  private static int chooseType() {
    int buttonChoice;
    Display.showText("< Left | Right >", "   " + "    |        ", " Stationary Launch   " + "   | Mobile Launch ",
        " <| >" + "  ", "       | ");

    do {
      buttonChoice = Button.waitForAnyPress(); // left or right press
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
    return buttonChoice;

  }

  /**
   * initiates localization routines
   */
  private static void localize() {
    System.out.println("max speed: " + launchMotor1.getMaxSpeed());
    new Thread(new UltrasonicPoller()).start();
    sleepFor(1000);
    UltrasonicLocalizer.RisingEdge();
    sleepFor(500);
    Sound.buzz();
    // increase sleep time so the thread doesnt consume as much time
    UltrasonicPoller.setSleepTime(2000);
    new Thread(new lightPoller()).start();
   // Button.waitForAnyPress();
    LightLocalizer.localizeDistance();
    sleepFor(1000);
    lightPoller.changeSleepTime(1000);
    // LightLocalizer.localizeAngle();
  }

  /**
   * Travels to face launch point T
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

}
