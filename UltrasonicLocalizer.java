package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.leftMotor;
import static ca.mcgill.ecse211.team4.Resources.rightMotor;
import ca.mcgill.ecse211.team4.Resources;
import lejos.hardware.Button;
import lejos.hardware.Sound;
/**
 * Performs ultrasonic localization
 *
 */
public class UltrasonicLocalizer {
  public static boolean sweepDone = false; // currently sweeping
  /**
   * Perform falling edge localization routine
   */
  public static void FallingEdge() {
    // record first edge angle, second edge angle, get average.
    int firstEdge = 370; // initalize to impossible value for the conditions later on
    int secondEdge = 370;
    int prevData = 0;
    leftMotor.setSpeed(130);
    rightMotor.setSpeed(130);
    leftMotor.forward();
    rightMotor.backward();
    // stop when both are not 370
    while (firstEdge == 370) {
      int theta = (int) Resources.odometer.getXYT()[2];
      int data = UltrasonicPoller.getDistance();
      /**
       * Is below threshold and there was also a drop TODO implement noise margin
       */
      if (data < Resources.EDGE_THRESHOLD && prevData > Resources.EDGE_THRESHOLD) {
        firstEdge = theta;
        Sound.beep();
        /*
         * switch directions
         */
      }
      prevData = data;

    }
    leftMotor.backward();
    rightMotor.forward();
    Main.sleepFor(500);
    prevData = 0;
    while (secondEdge == 370) {
      int theta = (int) Resources.odometer.getXYT()[2];
      int data = UltrasonicPoller.getDistance();
      /**
       * Is below threshold and there was also a drop
       */
      if (data < Resources.EDGE_THRESHOLD && prevData > Resources.EDGE_THRESHOLD) {
        secondEdge = theta;
        Sound.beep();

      }
      prevData = data;

    }
    // slow down ultrasonic polling because its no longer necessary; occupy less processor time.
    UltrasonicPoller.setSleepTime(500);
    // get average of both data
    int ave = (firstEdge + secondEdge) / 2;
    System.out.println("\n\n\n\n");
    System.out.println(firstEdge + ",  " + secondEdge + " average: " + ave);
    double dtheta;
    // detects back wall first. Since it rotates clockwise and detects falling edges.
    if (firstEdge > secondEdge)
      dtheta = 230 - ave;
    else
      dtheta = 230 - 180 - ave;
    Resources.odometer.incrementTheta(dtheta);
    // Navigation.turnTo(0);
    Sound.beepSequence();

    // wait for reading to stabilize before measuring vertical distance.

    Navigation.turnTo(0);
  }
  /**
   * Perform rising edge localization routine
   */
  public static void RisingEdge() {
    // record first edge angle, second edge angle, get average.
    int firstEdge = 370; // initalize to impossible value for the conditions later on
    int secondEdge = 370;
    int prevData = 100;
    leftMotor.setSpeed(130);
    rightMotor.setSpeed(130);
    leftMotor.forward();
    rightMotor.backward();
    // stop when both are not 370
    while (firstEdge == 370) {
      int theta = (int) Resources.odometer.getXYT()[2];
      int data = UltrasonicPoller.getDistance();
      /**
       * Is below threshold and there was also a drop TODO implement noise margin
       */
      if (data > Resources.EDGE_THRESHOLD && prevData < Resources.EDGE_THRESHOLD) {
        firstEdge = theta;
        Sound.beep();
        /*
         * switch directions
         */
      }
      prevData = data;

    }
    leftMotor.backward();
    rightMotor.forward();
    Main.sleepFor(300);
    prevData = 100;
    
    while (secondEdge == 370) {
      int theta = (int) Resources.odometer.getXYT()[2];
      int data = UltrasonicPoller.getDistance();
      /**
       * Is below threshold and there was also a drop
       */
      if (data > Resources.EDGE_THRESHOLD && prevData < Resources.EDGE_THRESHOLD) {
        secondEdge = theta;
        Sound.beep();

      }
      prevData = data;

    }
    // slow down ultrasonic polling because its no longer necessary; occupy less processor time.
    UltrasonicPoller.setSleepTime(500);
    // get average of both data
    int ave = (firstEdge + secondEdge) / 2;
    System.out.println("\n\n\n\n");
    System.out.println(firstEdge + ",  " + secondEdge + " average: " + ave);
    double dtheta;
    // detects left wall first
    if (firstEdge > secondEdge)
      dtheta = 220 - ave;
    else
      dtheta = 215 - 180 - ave;
    Resources.odometer.incrementTheta(dtheta);

    // wait for reading to stabilize before measuring vertical distance.

    Navigation.turnTo(0);
  }
}
