package ca.mcgill.ecse211.team4;

import static ca.mcgill.ecse211.team4.Resources.lightSensor;
import static ca.mcgill.ecse211.team4.Resources.*;
import static ca.mcgill.ecse211.team4.Resources.leftMotor;
import static ca.mcgill.ecse211.team4.Resources.odometer;
import static ca.mcgill.ecse211.team4.Resources.rightMotor;
import lejos.hardware.Sound;
import lejos.utility.TimerListener;
/**
 * Samples the light sensor and applies filtering and difference calculations.
 *
 */
public class lightPoller implements TimerListener {
  public static int[] lbuffer = new int[4];
  public static int[] rbuffer = new int[4];
  /**
   * intialize buffer array
   */
  public lightPoller()
  {
    initalize(lbuffer);
    initalize(rbuffer);
  }
  public static int getIntensity()
  {
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
  
  public static  int lprevIntensity;
  public static int lintensity;
  public static  int rprevIntensity;
  public static int rintensity;
  public static int ldiff;
  public static int rdiff;
  //remove just used for debuging
  public static boolean t = false;
  public static int signedSquare(int num)
  {
    return (int) (num * num * Math.signum(num));
  }
  /**
   * Performs light polling
   * Assumes it starts facing 0 degrees in the corner block
   */
  public void timedOut() {
    // if light sensing returns to 0 to prevent multiple line readings.
    lprevIntensity = lintensity;
    rprevIntensity = rintensity;
 //   System.out.println(lintensity + ", " + lprevIntensity);
    boolean steadyState = true;
      calculateIntensity();
      ldiff = lintensity - lprevIntensity;
      rdiff = rintensity - rprevIntensity;
      if(t == true)
        System.out.println(((int) (odometer.getXYT()[1] * 100)) / 100.0 + ", " + signedSquare(ldiff)
         +  ",  " + ldiff);
      
  }
  /*
   * returns RMS of an int array
   * 
   */
  public static int average(int[] arr) {
 /*   double sum = 0;
    for (int i = 0; i < arr.length; i++)
      sum = sum + arr[i] * arr[i] * Math.signum(arr[i]);

    return (int) Math.sqrt((sum / arr.length));*/
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
  public static void initalize(int[] buffer) {

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
  * @param intensity
  * @param buffer
  */
  public static void shiftBuffer(int intensity, int[] buffer)
  {
    for (int i = 0; i < buffer.length - 1; i++) {
      buffer[i] = buffer[i + 1];
    }
    buffer[buffer.length - 1] = intensity;
  }
}
