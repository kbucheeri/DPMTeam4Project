package ca.mcgill.ecse211.team4;
import static ca.mcgill.ecse211.team4.Resources.*;
import static ca.mcgill.ecse211.team4.Navigation.*;
import lejos.hardware.Sound;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
public class OdometryCorrection {

  /**
   * returns true if X, Y otherwise
   * @param X
   * @param Y
   * @return
   */
  private static int determineNearestLine(int X)
  {
    return Math.round(X / 3048);
  }
  /**
   * Correct odometry if travelling parallel to coordinate axes
   */
  public static void correctParallel()
  {
    double[] currentPos = odometer.getXYT();
    if(currentPos[2] < 15 || currentPos[2] > 340 || (currentPos[2] < 200 && currentPos[2] > 160)) //travelling vertically
      odometer.setY(30.48 * determineNearestLine((int) currentPos[1]));
    else
      odometer.setX(30.48 * determineNearestLine((int) currentPos[0]));
    
  }
}
