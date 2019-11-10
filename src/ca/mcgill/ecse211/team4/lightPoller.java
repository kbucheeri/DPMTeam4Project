package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import static ca.mcgill.ecse211.team4.Navigation.*;
import lejos.hardware.Sound;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

/**
 * Samples the light sensor and applies filtering and difference calculations.
 * Samples the left sensor
 */
public class lightPoller implements TimerListener {
  public static int lintensity;
  public static int ldiff;
  // used for correction. If line detected, its true. Used to distinguish between stops by this or navigation.
  private static boolean lstoppedFlag = false;
  static boolean steadyState = true;
  private static Timer lightTimer;
  private static lightPoller lPoller;
  public static int[] lbuffer = new int[3];
  public static int averageColor;

  /**
   * intialize buffer array and lightTimer
   */
  public static void initialize(int rate) {
    double sum = 0;
    for (int i = 0; i < 30; i++) {
      float[] lightData = new float[leftSensor.sampleSize()];
      leftSensor.getRedMode().fetchSample(lightData, 0);
      sum += (lightData[0] * 1024) / 30.0;
      Main.sleepFor(30);
    }
    averageColor = (int) sum;
    lintensity = averageColor;
 //   System.out.println(averageColor);
    lPoller = new lightPoller();
    lightTimer = new Timer(rate, lPoller);
  }

  /**
   * begin polling
   */
  public static void begin() {
    lightTimer.start();
  }

  /**
   * changes the polling rate
   */
  public static void changeRate(int newRate) {
    lightTimer.setDelay(newRate);
  }

  /**
   * stops polling
   */
  public static void stop() {
    lightTimer.stop();
  }

  public static int getIntensity() {
    return ldiff;
  }

  /**
   * calculates the intensity from the light sensor and applies rudimentary filtering In the form of the arithmetic
   * mean.
   * 
   * @return The average of the previous light sensor intensity values.
   */
  public static void calculateIntensity() {
    float[] lightData = new float[leftSensor.sampleSize()];
    leftSensor.getRedMode().fetchSample(lightData, 0);
    /**
     * Resizing the actual intensity values to make it more readable and thus easier to test. Also easier to deal with
     * ints than double precision
     */
    lintensity = (int) (lightData[0] * 1024);
    // System.out.println(lintensity + ", " + lprevIntensity);
  }


  public static int signedSquare(int num) {
    return (int) (num * num * Math.signum(num));
  }

  /**
   * Performs light polling Assumes it starts facing 0 degrees in the corner block
   */
  public void timedOut() {
    // if light sensing returns to 0 to prevent multiple line readings.
    // System.out.println(lintensity + ", " + lprevIntensity);
    calculateIntensity();
    ldiff = lintensity - averageColor;
    double angle = 0;
    // DETECTED A LINE
    if (ldiff < LIGHT_DIFF_THRESHOLD && leftMotor.isMoving() && steadyState == true
        && OdometryCorrection.ENABLE_CORRECTION == true) {
      steadyState = false;
      /**
       * slow down other motor if this is 1st line detected
       */
      if (rightMotor.isMoving() == true) {
        angle = odometer.getXYT()[2];
        rightMotor.setSpeed(100);
      }
      //System.out.println("\n detected left line \n");
      leftMotor.stop();
      OdometryCorrection.correctParallel(angle);
      lstoppedFlag = true;
      // Navigation.travelTo(currentXdest, currentYdest); //continue navigting to old desitination.
    }
      if (ldiff > -20)
        steadyState = true;

   // System.out.println(((int) (odometer.getXYT()[1] * 100)) / 100.0 + ", " + signedSquare(ldiff) + ",  " + ldiff);

  }


  /*
   * returns RMS of an int array
   * 
   */
  public static int average(int[] arr) {
    /*
     * double sum = 0; for (int i = 0; i < arr.length; i++) sum = sum + arr[i] * arr[i] * Math.signum(arr[i]);
     * 
     * return (int) Math.sqrt((sum / arr.length));
     */
    int sum = 0;
    for (int i = 0; i < arr.length; i++)
      sum = sum + arr[i];
    return sum / arr.length;
  }

  /**
   * Shifts values in buffer and stores newest value in array
   * 
   * @param intensity
   * @param buffer
   */
  public static void shiftBuffer(int intensity, int[] buffer) {
    for (int i = 0; i < buffer.length - 1; i++) {
      buffer[i] = buffer[i + 1];
    }
    buffer[buffer.length - 1] = intensity;
  }
}
