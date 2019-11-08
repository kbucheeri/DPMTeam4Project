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
public class lightPoller implements TimerListener {

  private static Timer lightTimer;
  private static lightPoller lPoller;
  public static int[] lbuffer = new int[5];
  public static int[] rbuffer = new int[5];

  /**
   * intialize buffer array and lightTimer
   */
  public static void initialize(int rate) {
    initializeArray(lbuffer);
    initializeArray(rbuffer);
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

  public static int average = 0;

  /**
   * calculates the intensity from the light sensor and applies rudimentary filtering In the form of the arithmetic
   * mean.
   * 
   * @return The average of the previous light sensor intensity values.
   */
  public static void calculateIntensity() {
    float[] lightData = new float[lightSensor.sampleSize()];
    float[] rightData = new float[lightSensor.sampleSize()];
    lightSensor.getRedMode().fetchSample(lightData, 0);
    rightSensor.getRedMode().fetchSample(rightData, 0);
    /**
     * Resizing the actual intensity values to make it more readable and thus easier to test. Also easier to deal with
     * ints than double precision
     */
    lintensity = (int) (lightData[0] * 2048);
    rintensity = (int) (rightData[0] * 2048);
    shiftBuffer(rintensity, rbuffer);
    shiftBuffer(lintensity, lbuffer);
    lintensity = average(lbuffer);
    rintensity = average(rbuffer);
    // System.out.println(lintensity + ", " + lprevIntensity);
  }

  public static int lprevIntensity;
  public static int lintensity;
  public static int rprevIntensity;
  public static int rintensity;
  public static int ldiff;
  public static int rdiff;
  // used for correction. If line detected, its correct.
  private static boolean lstoppedFlag = false;

  public static int signedSquare(int num) {
    return (int) (num * num * Math.signum(num));
  }

  /**
   * Performs light polling Assumes it starts facing 0 degrees in the corner block
   */
  public void timedOut() {
    // if light sensing returns to 0 to prevent multiple line readings.
    lprevIntensity = lintensity;
    rprevIntensity = rintensity;
    // System.out.println(lintensity + ", " + lprevIntensity);
    boolean steadyState = true;
    calculateIntensity();
    ldiff = lintensity - lprevIntensity;
    rdiff = rintensity - rprevIntensity;
    // DETECTED A LINE
    if (signedSquare(ldiff) < LIGHT_DIFF_THRESHOLD && leftMotor.isMoving()) {
      leftMotor.stop();
    //  steadyState = false;
      if (rightMotor.isMoving()) {      

        lightPoller.changeRate(LIGHT_RATE / 2);// increase the polling rate
      } 
      /*
       * Stopped flag is true which means that the right motor 
       * had stopped before and the left motor is currently rotating
       */
      else {       
        System.out.println("Entered here");
 
        lightPoller.changeRate(LIGHT_RATE); //reset polling rate
      
        OdometryCorrection.correctParallel();
        Navigation.travelTo(currentXdest, currentYdest); //continue navigting to old desitination.       
      }
    }    
    
    if(signedSquare(rdiff) < LIGHT_DIFF_THRESHOLD  && rightMotor.isMoving()) {
      Sound.buzz();
      rightMotor.stop();
      Sound.buzz();
      if (leftMotor.isMoving()) {      

        lightPoller.changeRate(LIGHT_RATE / 2);// increase the polling rate
      } else {
        System.out.println("Entered here");
        lightPoller.changeRate(LIGHT_RATE); //reset polling rate
        OdometryCorrection.correctParallel();
        Navigation.travelTo(currentXdest, currentYdest); //continue navigting to old desitination.       
      }
    }
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

    float[] lightData = new float[lightSensor.sampleSize()];
    lightSensor.getRedMode().fetchSample(lightData, 0);
    /**
     * Resizing the actual intensity values to make it more readable and thus easier to test. Also easier to deal with
     * ints than double precision
     */

    int init = (int) (lightData[0] * 512);
    for (int i = 0; i < buffer.length; i++) {
      buffer[i] = init;
    }

    lprevIntensity = lintensity;
    rprevIntensity = rintensity;
    ldiff = 5;
    rdiff = 5;
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
