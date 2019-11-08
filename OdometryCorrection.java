package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import static ca.mcgill.ecse211.team4.Navigation.*;
import lejos.hardware.Sound;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class OdometryCorrection {
  public static void startCorrecting() {
    correctingStatus = true;
  }

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
      if (angle < 15 || angle > 340 
          || (angle < 200 && angle > 160)) // travelling                                                                                                                       // vertically
      {
        if(LightLocalizer.LOCALIZING == true)
        {
          odometer.setY(STARTING_Y - SENSOR_TO_WHEEL_DISTANCE);
        }
        else
          odometer.setY(determineNearestLine(currentPosition[1]));
        
      } 
      else // travelling horizontally
        {
        if(LightLocalizer.LOCALIZING == true)
        {
          odometer.setX(STARTING_X - SENSOR_TO_WHEEL_DISTANCE);
        }
        odometer.setX(determineNearestLine(currentPosition[0]));      
        }
      odometer.setTheta(determineAngle(angle));
      correctingStatus = false;
    } else
      startCorrecting();
  }

  /**
   * Used for navigation (only issues another motor command when it finishes correcting)
   */
  public static boolean isCorrecting() {
    return correctingStatus;
  }
}
