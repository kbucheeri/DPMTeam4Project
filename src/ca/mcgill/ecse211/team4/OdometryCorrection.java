package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import static ca.mcgill.ecse211.team4.Navigation.*;
import lejos.hardware.Sound;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
/**
 * 
 * @author Khaled
 * Does odometry correction by correcting the angle and distance
 */
public class OdometryCorrection {
  /**
   * Sets correcting status to true
   */
  public static void startCorrecting() {
    correctingStatus = true;
  }

  private static double initial_angle;
  private static boolean correctingStatus = false;

  /**
   * returns nearest line distance from origin (i.e if at 59, nearest line is at 30.48 * 2)
   * 
   * @param X
   */
  public static double determineNearestLine(double X) {
    return 30.48 * Math.round(X / 30.48);
  }

  /**
   * determines nearest angle of the 4 cardinal directions (multiples of 90)
   * 
   * @param theta
   * @return nearest angle
   */
  public static int determineAngle(double theta) {
    return (int) Math.round(theta / 90.0);
  }

  /**
   * Correct odometry if travelling parallel to coordinate axes
   */
  public static void correctParallel(double angle) {
    if (correctingStatus == true) // called once before
    {
      double[] currentPosition = odometer.getXYT();
      if (angle < 15 || angle > 340 || (angle > 160 && angle < 200)) // travelling // vertically
      {
        if (LightLocalizer.LOCALIZING == true) {
          odometer.setY(STARTING_Y - SENSOR_TO_WHEEL_DISTANCE);
        } else
          odometer.setY(determineNearestLine(currentPosition[1]) - SENSOR_TO_WHEEL_DISTANCE);

      } else // travelling horizontally
      {
        if (LightLocalizer.LOCALIZING == true) {
          odometer.setX(STARTING_X - SENSOR_TO_WHEEL_DISTANCE);
        } else
          odometer.setX(determineNearestLine(currentPosition[0]) - SENSOR_TO_WHEEL_DISTANCE);
      }


      if (LightLocalizer.LOCALIZING == true) {
        if (LightLocalizer.LOCALIZINGY == true)
          odometer.setTheta(0);
        else
          odometer.setTheta(90);
      } else {
        if (Math.abs(initial_angle - currentPosition[2]) < 30) // Too much correction. Probably a fault
          odometer.setTheta(determineAngle(odometer.getXYT()[2]));
      }
      correctingStatus = false;
      correctionDone = true; // correcting ended
    } else {
      startCorrecting();
      initial_angle = angle;
      correctionDone = false; // begun correcting
    }
  }

  private static boolean correctionDone = true;

  /**
   * tells robot if done correcting (To issue another navigation command)
   */
  public static boolean doneCorrecting() {
    return correctionDone;
  }

  /**
   * Used for navigation (only issues another motor command when it finishes correcting)
   */
  public static boolean isCorrecting() {
    return correctingStatus;
  }

  /**
   * Enable/disable odometry correction
   * 
   * @param status - set correction to true/false. True means you enable correction
   */
  public static void toggleCorrection(boolean status) {
    ENABLE_CORRECTION = status;
  }
  public static boolean ENABLE_CORRECTION = true;
}
