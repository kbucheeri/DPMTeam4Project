package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import ca.mcgill.ecse211.lab3.Odometer;
import ca.mcgill.ecse211.lab3.UltrasonicPoller;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class BangBangController extends UltrasonicController {
  private int distance;
  private int filterControl;
  private int turnCounter;
  public float dotMagnitudeRatio = 0;
  private double initLocation[];

  private double startEndVector[];
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private double TRACK;
  private static final double ERROR_THRESHOLD = 0.09;

  public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, final double WHEEL_RAD,
      final double TRACK) {
    // Default Constructor

    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
    this.TRACK = TRACK;
    this.filterControl = 0;
    this.turnCounter = 0;
  }

  /**
   * The distance, either positive or negative from the BAND_CENTER (cm).
   */
  public static int distError = 0;
  /**
   * The default rotation speed of the wheels in Deg/s.
   */
  public static final int FWDSPEED = 150;
  /**
   * Bang Bang constant for the right motor in Deg/s.
   */
  public static final int BANGBANG_RIGHT = 60;
  /**
   * Bang Bang constant for the left motor in Deg/s.
   */
  public static final int BANGBANG_LEFT = 100;
  /**
   * Variable for when the sensor is too close and must turn away as fast as possible
   */
  public static final int tooClose = -18;

  public void processUSData(int distance) {

    filter(distance);

    if (turnCounter > 0) {
      Odometer odo;

      odo = Odometer.getOdometer();
      double xyt[] = odo.getXYT();

      double[] currVector = new double[] {xyt[0] - this.initLocation[0], xyt[1] - this.initLocation[1]};

      // Dot product: determines when to stop performing the bang-bang corrections
      // using dot product calculations between the vector of the robot's position and
      // the robot's destination
      float dotProduct = (float) (currVector[0] * this.startEndVector[0] + currVector[1] * this.startEndVector[1]);
      float currMagnitude = (float) Math.pow(Math.pow(currVector[0], 2) + Math.pow(currVector[1], 2), 1 / 2.0);
      float startEndMagnitude =
          (float) Math.pow(Math.pow(this.startEndVector[0], 2) + Math.pow(this.startEndVector[1], 2), 1 / 2.0);
      float magnitudeProduct = currMagnitude * startEndMagnitude;
      dotMagnitudeRatio = dotProduct / magnitudeProduct;
      if (Math.abs(dotProduct / magnitudeProduct) >= 1 - ERROR_THRESHOLD) { 
        //If both products are very similar, end bang bang and resume normal navigation until another object appears
        UltrasonicPoller usPoller = UltrasonicPoller.getInstance();

        usPoller.isAvoiding = false;

        synchronized (usPoller.doneAvoiding) {
          usPoller.doneAvoiding.notifyAll();
        }

      }
    }

    distError = distance - BAND_CENTER; // Compute error



    // If the error is within the limits, continue forward.
    if (Math.abs(distError) <= BAND_WIDTH) {
      leftMotor.setSpeed(FWDSPEED);
      rightMotor.setSpeed(FWDSPEED);
      leftMotor.forward();
      rightMotor.forward();
    }

    if (distError < -10) {
      leftMotor.setSpeed(MOTOR_LOW);
      rightMotor.setSpeed(MOTOR_LOW);
      leftMotor.forward();
      rightMotor.backward();
    }

    // If the error is negative, move farther from the wall (right turn).
    else if (distError < 0) {
      if (distError < -BAND_WIDTH) {
        leftMotor.setSpeed(MOTOR_LOW);
        rightMotor.setSpeed(MOTOR_LOW);
        leftMotor.forward();
        rightMotor.backward();
      } 
      else {
        leftMotor.setSpeed(MOTOR_HIGH);
        rightMotor.setSpeed(MOTOR_LOW);
        leftMotor.forward();
        rightMotor.forward();
      }
    }

    // A positive error means we need to move closer to the wall (left turn).
    else if (distError > 0) {
      if (distError >= BAND_WIDTH) {
        turnCounter++;
      }
      leftMotor.setSpeed(FWDSPEED - BANGBANG_RIGHT);
      rightMotor.setSpeed(MOTOR_HIGH);
      leftMotor.forward();
      rightMotor.forward();
    }
  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }

  public void initBangBang(double endX, double endY) {
    Odometer odo;

    odo = Odometer.getOdometer();
    double xyt[] = odo.getXYT();

    this.initLocation = new double[] {xyt[0], xyt[1]};
    this.startEndVector = new double[] {xyt[0] - endX, xyt[1] - endY};


    turnCounter = 0;
    dotMagnitudeRatio = 0;


    leftMotor.setSpeed(MOTOR_LOW);
    rightMotor.setSpeed(MOTOR_LOW);

    leftMotor.rotate(convertAngle(WHEEL_RAD, TRACK, 90), true);
    rightMotor.rotate(-convertAngle(WHEEL_RAD, TRACK, 90), false);

  }

  /**
   * Rudimentary filter - toss out invalid samples corresponding to null signal.
   * 
   * @param distance distance in cm
   */
  void filter(int distance) {
    if (distance >= 255 && filterControl < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the filter value
      filterControl++;
    } else if (distance >= 255) {
      // Repeated large values, so there is nothing there: leave the distance alone
      this.distance = distance;
    } else {
      // Distance went below 255: reset filter and leave distance alone.
      filterControl = 0;
      this.distance = distance;
    }
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
