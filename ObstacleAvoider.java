package ca.mcgill.ecse211.team4;
/**
 * This class handles Obstacle avoidance
 * @author Khaled
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

public static void obstacleAvoid(double x, double y, double theta) {

  Navigation.travelTo(x, y);
}

}
