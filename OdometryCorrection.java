package ca.mcgill.ecse211.team4;
import static ca.mcgill.ecse211.team4.Resources.*;
import static ca.mcgill.ecse211.team4.Navigation.*;
import lejos.hardware.Sound;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
public class OdometryCorrection {
	private static boolean correctingStatus = false;
  /**
   * returns nearest line distance from origin (i.e if at 59, nearest line is at 30.48 * 2)
   * @param X
   */
  public static double determineNearestLine(double X)
  {
    return 30.48 * Math.round(X / 30.48);
  }
  /**
   * determines nearest angle of the 4 cardinal directions (multiples of 90)
   * @param theta
   * @return nearest angle
   */
  public static int determineAngle(double theta)
  {
	  return (int) Math.round(theta / 90);
  }
  /**
   * Correct odometry if travelling parallel to coordinate axes
   */
  public static void correctParallel()
  {
	  correctingStatus = true;
    double[] currentPosition = odometer.getXYT();
    if(currentPosition[2] < 15 || currentPosition[2] > 340 || (currentPosition[2] < 200 && currentPosition[2] > 160)) //travelling vertically
      odometer.setY(determineNearestLine(currentPosition[1]));
    else //travelling horizontally
      odometer.setX(determineNearestLine(currentPosition[0]));
    odometer.setTheta(determineAngle(currentPosition[2]));
    correctingStatus = false;
  }
  
  /**
   * Used for navigation (only issues another motor command when it finishes correcting)
   */
  public static boolean isCorrecting()
  {
	  return correctingStatus;
  }
}
