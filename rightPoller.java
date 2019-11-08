package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.*;
import static ca.mcgill.ecse211.team4.Navigation.*;
import lejos.hardware.Sound;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

/**
 * Samples the light sensor and applies filtering and difference calculations.
 *
 */
public class rightPoller implements TimerListener {

  private static Timer lightTimer;
  private static rightPoller lPoller;
  public static int[] lbuffer = new int[3];

  /**
   * intialize buffer array and lightTimer
   */
  public static void initialize(int rate) {
    initializeArray(lbuffer);
    lPoller = new rightPoller();
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

  public static int average = 0;

  /**
   * calculates the intensity from the light sensor and applies rudimentary filtering In the form of the arithmetic
   * mean.
   * 
   * @return The average of the previous light sensor intensity values.
   */
  public static void calculateIntensity() {
    float[] lightData = new float[rightSensor.sampleSize()];
    rightSensor.getRedMode().fetchSample(lightData, 0);
    /**
     * Resizing the actual intensity values to make it more readable and thus easier to test. Also easier to deal with
     * ints than double precision
     */
    lintensity = (int) (lightData[0] * 2048);
    shiftBuffer(lintensity, lbuffer);
    lintensity = average(lbuffer);
    // System.out.println(lintensity + ", " + lprevIntensity);
  }

  public static int lprevIntensity;
  public static int lintensity;
  public static int ldiff;
  // used for correction. If line detected, its true. Used to distinguish between stops by this or navigation.
  private static boolean lstoppedFlag = false;
  static boolean steadyState = true;
  public static int signedSquare(int num) {
    return (int) (num * num * Math.signum(num));
  }

  /**
   * Performs light polling Assumes it starts facing 0 degrees in the corner block
   */
  public void timedOut() {
    // if light sensing returns to 0 to prevent multiple line readings.
    lprevIntensity = lintensity;
    // System.out.println(lintensity + ", " + lprevIntensity);
    calculateIntensity();
    ldiff = lintensity - lprevIntensity;
    double angle = 0;
    // DETECTED A LINE
    if (signedSquare(ldiff) < LIGHT_DIFF_THRESHOLD && rightMotor.isMoving() && steadyState == true) {
      steadyState= false;
      /**
       * slow down other motor if this is 1st line detected
       */
      if(leftMotor.isMoving() == true)
        {
          angle = odometer.getXYT()[2];
          leftMotor.setSpeed(80);
        }
      rightMotor.stop();
      OdometryCorrection.correctParallel(angle);
      lstoppedFlag = true;
      Sound.twoBeeps();
     //   Navigation.travelTo(currentXdest, currentYdest); //continue navigting to old desitination.       
      }    
    LCD.drawString("l: " + ldiff, 0, 5);
    if(lstoppedFlag == true)
    System.out.print(odometer.getXYT()[1]);
    System.out.println(", " + ldiff);
    if(signedSquare(ldiff) > 0)
      steadyState = true;
    /*
     * if(t == true) System.out.println(((int) (odometer.getXYT()[1] * 100)) / 100.0 + ", " + signedSquare(ldiff) +
     * ",  " + ldiff);
     */
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
   * initalizes the buffer array
   * 
   * @param buffer array to initalize
   */
  public static void initializeArray(int[] buffer) {

    float[] lightData = new float[rightSensor.sampleSize()];
    rightSensor.getRedMode().fetchSample(lightData, 0);
    /**
     * Resizing the actual intensity values to make it more readable and thus easier to test. Also easier to deal with
     * ints than double precision
     */

    int init = (int) (lightData[0] * 512);
    for (int i = 0; i < buffer.length; i++) {
      buffer[i] = init;
    }

    lprevIntensity = lintensity;
    ldiff = 5;
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