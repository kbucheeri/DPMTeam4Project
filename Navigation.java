package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import ca.mcgill.ecse211.team4.Resources;
import lejos.hardware.Sound;
/**
 * Contains all the static methods related to navigation
 * @author Khaled Bucheeri
 *
 */
public class Navigation {
  private static boolean navigationStatus = false;

  public Navigation() {
    leftMotor.stop();
    rightMotor.stop();
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
  }

  /**
   * 
   * @param x absolute position of the coordinate to travel to.
   * @param y absolute position of the coordinate to travel to.
   */
  public static void travelTo(double x, double y) {
    navigationStatus = true;
   
    double[] position = Resources.odometer.getXYT();
    /**
     * vector to move from current position to the next
     */
    double[] movementVector = {x - position[0], y - position[1]};

    /*
     * angle needed to face the robot towards the movement vector arctan(y/x)
     */
    double angle = Math.atan2(movementVector[0], movementVector[1]) * 180 / Math.PI; // signed arctan
    if (angle < 0) // set it to between 0 and 360
      angle += 360;
    turnTo(angle);

    // essentially a polar problem
    // move forwards a distance of the magnitude
    double magnitude = Math.hypot(movementVector[0], movementVector[1]);

    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);
    leftMotor.rotate(convertDistance(magnitude), true);
    rightMotor.rotate(convertDistance(magnitude), false); // executes next line first
  /*  while (true) {
      if ((leftMotor.isMoving() && rightMotor.isMoving()) == false) // one motor isnt moving then stop
        // || UltrasonicPoller.getDistance() <= 9)
        break;
    }
*/
    navigationStatus = false; // only sets it to false after travel function terminates
    // System.out.println("ended navigation!");
  }

  /**
   * loads the waypoints in the navigator
   * 
   * @param wayPointsX list of x coordinates of the waypoints
   * @param wayPointsY list of y coordinates of the waypoints
   */
  public static void loadWaypoints(int[] wayPointsX, int[] wayPointsY) {
    for (int i = 0; i < wayPointsX.length;) {
      if (isNavigating() == false) { // only receive new command if its not navigating
        travelTo(wayPointsX[i] * TILE_SIZE, wayPointsY[i] * TILE_SIZE);
        i++; // only count up if the first one ended.
      }
    }
  }

  /**
   * turns by a set amount instead of to an absolute angle relative to the Y-axis TODO test it
   * 
   * @param theta relative angle to turn to
   */
  public static void turn(double theta) {
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.rotate(convertAngle(theta), true);
    rightMotor.rotate(-convertAngle(theta), false);
  }

  /**
   * 
   * @param theta angle to turn to in absolute units
   */
  public static void turnTo(double theta) {
    // take in current angle
    double currentAngle = Resources.odometer.getXYT()[2];
    double turn = theta - currentAngle;
    if (Math.abs(turn) > 180) // maximal turn
    {
      if (turn > 0)
        turn -= 360; // i.e from 270 to -90
      else
        turn += 360;
    }
    // System.out.println("Current turn: " + turn);
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.forward();
    rightMotor.forward();
    leftMotor.rotate(convertAngle(turn), true);
    rightMotor.rotate(-convertAngle(turn), false);
  }

  /**
   * Turns to face a point (X, Y) in absolute position
   * 
   * @param x
   * @param y
   */
  public static void turnToPoint(double x, double y) {
    double[] currentPosition = odometer.getXYT();
    // vector of the point from the robot
    // Alternatively, the positon of the point with the robot as the origin
    double vector[] = {currentPosition[0] - x, currentPosition[1] - y};
    // turn to the angle of the vector
    double angle = Math.atan2(vector[0], vector[1]);
    if (angle < 0) // set it to between 0 and 360
      angle += 360;
    turnTo(angle);
  }


  /**
   * 
   * @return if the robot is currently navigating
   */
  public static boolean isNavigating() {
    return navigationStatus;
  }

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
  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * TRACK * angle / 360.0);
  }

  /**
   * 
   *
   * @return direction of the cross product of the distance and movement. This is the direction of axis of rotation. 1
   *         if clockwise, -1 otherwise Used to determine whether robot should turn left or right if it detects obstacle
   *         so that it bump into wall
   */
  public static int crossProduct() {
    double[] position = odometer.getXYT();
    double[] radius = {2 * TILE_SIZE - position[0], 2 * TILE_SIZE - position[1]};
    double[] movement = {Math.sin(position[2] * Math.PI / 180), Math.cos(position[2] * Math.PI / 180)}; // unit vector
                                                                                                        // in direction
                                                                                                        // of motion
    if (radius[0] * movement[1] - radius[1] * movement[0] > 0) // rxvy - ryvx
      return -1;
    else
      return 1;
  }
}

