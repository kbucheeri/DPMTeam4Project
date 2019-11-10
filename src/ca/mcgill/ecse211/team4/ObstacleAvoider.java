package ca.mcgill.ecse211.team4;
import java.util.*;
import static ca.mcgill.ecse211.team4.Resources.*;

/**
 * This class handles Obstacle avoidance
 * @author Team 4
 *
 */
public class ObstacleAvoider extends Navigation{
/**
 * Initiates wall following and avoidance routine. Then returns command to the Navigation to continue
 * travelling to original target.
 * @param x 
 * @param y
 * @param theta
 */

private static int TURN_AMOUNT = 125;

private ArrayList<double[]> coordsList;


/**
 * Speed of wheels during forward movement in deg/s.
 */
private static final int FORWARD_SPEED = 250;
/**
 * Speed of wheel during rotation toward new waypoint in deg/s.
 */
private static final int ROTATE_SPEED = 150;
private final double WHEEL_RAD;
private final double WHEEL_BASE;
private final double TILE_SIZE;

private Odometer odometer;
private double x, y, theta;
private static boolean isNavigating = false;

public ObstacleAvoider(Odometer odo, final double WHEEL_RAD, final double WHEEL_BASE, double tileSize) {

  this.odometer = odo;
  this.WHEEL_RAD = WHEEL_RAD;
  this.WHEEL_BASE = WHEEL_BASE;
  this.TILE_SIZE = tileSize;
  this.coordsList = new ArrayList<double[]>();

}


boolean boolTravelTo(double navX, double navY) {

  UltrasonicPoller usPoller = null;
  if (UltrasonicPoller.getInstance() != null) {
    usPoller = UltrasonicPoller.getInstance();
  }

  // Get current coordinates.
  theta = odometer.getXYT()[2];
  x = odometer.getXYT()[0];
  y = odometer.getXYT()[1];


  double deltaX = navX - x;
  double deltaY = navY - y;

  // Get absolute values of deltas.
  double absDeltaX = Math.abs(deltaX);
  double absDeltaY = Math.abs(deltaY);

  // Need to convert theta from degrees to radians.
  double deltaTheta = Math.atan2(deltaX, deltaY) / Math.PI * 180;

  // Turn to the correct direction.
  this.turnTo(theta, deltaTheta);
  // Move until destination is reached.
  // While loop is used in case of collision override.
  leftMotor.setSpeed(FORWARD_SPEED);
  rightMotor.setSpeed(FORWARD_SPEED);
  leftMotor.forward();
  rightMotor.forward();

  while (true) {
    double newX, newY;

    double xyt[] = odometer.getXYT();
    // Need to convert theta from degrees to radians.
    newX = xyt[0];
    newY = xyt[1];

    // If the difference between the current x/y and the x/y we started from is similar to the deltaX/deltaY,
    // stop the motors because the point has been reached.
    if (Math.pow(newX - x, 2) + Math.pow(newY - y, 2) > Math.pow(absDeltaX, 2) + Math.pow(absDeltaY, 2)) {
      break;
    }

    // This is only relevant if the ultrasonic poller thread is being used.
    if (usPoller != null) {
      if (usPoller.isInitializing) { // isInitializing is true when the distance is too close.
        leftMotor.stop(true);
        rightMotor.stop(false);
        usPoller.init(navX, navY);

        try {
          synchronized (usPoller.doneAvoiding) {
            while (usPoller.isAvoiding) { // Checking if thread is paused by another thread
              usPoller.doneAvoiding.wait();
            }
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        this.coordsList.add(0, new double[] {navX, navY});

        return false;
      }
    }

    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
  leftMotor.stop(true);
  rightMotor.stop(false);
  this.isNavigating = false;
  return true;
}

// This method causes the robot to turn (on point) to the absolute heading theta. This method
// will turn a MINIMAL angle to its target.
/**
 * Turns the robot to the absolute (minimal) heading theta
 * 
 * @param currTheta current theta
 * @param destTheta to turn robot by
 */
void turnTo(double currTheta, double destTheta) {
  // get theta difference
  double deltaTheta = destTheta - currTheta;
  // normalize theta (get minimum value)
  deltaTheta = normalizeAngle(deltaTheta);

  leftMotor.setSpeed(ROTATE_SPEED);
  rightMotor.setSpeed(ROTATE_SPEED);

  leftMotor.rotate(convertAngle(WHEEL_RAD, WHEEL_BASE, deltaTheta), true);
  rightMotor.rotate(-convertAngle(WHEEL_RAD, WHEEL_BASE, deltaTheta), false);
}

// Getting the minimum angle to turn:
// It is easier to turn +90 than -270
// Also, it is easier to turn -90 than +270
double normalizeAngle(double theta) {
  if (theta <= -180) {
    theta += 360;
  } else if (theta > 180) {
    theta -= 360;
  }
  return theta;
}


public static boolean isNavigating() {
  return isNavigating;
}

/**
 * This method allows the conversion of a distance to the total rotation of each wheel need to cover that distance.
 * 
 * @param radius
 * @param distance
 * @return
 */
private static int convertDistance(double radius, double distance) {
  return (int) ((180.0 * distance) / (Math.PI * radius));
}

private static int convertAngle(double radius, double width, double angle) {
  return convertDistance(radius, Math.PI * width * angle / 360.0);
}

}
