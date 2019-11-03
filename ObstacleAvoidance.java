package ca.mcgill.ecse211.team4;
import static ca.mcgill.ecse211.team4.Resources.*;
import lejos.hardware.Sound;
import ca.mcgill.ecse211.team4.Navigation.*;
public class ObstacleAvoidance {

  /**
   * dumb implementation. Rotates orthogonally to the block, travels worst case scenario to avoid 21 cm left, 21 cm
   * right 21 cm forward Needs to be in seperate thread from the ultrasonic poller (ObstacleAvoider)
   */

  public static int TURN_AMOUNT = 125;
  public static void obstacleAvoiderSimple(double x, double y, double theta) {

    leftMotor.stop();
    rightMotor.stop();
    int dir = Navigation.crossProduct();
    Navigation.turnTo(theta);
    try {
      Thread.sleep(300); // time for readings to stabilize
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    Navigation.turn(-90 * dir);
    leftMotor.setSpeed(FOLLOWER_SPEED);
    rightMotor.setSpeed(FOLLOWER_SPEED);
    int dist = UltrasonicPoller.getDistance();
    /**
     * "Wall follower 1"
     */
    while (true) {
      leftMotor.rotate(Navigation.convertDistance(10), true);
      rightMotor.rotate(Navigation.convertDistance(10), false);
      Navigation.turn(TURN_AMOUNT * dir);

      /*
       * wait for an amount of time for readings to stabilize
       */
      try {
        Thread.sleep(SLEEP_TIME); // time for readings to stabilize
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
      dist = UltrasonicPoller.getDistance();
      if (dist > BANDCENTRE)
        break;
      
      Navigation.turn(-TURN_AMOUNT * dir);
    }
    Sound.beep();
    Navigation.turnTo(theta - 25 * dir); // original angle + extra amount so it doesn't crash into block
    Team4.sleepFor(SLEEP_TIME);
    while (true) {
      leftMotor.rotate(Navigation.convertDistance(26), true);
      rightMotor.rotate(Navigation.convertDistance(26), false);
      Navigation.turn(TURN_AMOUNT * dir);
      try {
        Thread.sleep(SLEEP_TIME); // time for readings to stabilize
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
      dist = UltrasonicPoller.getDistance();

      if (dist > BANDCENTRE)
        break;
      Navigation.turn(-TURN_AMOUNT * dir);
    }
    leftMotor.stop();
    rightMotor.stop();
    Sound.beep();
    Navigation.travelTo(x, y);
  }

}
